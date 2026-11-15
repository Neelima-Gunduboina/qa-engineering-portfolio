# Interview Preparation

## How mock interviews work

1. My mentor asks **one question at a time**. I answer in my own words, without notes or AI.
2. Each answer is scored 1–5:
   - **1:** wrong or blank
   - **3:** correct but generic
   - **5:** correct, specific, with my own example
3. I get the weak points and an improved answer, then the next question.
4. Scores go in the mock interview table in `ROADMAP.md` (kept in my local workspace, not in this repo).

**Answer formula for experience questions (STAR-lite):** situation in one line → what *I* did → result with a number → what I learned.

---

## A. Manual testing and QA fundamentals

1. Difference between verification and validation?
2. Severity vs priority: give a banking example where they differ.
3. How do you decide what to regression test when time is short?
4. Explain boundary value analysis using a £25,000 daily transfer limit.
5. What makes a good bug report?
6. Smoke vs sanity testing?
7. How would you test a maker-checker approval flow?
8. A developer says "can't reproduce". What do you do?
9. What is exploratory testing, and how do you make it accountable?
10. Test plan vs test strategy?

**Model answer, Q4 (BVA).** "The limit is £25,000, so I test either side of each boundary: £0 and £0.01 at the bottom (is zero allowed? the spec must say), £24,999.99, £25,000.00 and £25,000.01 at the top. I'd also check how the limit accumulates: two transfers of £12,500.01 in one day should fail on the second. On my ParaBank project, zero and same-account transfers were accepted (DEF-004), exactly the kind of boundary gap this finds."

**Model answer, Q7 (maker-checker).** "Positive: maker creates, checker approves, transaction posts once. Negative: maker can't approve their own entry; checker can't approve after the maker edits without re-review; rejection returns it with a reason; a timeout or expiry rule if one exists. Data: verify in the database that status changes and the audit trail record who did what and when. At Citi I validated these flows across environments, including the backend rows."

## B. Java (coding exercises live in `neelima-my-learning/skills-check` and `katas/`)

1. `==` vs `.equals()`?
2. Why is `String` immutable, and why does that matter?
3. `ArrayList` vs `LinkedList`; `HashMap` vs `TreeMap`.
4. Checked vs unchecked exceptions; how should a test framework handle each?
5. What do `final`, `static` and `this` mean?
6. Interface vs abstract class: where does each appear in my framework?
7. Why `BigDecimal` for money?
8. **Code:** reverse a string; count character frequency; find duplicates in a list; check a palindrome; second-highest number; sum of valid amounts (skills check 1).

## C. Selenium, TestNG and Page Object Model

1. Explicit vs implicit vs fluent waits. Why not `Thread.sleep()`?
2. `StaleElementReferenceException`: what causes it, and how do you fix it?
3. Locator priority: which do you use first, and why?
4. How does POM reduce maintenance? Show it in your ParaBank code.
5. TestNG annotation execution order.
6. How do you run tests in parallel safely? (ThreadLocal driver)
7. `@DataProvider` vs reading JSON: when would you use each?
8. How do you handle flaky tests? (RetryOnce: when it's acceptable and when it hides bugs)
9. How do you take a screenshot on failure? (listener)
10. Hard vs soft assertions.

## D. API testing

1. Status codes: 200, 201, 204, 400, 401, 403, 404, 409, 500. Which would you expect for a duplicate username?
2. PUT vs PATCH; idempotency.
3. How do you chain requests in Postman and in REST Assured?
4. What do you check in an API response beyond the status code?
5. How would you test authentication and authorisation?
6. JSON schema validation: why bother?
7. Walk me through DEF-001 (negative transfer amount): how you found it, why it matters, how you'd prevent it.

## E. SQL

1. INNER vs LEFT JOIN, with a customers/accounts example.
2. Find customers with more than 3 transactions today (GROUP BY / HAVING).
3. Find accounts whose balance doesn't equal opening balance + credits − debits (reconciliation).
4. Second-highest transaction amount.
5. Find duplicate transactions (same account, amount and timestamp).
6. What's a transaction and rollback, and how did you test one?

## F. Playwright

1. How does Playwright auto-waiting differ from Selenium?
2. Fixtures: what problem do they solve?
3. How do you reuse login state across tests?
4. Trace viewer: when do you use it?
5. Why `getByRole` over CSS selectors?
6. How does sharding work in your CI?

## G. CI/CD and framework design

1. Walk me through your GitHub Actions workflow line by line.
2. Where should UI, API and unit tests run in a pipeline, and why? (test pyramid)
3. How do you manage secrets in CI?
4. Design a framework for a new banking web app with UI and APIs. What layers, what tools, and why?
5. How do you keep a suite fast as it grows?
6. Your nightly run has 12 failures. Walk me through triage.

## H. AI testing and LLM applications

1. How is testing an LLM application different from testing normal software?
2. What is hallucination, and how do you measure it?
3. What is RAG? What can go wrong in retrieval vs generation?
4. What is prompt injection? Give two examples and how you'd test for them.
5. LLM-as-judge: benefits and risks. How do you check the judge?
6. How would you build a regression gate for a prompt change?
7. What are the OWASP Top 10 for LLM Applications? Name 3.
8. Your self-healing locator: when is self-healing dangerous? (Model answer: when it silently passes a test against the wrong element. Mine accepts only a unique match, is off in CI by default, and logs every heal.)

## I. Agentic AI and MCP

1. What makes something an agent rather than a single LLM call?
2. What is tool calling / function calling?
3. What is MCP, and why does it matter for QA teams?
4. Where must a human stay in the loop in a testing agent, and why?
5. How would you test an agent? (tools in isolation, recorded scenarios, limits on actions, logs)
6. What should an AI testing agent never be allowed to do?

## J. SDET system design (week 12)

1. Design a test strategy and automation platform for a payments microservice.
2. Design test data management for 10 teams sharing one staging environment.
3. How would you test a fund-transfer system end to end, including idempotency and failures?
4. Design an evaluation pipeline for a customer-facing banking chatbot.

## K. Behavioural

1. Tell me about a critical defect you found.
2. A time you disagreed with a developer.
3. A release under pressure: what did you prioritise?
4. Why are you moving from manual testing to automation and AI?
5. Explain your gap since October 2025. (Model answer: "I relocated to London in January 2026. Since then I've upskilled full-time in automation and AI testing; you can see the work on my GitHub.")
