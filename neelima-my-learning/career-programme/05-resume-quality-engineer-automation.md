# Resume: Quality Engineer / Automation 🔒 (use from week 6)

> **Notes for me (delete before sending)**
> - **Unlock condition:** pass the week 6 checkpoint (skills checks 1–3 passed, mock interview 2 passed). This version leads with automation, so an interviewer *will* ask me to code.
> - Projects move above experience, because for automation roles they're the strongest evidence.
> - Update the `[ ]` items with real results when I have them; delete any I haven't done.
> - Further versions, built from this one when they're true:
>   - **AI Quality Engineer** (after week 9): flagship `banking-chatbot-eval` goes first; summary leads with LLM evaluation.
>   - **SDET** (after week 12, and only if mock interview 3 goes well): adds framework design and the MCP/agent work.

---

**NEELIMA GUNDUBOINA**
Quality Engineer | Test Automation (Java, Selenium, REST Assured, Playwright) | Banking Domain | ISTQB
London, UK | neelimagunduboina@outlook.com | +44 7865 406377
linkedin.com/in/neelimagunduboina | github.com/Neelima-Gunduboina
Full right to work in the UK, no sponsorship required

**PROFESSIONAL SUMMARY**

Quality Engineer combining 4 years of enterprise banking and insurance testing (Citi Bank FLEXCUBE, Royal London) with hands-on test automation in Java and TypeScript. I build automation that checks the data, not just the screen: UI, API and SQL tests that reconcile balances to the penny, run in GitHub Actions CI, and have found real defects in a public demo bank. Strong SQL and API testing background; ISTQB CTFL certified.

**TECHNICAL SKILLS**

- Languages: Java, TypeScript, SQL[, Python — add after week 7]
- UI automation: Selenium WebDriver, Playwright, Page Object Model, TestNG
- API: REST Assured, Postman, Newman, JSON schema validation
- CI/CD and tools: GitHub Actions, Maven, Docker[ — after week 4], Git, Jira, Zephyr, Azure DevOps
- Quality: Test strategy, risk-based testing, accessibility (WCAG 2.1 AA, axe-core), visual regression, defect management

**PROJECTS**

**ParaBank QA Framework** — Java, Selenium, TestNG, REST Assured, Postman/Newman, SQL, Docker, GitHub Actions
- Built a layered framework (Page Objects, API client, JSON-driven data, ThreadLocal drivers, Extent reporting) covering registration, transfers, bill pay and account APIs.
- Validated transfers end to end: UI action, API balance check and SQL reconciliation [ — against ParaBank's own database running in Docker, after week 4].
- Found and documented 5 defects, including negative and zero-amount transfers being accepted; open defects are tracked in a separate TestNG group so CI stays green while each remains reproducible.

**Playwright Banking Framework** — TypeScript, Playwright, axe-core, Claude API, GitHub Actions
- UI, API, visual and accessibility tests across 3 browsers in 4 parallel CI shards with merged reports.
- WCAG 2.1 AA audit of the bank: 7 issues documented.
- AI-assisted features with human review: requirement-to-test generation, CI failure triage, and self-healing locators that are disabled in CI by default and covered by deterministic tests.

**PROFESSIONAL EXPERIENCE**

**QA Test Engineer — Tata Consultancy Services (TCS), Hyderabad, India** | Aug 2021 – Oct 2025

*Citi Bank — Oracle FLEXCUBE Core Banking*
- Executed 200+ test cases per quarterly release across fund transfers, payments, SWIFT messaging and account management, including maker-checker and end-to-end flows.
- Validated transaction processing with SQL against Oracle; designed negative and boundary tests for transaction fields.
- Raised and tracked 100+ defects across 4 major releases in Jira/Zephyr; release sign-off with zero critical post-release incidents.

*Royal London — Insurance*
- Migration, remediation and release-validation testing across 3+ environments for 6 major releases.
- Authored and executed 50+ API scenarios in Postman; SQL validation of post-migration data integrity.
- Test and defect management in Azure DevOps; supported client-facing release runbook calls.

**CERTIFICATIONS**
ISTQB Certified Tester — Foundation Level (CTFL) · Agile Testing Certification

**EDUCATION**
B.Tech, Electronics and Communication Engineering — Shri Vishnu Engineering College for Women, India | 2021
