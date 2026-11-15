# AI in QA — Feature Guide

This document explains every GenAI feature in the framework: **what it does**,
**why it matters**, and — critically — the **human-in-the-loop safeguard** that
ensures AI assistance enhances rather than replaces QA judgement.

---

## 1. Self-Healing Locators (`utils/self-heal.ts`)

### What it does
When a Playwright locator fails to find an element, instead of immediately failing
the test, the self-heal utility:

1. Captures the current page's ARIA snapshot (YAML)
2. Sends the snapshot + the intent of the element (e.g., *"the Log In submit button"*) to the LLM
3. Accepts the suggested selector **only if it matches exactly one element**
4. Logs the heal (old → new) and caches it locally in `.locator-cache.json` (gitignored)
5. Returns the healed Playwright `Locator`

The resolver is injectable, so `tests/ui/self-heal.spec.ts` checks the whole flow
deterministically with a fake resolver; one extra test calls the real LLM when
`ANTHROPIC_API_KEY` is set.

### Why it matters
In agile banking projects, the frontend changes frequently (rebranding, accessibility
improvements, framework upgrades). Locator maintenance is one of the biggest ongoing
costs in test automation. Self-healing reduces false failures from selector drift and
surfaces *actual* application bugs more clearly.

### Human-in-the-loop safeguard
- **Off in CI by default**: unless `SELF_HEAL=on` is set, a broken locator fails the pipeline instead of being patched
- **All heals are logged** with before/after selectors — nothing happens silently
- **The cache is local only**: a heal is never the fix. A human checks the suggested selector and updates the page object
- **Ambiguous suggestions are rejected**: a selector matching zero or several elements is never used

---

## 2. AI Test Generation (`scripts/generate-tests.ts`)

### What it does
Takes a requirements Markdown file (user story + acceptance criteria) and asks the LLM to generate a complete Playwright TypeScript spec covering each AC as a separate test case.

```bash
npx ts-node scripts/generate-tests.ts --req requirements/REQ-001-login.md
```

Output is placed in `tests/ui/ai-generated/` with a prominent review checklist header.

### Why it matters
Writing test cases from requirements is time-consuming. AI generation can produce
a comprehensive first draft in seconds, dramatically reducing the time from
"requirement signed off" to "test in CI". In practice this is most useful for:

- Covering edge cases a human might overlook
- Generating negative test scaffolding
- Rapid onboarding: new features get test drafts immediately

### Human-in-the-loop safeguard
AI-generated specs are **never** placed directly in `tests/ui/`. They go to
`tests/ui/ai-generated/` and carry this checklist:

```
REVIEW CHECKLIST:
[ ] Selectors verified against live application
[ ] Test data values are valid for the environment
[ ] Negative test assertions match actual error messages
[ ] Each test runs in isolation (no shared mutable state)
[ ] Tests moved to tests/ui/ after review and approval
```

The LLM generates *plausible-looking* tests that may have wrong selectors,
incorrect error message text, or assert on elements that don't exist. A QA
engineer's review is the mandatory quality gate before any generated test runs in CI.

**The value is in the acceleration, not automation of QA judgement.**

---

## 3. LLM Failure Triage (`scripts/triage.ts`)

### What it does
After a test run, reads `test-results/results.json` and for each failed test:
1. Sends the test name, error message, and code snippet to the LLM
2. Receives a structured classification:
   - `REAL_BUG` — application defect, raise a bug ticket
   - `LOCATOR_DRIFT` — HTML changed, update selectors
   - `FLAKY` — timing issue, consider retry or async fix
   - `ENV_ISSUE` — infrastructure problem, check logs
   - `TEST_BUG` — incorrect assertion, fix the test

3. Writes a markdown report to `$GITHUB_STEP_SUMMARY` (visible in the GitHub Actions UI)

### Why it matters
When a CI run fails with 10+ tests, manual triage — reading each error, looking
at traces, determining root cause — can take an engineer 30-60 minutes. AI triage
provides an instant first-pass classification that helps the team prioritise:

- `REAL_BUG` items go into the sprint backlog immediately
- `FLAKY` items are quarantined and investigated separately
- `ENV_ISSUE` items get escalated to infrastructure, not QA

### Human-in-the-loop safeguard
- Triage output is labelled **"AI-assisted triage — verify before acting"**
- `REAL_BUG` classifications still require a human to reproduce, scope, and raise
- The raw error and trace link are always included so engineers can verify the LLM's reasoning
- LLM classifications are probabilistic — a `FLAKY` finding might be masking a real timing bug in the app

---

## 4. Playwright MCP Integration

### What it does
Playwright MCP (Model Context Protocol) connects Claude directly to a live browser
via the accessibility tree. Instead of writing code, you give Claude natural-language
instructions:

> "Navigate to the Transfer Funds page, select the second account in the From dropdown,
> enter £50, and tell me what confirmation message appears."

Claude executes each step, observes the result, and reports back.

### Why it matters
- **Exploratory testing** at scale: describe a scenario, Claude executes it
- **Accessibility-first**: MCP uses the ARIA tree — same view screen readers use
- **Requirement validation**: POs can describe a flow in plain English and get
  immediate feedback on whether it works as expected
- **Demonstrated in interviews**: live AI-driven test execution is compelling evidence
  of modern QA tooling knowledge

### Human-in-the-loop safeguard
MCP-driven interactions are **not** part of the deterministic CI suite. They are:
- Exploratory and session-based
- Non-reproducible without replaying the exact Claude conversation
- Suitable for discovery, not regression

See [mcp-setup.md](mcp-setup.md) for configuration and usage.

---

## 5. Visual Regression (`tests/visual/`)

### What it does
Playwright's built-in `toHaveScreenshot()` captures pixel-accurate screenshots of key
pages and compares them against committed baselines. Differences above a configurable
threshold fail the test.

Dynamic content (account balances, account IDs) is masked out before comparison
so only structural/layout changes trigger failures.

### Why it matters
In banking portals, visual changes can have regulatory significance:
- Required disclosures must remain visible
- Button placement affects how quickly users can initiate transfers (usability + compliance)
- Rebranding exercises must not inadvertently remove mandated elements

Visual tests catch these regressions when functional tests would pass.

### Human-in-the-loop safeguard
- Run `npm run update-snapshots` **deliberately** after intentional UI changes
- Snapshot updates appear as binary diffs in PRs — a visual reviewer must approve them
- `maxDiffPixelRatio: 0.02` (2%) allows minor rendering differences without noise

---

## 6. Accessibility Testing (`tests/a11y/`)

### What it does
Uses `@axe-core/playwright` to run WCAG 2.1 AA checks on key pages. Critical and
serious violations fail the test. Moderate and minor violations are logged as
warnings for informational review.

### Why it matters
UK financial services are legally required to meet WCAG 2.1 AA (Equality Act 2010,
FCA Consumer Duty). Integrating axe-core into CI
means accessibility regressions are caught at the PR stage, not in UAT.

---

## GenAI Maturity Model — Where This Framework Sits

```
Level 1: Manual testing
Level 2: Record-and-playback automation
Level 3: Code-based automation (Selenium, Cypress)          ← Industry standard 2020
Level 4: Playwright + POM + CI + visual + a11y              ← This framework's baseline
Level 5: AI-assisted (self-heal, generation, triage, MCP)   ← This framework's differentiator
Level 6: Autonomous AI agents running full test cycles      ← Research territory (2025+)
```

This framework is intentionally positioned at **Level 5** — a credible, production-ready
application of AI tooling with clear human oversight at every step.
