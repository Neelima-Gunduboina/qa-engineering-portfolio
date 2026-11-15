#!/usr/bin/env ts-node
/**
 * triage.ts — LLM-powered test failure classification.
 *
 * Reads the Playwright JSON results file (test-results/results.json),
 * finds failed tests, and asks the LLM to classify each failure into:
 *
 * - REAL_BUG:       Application behaviour differs from expected spec
 * - LOCATOR_DRIFT:  Selector no longer matches — HTML/CSS changed
 * - FLAKY:          Timing/network issue — test may pass on re-run
 * - ENV_ISSUE:      Infrastructure problem (app down, wrong URL, auth expired)
 * - TEST_BUG:       Incorrect assertion or test logic problem
 *
 * Output:
 * - Prints a markdown summary to stdout
 * - In CI: writes to $GITHUB_STEP_SUMMARY for the Actions job summary
 * - Optional: posts as a PR comment via GitHub API
 *
 * Usage:
 *   npx ts-node scripts/triage.ts
 *   npx ts-node scripts/triage.ts --results path/to/results.json
 */

import * as fs from 'fs';
import * as path from 'path';
import { chat } from '../utils/llm-client';
import { log } from '../utils/logger';
import dotenv from 'dotenv';
dotenv.config();

interface PlaywrightTestResult {
  status: string;
  title: string;
  error?: { message: string; snippet?: string; stack?: string };
  retry: number;
  duration: number;
  location?: { file: string; line: number };
}

interface PlaywrightSuite {
  title: string;
  specs?: PlaywrightTestResult[];
  suites?: PlaywrightSuite[];
}

interface PlaywrightResults {
  stats: {
    expected: number;
    unexpected: number;
    skipped: number;
    flaky: number;
  };
  suites: PlaywrightSuite[];
}

type FailureCategory =
  | 'REAL_BUG'
  | 'LOCATOR_DRIFT'
  | 'FLAKY'
  | 'ENV_ISSUE'
  | 'TEST_BUG'
  | 'UNKNOWN';

interface TriageResult {
  testTitle: string;
  file: string;
  errorMessage: string;
  category: FailureCategory;
  explanation: string;
  suggestedAction: string;
}

const SYSTEM_PROMPT = `You are a senior QA engineer and test automation expert.
Analyse the following failed Playwright test and classify the failure.

Categories:
- REAL_BUG: The application behaviour changed or is wrong — a real defect
- LOCATOR_DRIFT: A CSS/ARIA selector no longer matches — HTML structure changed
- FLAKY: Timing, network, or race condition — test may pass on retry
- ENV_ISSUE: Infrastructure problem — app is down, wrong env, auth expired
- TEST_BUG: Incorrect assertion, wrong test logic, or outdated expected value

Respond in this exact JSON format (no markdown, just JSON):
{
  "category": "REAL_BUG|LOCATOR_DRIFT|FLAKY|ENV_ISSUE|TEST_BUG|UNKNOWN",
  "explanation": "One sentence explaining what went wrong",
  "suggestedAction": "One sentence recommending what to do next"
}`;

function collectFailures(suites: PlaywrightSuite[], file = ''): PlaywrightTestResult[] {
  const failures: PlaywrightTestResult[] = [];
  for (const suite of suites) {
    const suiteFile = suite.title || file;
    if (suite.specs) {
      for (const spec of suite.specs) {
        if (spec.status === 'unexpected' || spec.status === 'failed') {
          failures.push({ ...spec, location: spec.location ?? { file: suiteFile, line: 0 } });
        }
      }
    }
    if (suite.suites) {
      failures.push(...collectFailures(suite.suites, suiteFile));
    }
  }
  return failures;
}

async function triageFailure(failure: PlaywrightTestResult): Promise<TriageResult> {
  const errorText = failure.error?.message ?? 'No error message';
  const snippet = failure.error?.snippet ?? '';

  const prompt = `Test: ${failure.title}
File: ${failure.location?.file ?? 'unknown'}:${failure.location?.line ?? 0}
Retries: ${failure.retry}
Duration: ${failure.duration}ms

Error:
${errorText}

${snippet ? `Code snippet:\n${snippet}` : ''}`;

  const response = await chat(prompt, SYSTEM_PROMPT);

  if (!response) {
    return {
      testTitle: failure.title,
      file: `${failure.location?.file ?? 'unknown'}:${failure.location?.line ?? 0}`,
      errorMessage: errorText,
      category: 'UNKNOWN',
      explanation: 'LLM unavailable — set ANTHROPIC_API_KEY for AI triage',
      suggestedAction: 'Review error message manually',
    };
  }

  try {
    const parsed = JSON.parse(response.content) as {
      category: FailureCategory;
      explanation: string;
      suggestedAction: string;
    };
    return {
      testTitle: failure.title,
      file: `${failure.location?.file ?? 'unknown'}:${failure.location?.line ?? 0}`,
      errorMessage: errorText.slice(0, 300),
      category: parsed.category,
      explanation: parsed.explanation,
      suggestedAction: parsed.suggestedAction,
    };
  } catch {
    return {
      testTitle: failure.title,
      file: `${failure.location?.file ?? 'unknown'}:${failure.location?.line ?? 0}`,
      errorMessage: errorText.slice(0, 300),
      category: 'UNKNOWN',
      explanation: response.content.slice(0, 200),
      suggestedAction: 'Review error manually',
    };
  }
}

