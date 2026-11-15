/**
 * Self-healing locator — proves the healing flow in utils/self-heal.ts.
 *
 * Each test starts from a deliberately broken selector for the ParaBank
 * "Log In" button (#login-btn-v2 does not exist on the page).
 *
 * The first four tests inject a fake resolver, so they are deterministic and
 * need no API key. The last test calls the real LLM and is skipped unless
 * ANTHROPIC_API_KEY is set.
 */

import { test, expect } from '../../fixtures';
import { selfHeal, SelectorResolver } from '../../utils/self-heal';

const BROKEN = '#login-btn-v2';
const REAL = 'input[type="submit"][value="Log In"]';
const INTENT = 'the Log In submit button on the login form';

test.describe('Self-healing locator', () => {
  test.use({ storageState: { cookies: [], origins: [] } });

  let savedSelfHeal: string | undefined;

  test.beforeEach(async ({ loginPage }) => {
    savedSelfHeal = process.env.SELF_HEAL;
    process.env.SELF_HEAL = 'on';
    await loginPage.goto();
  });

  test.afterEach(() => {
    if (savedSelfHeal === undefined) delete process.env.SELF_HEAL;
    else process.env.SELF_HEAL = savedSelfHeal;
  });

  test('heals a broken selector and caches the result', async ({ page }, testInfo) => {
    const calls: string[] = [];
    const resolver: SelectorResolver = async (_page, _intent, broken) => {
      calls.push(broken);
      return REAL;
    };
    const cacheFile = testInfo.outputPath('locator-cache.json');

    const button = selfHeal(page, BROKEN, INTENT, { resolver, cacheFile });
    await expect(await button.locate()).toHaveAttribute('value', 'Log In');
    expect(button.resolvedSelector).toBe(REAL);
    expect(calls).toEqual([BROKEN]);

    // A second locator for the same element uses the cache, not the resolver
    const again = selfHeal(page, BROKEN, INTENT, { resolver, cacheFile });
    await again.locate();
    expect(again.resolvedSelector).toBe(REAL);
    expect(calls).toHaveLength(1);
  });

  test('does not call the resolver when the original selector works', async ({ page }, testInfo) => {
    let called = false;
    const resolver: SelectorResolver = async () => {
      called = true;
      return null;
    };

    const button = selfHeal(page, REAL, INTENT, { resolver, cacheFile: testInfo.outputPath('c.json') });
    await button.locate();

    expect(button.resolvedSelector).toBe(REAL);
    expect(called).toBe(false);
  });

  test('rejects a heal that matches more than one element', async ({ page }, testInfo) => {
    const resolver: SelectorResolver = async () => 'input'; // matches every input on the page

    const button = selfHeal(page, BROKEN, INTENT, { resolver, cacheFile: testInfo.outputPath('c.json') });
    await button.locate();

    expect(button.resolvedSelector).toBe(BROKEN);
  });

  test('does not heal when healing is disabled', async ({ page }, testInfo) => {
    process.env.SELF_HEAL = 'off';
    let called = false;
    const resolver: SelectorResolver = async () => {
      called = true;
      return REAL;
    };

    const button = selfHeal(page, BROKEN, INTENT, { resolver, cacheFile: testInfo.outputPath('c.json') });
    await button.locate();

    expect(called).toBe(false);
    expect(button.resolvedSelector).toBe(BROKEN);
  });

  test('LLM heals the broken selector (live)', async ({ page }, testInfo) => {
    test.skip(!process.env.ANTHROPIC_API_KEY, 'Needs ANTHROPIC_API_KEY');
    test.setTimeout(90_000);

    const button = selfHeal(page, BROKEN, INTENT, { cacheFile: testInfo.outputPath('c.json') });
    const healed = await button.locate();

    testInfo.annotations.push({ type: 'healed-selector', description: button.resolvedSelector });
    expect(button.resolvedSelector).not.toBe(BROKEN);
    await expect(healed).toHaveAttribute('value', 'Log In');
  });
});
