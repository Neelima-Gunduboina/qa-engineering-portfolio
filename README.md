# ParaBank QA Portfolio: UI, API and Backend Testing

End-to-end test framework for **ParaBank**, a public demo online bank. It covers banking workflows the way I test them at work: functional UI flows, API checks with **Postman** and **REST Assured**, and **SQL backend validation** of transfer business rules.

![Java](https://img.shields.io/badge/Java-17-blue) ![Selenium](https://img.shields.io/badge/Selenium-4.52-green) ![TestNG](https://img.shields.io/badge/TestNG-7.12-orange) ![Postman](https://img.shields.io/badge/Postman-Newman-ff6c37) ![SQL](https://img.shields.io/badge/SQL-Oracle%20mode-lightgrey)

## What is tested

| Layer | Scenarios | Tool |
|---|---|---|
| **UI** | Registration, duplicate username, login / invalid / empty login, open account, **fund transfer with balance reconciliation** (data-driven), **bill payment** | Selenium WebDriver, POM, TestNG |
| **API** | Valid / invalid login, customer accounts, account lookup, unknown account (400), non-numeric amount, **transfer → balances → transaction history** | REST Assured + Postman/Newman |
| **Backend (SQL)** | Debit/credit, total-balance reconciliation, audit row, **insufficient funds rollback**, unknown account rollback, negative amount blocked | JDBC + H2 (Oracle mode) |
| **Defects** | [DEF-001](docs/defects/DEF-001-negative-transfer-amount.md): API accepts a negative transfer amount | Reproduced in TestNG and Postman |

## Project structure

```
├── postman/                               # Postman collection + environment (13 requests, 28 assertions)
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
    │   ├── dataproviders/                 # JSON-driven @DataProviders
    │   └── tests/{ui,api,db}/
    └── resources/
        ├── config-qa.properties / config-staging.properties
        ├── testng.xml · smoke.xml · regression.xml · api-db.xml
        ├── db/schema.sql                  # schema with CHECK constraints = business rules
        └── testdata/*.json
```

## How to run

Prerequisites: Java 17+, Maven 3.9+, Chrome (Selenium Manager fetches the driver automatically).

```bash
mvn test                      # full suite
mvn test -Psmoke              # smoke tests only
mvn test -Pregression         # regression
mvn test -Papi-db             # API + DB only (no browser needed)
mvn test -Denv=staging        # switch environment config
mvn test -Dheadless=false     # watch the browser
```

Postman / Newman:

```bash
npx newman run postman/ParaBank-API.postman_collection.json \
  -e postman/ParaBank-QA.postman_environment.json \
  --folder "01 Authentication" --folder "02 Accounts" --folder "03 Fund Transfer (end-to-end)"
```

You can also import both files into the Postman app and use **Run collection**.

Docker (API + DB suites):

```bash
docker build -t parabank-qa . && docker run --rm parabank-qa
```

## Reports

- ExtentReports HTML: `reports/extent-report.html` (failed UI tests include a screenshot)
- TestNG/Surefire: `target/surefire-reports/`
- CI: GitHub Actions runs the Maven smoke suite and the Newman collection on every push, then uploads both reports as artifacts.

## Design choices

- **No `Thread.sleep()`**: every interaction waits explicitly, and ParaBank's AJAX-loaded account dropdowns are awaited before selection.
- **Assertions check the data itself**, not just the page: for example, a transfer test reads balances before and after and checks the debit and credit with `BigDecimal`.
- **Self-contained test data**: each UI test registers its own unique customer, and API transfer tests open fresh accounts, so tests don't depend on shared state.
- **Known defects are tracked, not hidden**: they sit in the `known-defect` TestNG group (excluded from suites) and in a separate Postman folder.
- **No real secrets**: `john/demo` is ParaBank's published demo login and can be overridden with `-Dapi.username` / `-Dapi.password`.

## Note on the database layer

ParaBank's production database isn't publicly reachable, so the DB tests use an in-memory H2 database in **Oracle compatibility mode**. The schema enforces the same rules a core-banking backend would (no overdraft, positive amounts, referential integrity), and the tests use the same approach I use for SQL/Oracle backend validation on real projects.
