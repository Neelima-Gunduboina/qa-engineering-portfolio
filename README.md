# ParaBank QA Portfolio: UI, API and Backend Testing

End-to-end test framework for **ParaBank**, a public demo online bank. It covers banking workflows the way I test them at work: functional UI flows, API checks with **Postman** and **REST Assured**, and **SQL backend validation** of transfer business rules.

[![CI](https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/actions/workflows/ci.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/actions/workflows/ci.yml) ![Java](https://img.shields.io/badge/Java-25-blue) ![Selenium](https://img.shields.io/badge/Selenium-4.52-green) ![TestNG](https://img.shields.io/badge/TestNG-7.12-orange) ![Postman](https://img.shields.io/badge/Postman-Newman-ff6c37) ![SQL](https://img.shields.io/badge/SQL-Oracle%20mode-lightgrey) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## What is tested

| Layer | Scenarios | Tool |
|---|---|---|
| **UI** | Registration, duplicate username, login / invalid / empty login, open account, **fund transfer with balance reconciliation** (data-driven), **bill payment** | Selenium WebDriver, POM, TestNG |
| **API** | Valid / invalid login, customer accounts, account lookup, unknown account (400), non-numeric amount, **transfer → balances → transaction history**, **bill payment → balance → history** | REST Assured + Postman/Newman |
| **Backend (SQL)** | Debit/credit, total-balance reconciliation, audit row, **insufficient funds rollback**, unknown account rollback, negative amount blocked | JDBC + H2 (Oracle mode) |
| **Defects** | 3 real defects found in ParaBank and documented (see below) | Reproduced in TestNG and Postman |

## Defects found

Testing every layer found three real defects in the live ParaBank demo. Each has a full report with steps, expected vs actual results and evidence, plus an automated test that reproduces it.

| ID | Defect | Severity | Found by |
|---|---|---|---|
| [DEF-001](docs/defects/DEF-001-negative-transfer-amount.md) | Transfer API accepts a **negative amount** and moves money in reverse | High | Negative API testing |
| [DEF-002](docs/defects/DEF-002-login-accepts-unknown-credentials.md) | Web login with **unknown credentials signs you in as another customer** | Critical | Negative UI testing |
| [DEF-003](docs/defects/DEF-003-ui-bill-pay-does-not-debit.md) | UI Bill Pay shows "Payment Complete" but **never debits the account** (the API path works) | High | UI balance reconciliation, cross-checked via API |

The defect tests sit in the `known-defect` TestNG group, so CI stays green while each defect can still be reproduced on demand with `mvn test -Pknown-defects`.

## Project structure

```
├── postman/                               # Postman collection + environment (15 requests, 31 assertions)
├── sql/backend-validation-queries.sql     # Oracle SQL used for post-transfer DB checks
├── docs/defects/                          # Defect reports
├── src/main/java/com/neelima/qa/
│   ├── pages/                             # Page Objects (BasePage with explicit waits)
│   ├── api/ParaBankApiClient.java         # REST Assured client
│   └── utils/                             # Config, DB, JSON, Extent reporting, screenshots
└── src/test/
    ├── java/com/neelima/qa/
    │   ├── base/BaseTest.java             # ThreadLocal WebDriver (parallel-safe)
    │   ├── listeners/TestListener.java    # ExtentReports + screenshot on failure
    │   ├── retry/RetryOnce.java           # single retry for long E2E UI flows
    │   ├── dataproviders/                 # JSON-driven @DataProviders
    │   └── tests/{ui,api,db}/
    └── resources/
        ├── config-qa.properties / config-staging.properties
        ├── testng.xml · smoke.xml · regression.xml · api-db.xml · known-defects.xml
        ├── db/schema.sql                  # schema with CHECK constraints = business rules
        └── testdata/*.json
```

## How to run

Prerequisites: Java 25+, Maven 3.9+, Chrome (Selenium Manager fetches the driver automatically).

```bash
mvn test                      # full suite
mvn test -Psmoke              # smoke tests only
mvn test -Pregression         # regression
mvn test -Papi-db             # API + DB only (no browser needed)
mvn test -Pknown-defects      # reproduce the open defects (these tests are expected to fail)
mvn test -Denv=staging        # switch environment config
mvn test -Dheadless=false     # watch the browser
```

Postman / Newman:

```bash
npx newman run postman/ParaBank-API.postman_collection.json \
  -e postman/ParaBank-QA.postman_environment.json \
  --folder "00 Setup" --folder "01 Authentication" --folder "02 Accounts" --folder "03 Fund Transfer (end-to-end)"
```

You can also import both files into the Postman app and use **Run collection**.

Docker (API + DB suites):

```bash
docker build -t parabank-qa . && docker run --rm parabank-qa
```

## Reports

- ExtentReports HTML: `reports/extent-report.html` (failed UI tests include a screenshot)
- TestNG/Surefire: `target/surefire-reports/`
- CI: GitHub Actions runs the Maven smoke suite and the Newman collection on every push, then uploads both reports as artifacts. Run the full regression suite from the Actions tab with **Run workflow → regression**.

## Design choices

- **No `Thread.sleep()`**: every interaction waits explicitly, and ParaBank's AJAX-loaded account dropdowns are awaited before selection.
- **Assertions check the data itself**, not just the page: for example, a transfer test reads balances before and after and checks the debit and credit with `BigDecimal`.
- **Self-contained test data**: every UI test, the API suite and the Postman run each register their own new customer, so nothing depends on shared demo data. The public demo resets regularly, and its old `john/demo` login no longer exists.
- **Robust against a shared demo**: UI suites run sequentially, and the two long end-to-end UI flows retry once (`RetryOnce`), because other visitors' activity can leak into a session (DEF-002). A genuine regression fails both attempts.
- **Known defects are tracked, not hidden**: they sit in the `known-defect` TestNG group (excluded from suites) and in a separate Postman folder.
- **No real secrets**: test customers are generated at runtime. To use an existing customer instead, pass `-Dapi.username=… -Dapi.password=…`.

## Note on the database layer

ParaBank's production database isn't publicly reachable, so the DB tests use an in-memory H2 database in **Oracle compatibility mode**. The schema enforces the same rules a core-banking backend would (no overdraft, positive amounts, referential integrity), and the tests use the same approach I use for SQL/Oracle backend validation on real projects.

## Author

**Neelima Gunduboina**, QA Engineer (Banking and Insurance), ISTQB CTFL · [LinkedIn](https://www.linkedin.com/in/neelimagunduboina/)

## License

MIT, see [LICENSE](LICENSE).
