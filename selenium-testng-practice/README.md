# Selenium TestNG Practice

Hands-on Selenium WebDriver practice in Java, organised as one Maven project with TestNG and the Page Object Model.
It covers **locator strategies, wait strategies, assertions, dropdowns, data-driven tests and negative testing**.

![Java](https://img.shields.io/badge/Java-17-blue) ![Selenium](https://img.shields.io/badge/Selenium-4.52-green) ![TestNG](https://img.shields.io/badge/TestNG-7.12-orange)

## What is covered

| Test class | Skill demonstrated | Site |
|---|---|---|
| `LocatorStrategiesTest` | ID, Name, XPath and CSS locators driven by one `@DataProvider`; invalid username/password errors | practicetestautomation.com |
| `SauceLoginTest` | POM login, assertions on URL, page title and product count; clearing and re-typing a text box | saucedemo.com |
| `NegativeLoginTest` | 7 data-driven negative cases: wrong credentials, empty fields, locked-out user, SQL-injection-style input | saucedemo.com |
| `ProductSortTest` | `Select` dropdown handling, plus checking that prices and names really are sorted | saucedemo.com |
| `WaitStrategiesTest` | Implicit, explicit (`WebDriverWait`) and fluent waits, without `Thread.sleep()` | saucedemo.com |

**21 test cases** in total.

## Project structure

```
src/main/java/com/neelima/practice/
├── config/Config.java          # reads config.properties; -Dkey=value overrides
├── driver/DriverFactory.java   # Chrome / Firefox / Edge, headless option
└── pages/                      # Page Object Model
    ├── BasePage.java           # shared explicit-wait helpers
    ├── SauceLoginPage.java
    ├── InventoryPage.java
    └── PracticeLoginPage.java
src/test/java/com/neelima/practice/
├── base/BaseTest.java          # opens/closes a browser per test (ThreadLocal, parallel-safe)
└── tests/                      # the test classes above
src/test/resources/
├── config.properties
├── testng.xml                  # full suite (runs classes in parallel)
└── smoke.xml                   # smoke group only
```

## How to run

Prerequisites: Java 17+, Maven 3.9+, Google Chrome. Selenium Manager downloads the right ChromeDriver automatically.

```bash
mvn test                                              # full suite, headless Chrome
mvn test -DsuiteXmlFile=src/test/resources/smoke.xml  # smoke tests only
mvn test -Dheadless=false                             # watch the browser
mvn test -Dbrowser=firefox                            # run in another browser
```

Results are written to `target/surefire-reports/` (open `index.html`).

## Coding standards used

- One page class per screen; locators are `private static final By` fields, and tests never call `findElement` on page elements directly
- Every interaction goes through an explicit wait in `BasePage`, with no `Thread.sleep()`
- Every test ends in a TestNG `Assert`, not a `System.out.println("passed")`
- The browser is always closed in `@AfterMethod(alwaysRun = true)`
- Config and test data are kept out of the test code

## History

This project replaces the earlier `selenium-login-automation/` scripts. Those were standalone `main()` programs using
`Thread.sleep()` and print statements instead of assertions. Each old script now maps to a proper TestNG test:

| Old script(s) | Now covered by |
|---|---|
| `Locators`, `LoginUsingXPath`, `LoginUsingCSSSelector` | `LocatorStrategiesTest` |
| `implicitwait`, `explicitwait` | `WaitStrategiesTest` (+ fluent wait) |
| `LoginPagePOM`, `TestngAssert` | `SauceLoginTest` + `SauceLoginPage` |
| `negativetesterrormessage`, `formvalidation` | `NegativeLoginTest` (7 data-driven cases) |
| `dropdownexample` | `ProductSortTest` (now verifies the sort result) |