const CATEGORY_EMOJI: Record<FailureCategory, string> = {
  REAL_BUG: '🐛',
  LOCATOR_DRIFT: '🎯',
  FLAKY: '🌊',
  ENV_ISSUE: '🔧',
  TEST_BUG: '📝',
  UNKNOWN: '❓',
};

function buildMarkdownSummary(results: TriageResult[], stats: PlaywrightResults['stats']): string {
  const lines: string[] = [
    '# 🤖 AI Failure Triage Report',
    '',
    `> Generated by LLM-powered triage | ${new Date().toISOString()}`,
    '',
    '## Test Run Summary',
    '',
    `| Metric | Count |`,
    `|--------|-------|`,
    `| ✅ Passed | ${stats.expected} |`,
    `| ❌ Failed | ${stats.unexpected} |`,
    `| ⏭️ Skipped | ${stats.skipped} |`,
    `| 🌊 Flaky | ${stats.flaky} |`,
    '',
  ];

  if (results.length === 0) {
    lines.push('✅ **No failures to triage.**');
    return lines.join('\n');
  }

  lines.push('## Failure Analysis', '');

  for (const r of results) {
    const emoji = CATEGORY_EMOJI[r.category];
    lines.push(
      `### ${emoji} \`${r.testTitle}\``,
      '',
      `**Category:** \`${r.category}\`  `,
      `**File:** \`${r.file}\`  `,
      '',
      `**What happened:** ${r.explanation}`,
      '',
      `**Suggested action:** ${r.suggestedAction}`,
      '',
      `<details><summary>Error message</summary>`,
      '',
      '```',
      r.errorMessage,
      '```',
      '',
      '</details>',
      '',
      '---',
      '',
    );
  }

  // Category breakdown
  const counts = results.reduce<Record<string, number>>((acc, r) => {
    acc[r.category] = (acc[r.category] ?? 0) + 1;
    return acc;
  }, {});

  lines.push('## Category Breakdown', '');
  for (const [cat, count] of Object.entries(counts)) {
    lines.push(`- ${CATEGORY_EMOJI[cat as FailureCategory]} **${cat}**: ${count}`);
  }

  return lines.join('\n');
}

async function main(): Promise<void> {
  const args = process.argv.slice(2);
  const resultsIndex = args.indexOf('--results');
  const resultsPath = resultsIndex !== -1
    ? args[resultsIndex + 1]
    : path.resolve(process.cwd(), 'test-results/results.json');

  if (!fs.existsSync(resultsPath)) {
    log.warn(`[triage] Results file not found: ${resultsPath}`);
    log.warn('[triage] Run playwright test first to generate results.json');
    process.exit(0); // Non-fatal — CI should still pass setup
  }

  const rawResults: PlaywrightResults = JSON.parse(
    fs.readFileSync(resultsPath, 'utf-8'),
  );

  const failures = collectFailures(rawResults.suites);

  if (failures.length === 0) {
    log.info('[triage] No failures found — all tests passed!');
    const summary = buildMarkdownSummary([], rawResults.stats);
    writeSummary(summary);
    return;
  }

  log.info(`[triage] Triaging ${failures.length} failure(s)...`);

  const triageResults = await Promise.all(failures.map(triageFailure));

  const summary = buildMarkdownSummary(triageResults, rawResults.stats);
  console.log(summary);
  writeSummary(summary);

  // Save as JSON for programmatic use
  const jsonOutput = path.resolve(process.cwd(), 'test-results/triage.json');
  fs.mkdirSync(path.dirname(jsonOutput), { recursive: true });
  fs.writeFileSync(jsonOutput, JSON.stringify(triageResults, null, 2));
  log.info(`[triage] Results saved to ${jsonOutput}`);
}

function writeSummary(content: string): void {
  const summaryFile = process.env.GITHUB_STEP_SUMMARY;
  if (summaryFile) {
    try {
      fs.appendFileSync(summaryFile, content + '\n');
      log.info('[triage] Written to GitHub Actions job summary');
    } catch (err) {
      log.warn(`[triage] Could not write to GITHUB_STEP_SUMMARY: ${String(err)}`);
    }
  }
}

main().catch((err) => {
  log.error(`[triage] Fatal: ${String(err)}`);
  process.exit(1);
});
