#!/usr/bin/env ts-node
/**
 * generate-tests.ts — AI-powered test case generator.
 *
 * Takes a requirements Markdown file and asks the LLM to generate a draft
 * Playwright TypeScript spec. The output is written to tests/ui/ai-generated/
 * and clearly labelled so reviewers know it requires human validation.
 *
 * HUMAN-IN-THE-LOOP SAFEGUARD:
 * Generated specs are placed in a dedicated directory and contain a prominent
 * comment block. Before merging, a QA engineer must:
 * 1. Review each test for correctness against the actual application
 * 2. Verify selectors match real DOM elements
 * 3. Remove or adjust tests that don't reflect actual behaviour
 * 4. Move reviewed tests to tests/ui/ once satisfied
 *
 * Usage:
 *   npx ts-node scripts/generate-tests.ts --req requirements/REQ-001-login.md
 *   npx ts-node scripts/generate-tests.ts --all
 */

import * as fs from 'fs';
import * as path from 'path';
import { chat } from '../utils/llm-client';
import { log } from '../utils/logger';
import dotenv from 'dotenv';
dotenv.config();

const OUTPUT_DIR = path.resolve(process.cwd(), 'tests/ui/ai-generated');
const REQUIREMENTS_DIR = path.resolve(process.cwd(), 'requirements');

const SYSTEM_PROMPT = `You are a senior SDET specialising in Playwright TypeScript test automation for banking applications.

Given a requirements document with user stories and acceptance criteria, generate a complete Playwright test spec file.

Rules:
1. Import { test, expect } from '../../fixtures' (not @playwright/test directly)
2. Use Page Object pattern — reference existing POMs: LoginPage, DashboardPage, AccountsPage, TransferFundsPage, BillPaymentPage
3. Use Playwright web-first assertions (expect(...).toBeVisible(), toHaveText(), etc.)
4. NO hardcoded waits (no setTimeout/sleep) — use Playwright auto-waiting
5. Test IDs must match the requirement AC numbers: TC-[REQ-ID]-[AC-NUMBER]
6. Add a prominent comment block at the top: "AI-GENERATED — REQUIRES HUMAN REVIEW"
7. Each test should have a comment explaining what it validates and why
8. Output ONLY the TypeScript code — no markdown fences, no explanation

Imports available:
- Pages: LoginPage, DashboardPage, AccountsPage, TransferFundsPage, BillPaymentPage
- Fixtures: test, expect from '../../fixtures'
- Test data: use { testData } fixture
`;

async function generateTestsForRequirement(reqFile: string): Promise<void> {
  const reqPath = path.resolve(process.cwd(), reqFile);

  if (!fs.existsSync(reqPath)) {
    log.error(`Requirements file not found: ${reqPath}`);
    process.exit(1);
  }

  const requirementContent = fs.readFileSync(reqPath, 'utf-8');
  const reqBaseName = path.basename(reqFile, '.md').toLowerCase();
  const outputFile = path.join(OUTPUT_DIR, `${reqBaseName}.ai-generated.spec.ts`);

  log.info(`Generating tests for: ${reqFile}`);

  const prompt = `Generate a Playwright TypeScript test spec for the following requirements:

${requirementContent}

The spec should cover all acceptance criteria as individual test cases.`;

  const response = await chat(prompt, SYSTEM_PROMPT);

  if (!response) {
    log.warn(
      `[generate-tests] LLM unavailable — skipping generation for ${reqFile}. ` +
      'Set ANTHROPIC_API_KEY to enable AI test generation.',
    );
    return;
  }

  // Ensure output directory exists
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });

  // Prepend a clear human-review header
  const fileContent = `// =============================================================================
// AI-GENERATED TEST SPEC — REQUIRES HUMAN REVIEW BEFORE USE IN CI
// =============================================================================
// Source requirement: ${reqFile}
// Generated: ${new Date().toISOString()}
// Model: ${response.model}
// Tokens: ${response.inputTokens} in / ${response.outputTokens} out
//
// REVIEW CHECKLIST (complete before removing this header):
// [ ] Selectors verified against live application
// [ ] Test data values are valid for the environment
// [ ] Negative test assertions match actual error messages
// [ ] Each test runs in isolation (no shared mutable state)
// [ ] Tests moved to tests/ui/ after review and approval
// =============================================================================

${response.content}
`;

  fs.writeFileSync(outputFile, fileContent, 'utf-8');
  log.info(`Generated: ${outputFile} (${response.outputTokens} tokens)`);
}

async function main(): Promise<void> {
  const args = process.argv.slice(2);

  if (args.includes('--all')) {
    // Generate for all requirement files
    const files = fs
      .readdirSync(REQUIREMENTS_DIR)
      .filter((f) => f.endsWith('.md'))
      .map((f) => `requirements/${f}`);

    if (files.length === 0) {
      log.error('No .md files found in requirements/');
      process.exit(1);
    }

    for (const file of files) {
      await generateTestsForRequirement(file);
    }
  } else {
    const reqIndex = args.indexOf('--req');
    if (reqIndex === -1 || !args[reqIndex + 1]) {
      console.error('Usage: ts-node scripts/generate-tests.ts --req <path> | --all');
      process.exit(1);
    }
    await generateTestsForRequirement(args[reqIndex + 1]);
  }
}

main().catch((err) => {
  log.error(`Fatal: ${String(err)}`);
  process.exit(1);
});
