# Repository Improvement Backlog

Status from the Stage 1 audit and the fixes pushed on 15 Nov 2026. I do the 🧑‍💻 items myself, as learning tasks; the 🤝 items I can pair on with my mentor.
**Rule:** nothing is pushed to GitHub without my approval.

## qa-portfolio-parabank (Java) — strongest repo, about 8/10

| Area | Finding | Fix | Week | Who |
|---|---|---|---|---|
| Framework design | DB tests test my own `DBUtils` against my own H2 schema, not ParaBank | Run ParaBank in Docker and validate its real database after UI/API transfers; keep the H2 tests as a "SQL reconciliation pattern" demo, clearly labelled | 4 | 🧑‍💻 |
| Cross-browser | Only Chrome is supported (`BaseTest` throws for others) | Add a `DriverFactory` with Chrome/Firefox/Edge, selected by `-Dbrowser=` | 5 | 🧑‍💻 |
| Flakiness | `RetryOnce` could hide real flakiness if used widely | Keep it on E2E only; log retries in the Extent report; add a "flaky tests" note to README | 5 | 🧑‍💻 |
| API tests | No JSON schema validation | Add `matchesJsonSchemaInClasspath` checks for account and customer responses | 3 | 🧑‍💻 |
| Assertions | Several `assertTrue(statusCode >= 400)` checks | Assert the exact status and error body where ParaBank is consistent | 3 | 🧑‍💻 |
| Test data | Passwords such as `Test@1234` in code | Move to `testdata/*.json` or config; document that they're throwaway demo values | 3 | 🧑‍💻 |
| Java version | Java 25, while UK adverts mostly ask for 17/21 | Keep 25 but add Java 21 to the CI matrix to show compatibility | 5 | 🧑‍💻 |
| Reports | No published report | Publish the Extent report via GitHub Pages from CI | 5 | 🤝 |
| AI | None | Project 7 (AI API test generator), after week 12 | — | 🤝 |

## ai-banking-qa-portfolio (Playwright/TS) — now about 7/10 after the fixes

| Area | Finding | Fix | Week | Who |
|---|---|---|---|---|
| ✅ Done | Self-healing was not wired up; unused `multiTurnChat`; backdated checklist; unverified experience claims | Fixed and pushed (`8cd4a4e`) | — | 🤝 |
| Understanding | Much of this repo was written quickly; can I explain every file? | Week 6: walk through `fixtures/`, `auth.setup.ts`, `playwright.config.ts` and CI line by line, then rebuild one spec from scratch | 6 | 🧑‍💻 |
| CI cost | Installs 3 browsers in each of 4 shards | Install only what each shard needs, cache browsers | 6 | 🧑‍💻 |
| Test data | `playwright/.auth/credentials.json` written at runtime | Confirm it's gitignored (it is) and say so in README | 6 | 🧑‍💻 |
| Triage | `triage.ts` classifies in one call with free-text output | Use structured output; feeds Project 3 | 11 | 🤝 |
| Dashboard | No trend view | Project 8 (after week 12) | — | 🤝 |

## neelima-my-learning — honest learning repo

| Area | Finding | Fix | Week | Who |
|---|---|---|---|---|
| ✅ Done | Defect count out of date; no skills-check area | Fixed and pushed (`c2cd2a7`) | — | 🤝 |
| Skills check | Exercises 2–5 not written yet | Add each exercise as I reach it | 1–2 | 🧑‍💻 |
| Katas | No daily practice record | Add `katas/` with one solved problem + test per day | 1–12 | 🧑‍💻 |

## Neelima-Gunduboina (GitHub profile)

| Area | Finding | Fix | Week |
|---|---|---|---|
| Positioning | Says "actively building automation skills" | Update at week 6 and week 12 to match what I can actually do | 6, 12 |
| Pinned repos | Check pins: ParaBank, Playwright, then flagship | Pin the 4 best repos only | 9 |

## Workspace housekeeping

| Item | Status |
|---|---|
| Local duplicate `selenium-testng-practice` | ✅ Removed 15 Nov |
| `archive/eclipse-practice`, `archive/selenium-jars` | Keep (workspace rule: archive, don't delete) |
| `Neelima_Kolusu_CV_01.pdf` (placeholder contact details, different surname) | ✅ Moved to `archive/old-cvs/` on 15 Nov |
| GitHub CLI logged in as another account (`harshithkonatham`) | ⚠️ Log in as my own account before using `gh` for my repos |
