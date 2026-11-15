/**
 * self-heal.ts — AI-assisted self-healing locator utility (opt-in).
 *
 * HOW IT WORKS
 * ─────────────
 * 1. A locator is registered with a human-readable intent
 *    (e.g. "the Log In submit button").
 * 2. `locate()` tries a previously healed selector from the local cache,
 *    then the original selector.
 * 3. If neither matches, a resolver is asked for a replacement. The default
 *    resolver sends the page's ARIA snapshot and the intent to the LLM.
 * 4. A healed selector is only accepted if it matches exactly one element.
 * 5. Accepted heals are logged (old → new) and cached in `.locator-cache.json`
 *    (gitignored, local to the machine).
 *
 * HUMAN-IN-THE-LOOP SAFEGUARDS
 * ─────────────────────────────
 * - Healing is OFF in CI unless SELF_HEAL=on is set explicitly, so a broken
 *   locator fails the pipeline instead of being silently patched.
 * - The cache is a local convenience, not a fix. Every heal is logged, and a
 *   human must copy the reviewed selector into the page object.
 * - The resolver is injectable, so the healing flow is tested deterministically
 *   without an API key (see tests/ui/self-heal.spec.ts).
 */

import * as fs from 'fs';
import * as path from 'path';
import { Locator, Page } from '@playwright/test';
import { chat } from './llm-client';
import { log } from './logger';

const DEFAULT_CACHE_FILE = path.resolve(process.cwd(), '.locator-cache.json');

/** Returns a replacement selector, or null when it cannot find the element. */
export type SelectorResolver = (
  page: Page,
  intent: string,
  brokenSelector: string,
) => Promise<string | null>;

export interface SelfHealOptions {
  /** Defaults to the LLM resolver. Inject a fake one in tests. */
  resolver?: SelectorResolver;
  /** Defaults to .locator-cache.json in the working directory. */
  cacheFile?: string;
}

type LocatorCache = Record<string, string>;

function loadCache(file: string): LocatorCache {
  try {
    if (fs.existsSync(file)) {
      return JSON.parse(fs.readFileSync(file, 'utf-8')) as LocatorCache;
    }
  } catch {
    log.warn(`[self-heal] Ignoring unreadable cache file ${file}`);
  }
  return {};
}

function saveCache(file: string, cache: LocatorCache): void {
  try {
    fs.writeFileSync(file, JSON.stringify(cache, null, 2), 'utf-8');
  } catch (err) {
    log.warn(`[self-heal] Could not persist cache: ${String(err)}`);
  }
}

/** Healing runs locally by default; in CI it must be switched on explicitly. */
export function healingEnabled(): boolean {
  if (process.env.SELF_HEAL === 'off') return false;
  if (process.env.CI) return process.env.SELF_HEAL === 'on';
  return true;
}

const SYSTEM_PROMPT = `You are a Playwright test automation expert.
Given an accessibility snapshot of a web page and a description of an element,
return a single CSS selector that uniquely identifies that element.

Rules:
- Prefer id, name, aria-label or data-testid attributes over class or position.
- Return ONLY the selector on a single line — no explanation, no code block.
- If you cannot identify the element with confidence, return: UNABLE_TO_HEAL`;

/** Default resolver: asks the LLM, using the page's ARIA snapshot as context. */
export const llmResolver: SelectorResolver = async (page, intent, brokenSelector) => {
  let snapshot: string;
  try {
    snapshot = await page.locator('body').ariaSnapshot();
  } catch {
    snapshot = await page.content();
  }

  const prompt = `Page accessibility snapshot:
\`\`\`yaml
${snapshot.slice(0, 8000)}
\`\`\`

Element: "${intent}"
Previous selector (now broken): "${brokenSelector}"

Return the best new selector for this element.`;

  const response = await chat(prompt, SYSTEM_PROMPT);
  if (!response || response.content.includes('UNABLE_TO_HEAL')) return null;
  return response.content.trim().replace(/^['"`]|['"`]$/g, '');
};

export class SelfHealingLocator {
  private readonly resolver: SelectorResolver;
  private readonly cacheFile: string;
  private readonly cacheKey: string;
  private cache: LocatorCache;

  /** The selector the last locate() call settled on (for logging and assertions). */
  resolvedSelector: string;

  constructor(
    private readonly page: Page,
    private readonly originalSelector: string,
    private readonly intent: string,
    options: SelfHealOptions = {},
  ) {
    this.resolver = options.resolver ?? llmResolver;
    this.cacheFile = options.cacheFile ?? DEFAULT_CACHE_FILE;
    this.cache = loadCache(this.cacheFile);
    this.cacheKey = `${intent}::${originalSelector}`;
    this.resolvedSelector = originalSelector;
  }

  /** Resolves a locator: cached heal → original selector → resolver. */
  async locate(): Promise<Locator> {
    const cached = this.cache[this.cacheKey];
    if (cached && (await this.matchesExactlyOne(cached))) {
      log.debug(`[self-heal] Using cached selector for "${this.intent}": ${cached}`);
      return this.use(cached);
    }

    if ((await this.page.locator(this.originalSelector).count()) > 0) {
      return this.use(this.originalSelector);
    }

    if (!healingEnabled()) {
      log.warn(`[self-heal] "${this.intent}" not found and healing is disabled — failing on original selector`);
      return this.use(this.originalSelector);
    }

    log.info(`[self-heal] Healing "${this.intent}" (was: ${this.originalSelector})`);
    const healed = await this.resolver(this.page, this.intent, this.originalSelector);

    if (!healed || !(await this.matchesExactlyOne(healed))) {
      log.warn(`[self-heal] Rejected heal for "${this.intent}": ${healed ?? 'no suggestion'}`);
      return this.use(this.originalSelector);
    }

    log.warn(`[self-heal] HEALED "${this.intent}": ${this.originalSelector} → ${healed} — update the page object`);
    this.cache[this.cacheKey] = healed;
    saveCache(this.cacheFile, this.cache);
    return this.use(healed);
  }

  async click(): Promise<void> {
    await (await this.locate()).click();
  }

  async fill(value: string): Promise<void> {
    await (await this.locate()).fill(value);
  }

  private async matchesExactlyOne(selector: string): Promise<boolean> {
    try {
      return (await this.page.locator(selector).count()) === 1;
    } catch {
      return false; // invalid selector syntax from the resolver
    }
  }

  private use(selector: string): Locator {
    this.resolvedSelector = selector;
    return this.page.locator(selector);
  }
}

/** Factory helper for page objects and tests. */
export function selfHeal(
  page: Page,
  selector: string,
  intent: string,
  options?: SelfHealOptions,
): SelfHealingLocator {
  return new SelfHealingLocator(page, selector, intent, options);
}
