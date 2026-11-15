# Curriculum: Phases 1–5

How the master prompt's five phases map onto the 12-week plan in `ROADMAP.md` (kept in my local workspace, not in this repo).
Each topic is marked:

- ✅ **Already strong**: revise for interviews only
- 🔨 **Build**: core of the plan
- ⏭️ **Later**: deliberately postponed, so I don't spread myself too thin

Free resources come first. Paid ones are marked 💷.

---

## Phase 1: Testing fundamentals (week 1, then 20 min/week of revision)

I have 4 years of this. The job here is to **name** what I already do, so I can talk about it in interviews.

| Topic | Status | What to be able to do | Resource |
|---|---|---|---|
| Functional, regression, smoke, sanity, integration, E2E | ✅ | Explain the difference with a FLEXCUBE example each | ISTQB CTFL syllabus v4 (free PDF, istqb.org) |
| Test design techniques | 🔨 | Apply equivalence partitioning, boundary values, decision tables and state transitions **by name** | ISTQB syllabus ch. 4; Ministry of Testing free articles |
| Exploratory testing | 🔨 | Write a session charter, run a 45-min timeboxed session, write notes | *Explore It!* (Elisabeth Hendrickson) 💷; Ministry of Testing |
| Defect lifecycle and bug reports | ✅ | My ParaBank DEF-001..005 reports are the evidence | Own repos |
| Test plan vs test strategy | 🔨 | Write a 1-page test strategy for a payment feature | ISTQB syllabus ch. 5 |
| Non-functional testing | 🔨 | Explain performance, security, accessibility at awareness level | OWASP Top 10 (free); WCAG quick reference |
| Agile and Scrum | ✅ | Explain how QA fits into a sprint, Three Amigos, Definition of Done | Scrum Guide (free, 13 pages) |
| Risk-based testing | 🔨 | Prioritise tests by impact × likelihood | ISTQB syllabus ch. 5 |

## Phase 2: Programming and automation (weeks 1–6) — the main work

| Topic | Status | Week | Must-have outcome | Resource |
|---|---|---|---|---|
| Java core: types, strings, control flow | 🔨 | 1–2 | Solve easy katas in 20 min without help | *dev.java* tutorials (free); Codewars 8–6 kyu |
| OOP: classes, inheritance, interfaces, polymorphism | 🔨 | 2 | Explain why POM uses inheritance (BasePage) and encapsulation (private locators) | dev.java "Learn" path |
| Collections and streams | 🔨 | 2 | `List`, `Map`, `Set`, sort, filter, group | dev.java; my `java-basics` docs |
| Exceptions | 🔨 | 2 | Checked vs unchecked; when a test should catch vs fail | dev.java |
| Selenium WebDriver | 🔨 | 2 | Locators, explicit waits, why `Thread.sleep` is bad | selenium.dev docs |
| TestNG | 🔨 | 2 | Annotations order, groups, data providers, listeners, retry | testng.org docs |
| Page Object Model | 🔨 | 2 | Build a POM from a blank file | Own ParaBank repo |
| Maven | 🔨 | 2–5 | Profiles, dependencies, `mvn test -P...` | maven.apache.org "in 5 minutes" |
| REST Assured | 🔨 | 3 | Given/when/then, auth, chaining, JSON schema validation | rest-assured.io docs |
| Postman / Newman | ✅→🔨 | 3 | Collection variables, test scripts, run in CI | Postman Learning Center (free) |
| SQL | ✅→🔨 | 3–4 | JOINs, GROUP BY/HAVING, subqueries, window functions basics | SQLBolt; Mode SQL tutorial (free) |
| Git and GitHub | 🔨 | 1–5 | Branch, commit, PR, rebase basics, resolve a merge conflict | *Pro Git* book (free) |
| Docker basics | 🔨 | 4 | Run ParaBank in a container; explain image vs container | docker.com "Get started" |
| GitHub Actions | 🔨 | 5 | Write a workflow from scratch: matrix, artifacts, secrets | GitHub Actions docs |
| Framework design | 🔨 | 5 | Config, layers, parallel safety, reporting, flakiness strategy | Own repos + interview bank |

## Phase 3: Modern QA engineering (weeks 4–7)

| Topic | Status | Week | Notes |
|---|---|---|---|
| Playwright (TypeScript) | 🔨 | 6 | Already in portfolio. Learn fixtures, auth state, tracing, web-first assertions |
| Pytest | 🔨 | 7 | Needed for the AI projects (Python) |
| Docker test environments | 🔨 | 4 | ParaBank in Docker |
| GitHub Actions CI/CD | 🔨 | 5 | Already used; now understand every line |
| Cross-browser | 🔨 | 6 | Playwright projects already run 3 browsers |
| Test reporting | 🔨 | 5 | Extent (Java), Playwright HTML, Allure awareness |
| API contract testing (Pact) | ⏭️ | After week 12 | Know what it is and why, for interviews |
| Performance testing (k6) | ⏭️ | After week 12 | k6 awareness only; one small script optional |
| Jenkins | ⏭️ | — | Awareness only: same ideas as GitHub Actions |
| Cypress | ⏭️ | — | Skip: Playwright covers the same ground and is growing faster |
| Appium (mobile) | ⏭️ | — | Skip unless a target job needs it |

## Phase 4: AI-powered testing (weeks 8–9)

| Topic | Status | Week | Built into |
|---|---|---|---|
| Prompt engineering for QA | 🔨 | 8 | Test case generator (Project 1) |
| GenAI for test case creation | 🔨 | 8 | Project 1 |
| AI test data generation | 🔨 | 8 | Project 1 (stretch) |
| Self-healing automation | ✅ (done) | — | ai-banking-qa-portfolio `utils/self-heal.ts`, now tested |
| AI failure triage | ✅ (exists) | 11 | ai-banking-qa-portfolio `scripts/triage.ts`, upgraded in Project 3 |
| LLM application testing | 🔨 | 9 | Flagship (Project 2) |
| RAG testing | 🔨 | 9 | Flagship: groundedness, retrieval quality |
| Hallucination and evaluation | 🔨 | 9 | Flagship: DeepEval / Promptfoo |
| AI security testing | 🔨 | 9 | Flagship: prompt injection, PII leaks (OWASP LLM Top 10) |
| Visual AI testing | ⏭️ | — | Playwright screenshot testing already covers the concept |

**Resources (free):** Anthropic prompt engineering docs; DeepEval docs; Promptfoo docs; OWASP Top 10 for LLM Applications; DeepLearning.AI short courses (free).

## Phase 5: Agentic AI engineering (weeks 10–11)

| Topic | Status | Week | Built into |
|---|---|---|---|
| What an agent is: loop, tools, memory | 🔨 | 10 | Project 4 |
| Tool calling / function calling | 🔨 | 10 | Project 4 |
| MCP: using servers | 🔨 | 10 | Playwright MCP (already documented in ai-banking repo) |
| MCP: building a server | 🔨 | 10 | Project 4 |
| LangGraph | 🔨 | 11 | Project 3 |
| Human-in-the-loop approval | 🔨 | 11 | Project 3 |
| AI governance and risk | 🔨 | 11 | README section in Projects 2 and 3 |
| Multi-agent architectures | ⏭️ | — | Awareness only |
| CrewAI, OpenAI Agents SDK | ⏭️ | — | Skip: one framework (LangGraph) is enough to show the concepts |
| Autonomous regression testing | ⏭️ | — | Discuss in interviews; too risky to build well in 12 weeks |

**Resources (free):** MCP docs (modelcontextprotocol.io); LangGraph docs and LangChain Academy (free); Anthropic "Building effective agents" article.
