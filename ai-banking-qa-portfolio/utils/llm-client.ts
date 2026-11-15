/**
 * llm-client.ts
 *
 * Thin wrapper around the Anthropic SDK used by every GenAI feature in this
 * framework: the self-healing resolver, AI test generation, and failure triage.
 *
 * Design decisions:
 * - Single shared instance (module-level singleton) keeps configuration DRY.
 * - Graceful degradation: when ANTHROPIC_API_KEY is absent, every public
 *   method resolves to `null` rather than throwing so the core test suite
 *   runs without any API credentials.
 * - Configurable model via LLM_MODEL env var so the team can swap models
 *   without touching source code.
 */

import Anthropic from '@anthropic-ai/sdk';
import { log } from './logger';

const API_KEY = process.env.ANTHROPIC_API_KEY;
const MODEL = process.env.LLM_MODEL ?? 'claude-sonnet-5-5';
const TIMEOUT_MS = 60_000;

let _client: Anthropic | null = null;

function getClient(): Anthropic | null {
  if (!API_KEY) {
    log.warn('[llm-client] ANTHROPIC_API_KEY not set — GenAI features disabled.');
    return null;
  }
  if (!_client) {
    _client = new Anthropic({ apiKey: API_KEY });
  }
  return _client;
}

export interface LlmResponse {
  content: string;
  model: string;
  inputTokens: number;
  outputTokens: number;
}

/**
 * Send a single-turn prompt to the configured Claude model.
 * Returns null when no API key is configured or the call times out.
 */
export async function chat(
  prompt: string,
  systemPrompt?: string,
): Promise<LlmResponse | null> {
  const client = getClient();
  if (!client) return null;

  try {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), TIMEOUT_MS);

    const response = await client.messages.create(
      {
        model: MODEL,
        max_tokens: 4096,
        system: systemPrompt,
        messages: [{ role: 'user', content: prompt }],
      },
      { signal: controller.signal },
    );

    clearTimeout(timer);

    const text = response.content
      .filter((b) => b.type === 'text')
      .map((b) => (b as { type: 'text'; text: string }).text)
      .join('');

    return {
      content: text,
      model: response.model,
      inputTokens: response.usage.input_tokens,
      outputTokens: response.usage.output_tokens,
    };
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : String(err);
    log.error(`[llm-client] Request failed: ${msg}`);
    return null;
  }
}

export { MODEL as llmModel };
