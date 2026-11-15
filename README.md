# Neelima – My Learning

My learning repository for core Java and Selenium automation, built alongside my work as a QA Engineer in banking and insurance.

[![CI](https://github.com/Neelima-Gunduboina/neelima-my-learning/actions/workflows/ci.yml/badge.svg)](https://github.com/Neelima-Gunduboina/neelima-my-learning/actions/workflows/ci.yml) ![Java](https://img.shields.io/badge/Java-17-blue) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

| Project | What it covers | Tests |
|---|---|---|
| [`java-basics/`](java-basics/) | Data types, control flow, OOP, collections, exceptions, streams; classic number programs; a layered Student Management mini-project | 70 JUnit 5 tests |
| [`selenium-testng-practice/`](selenium-testng-practice/) | Selenium WebDriver + TestNG + Page Object Model: locator strategies, implicit/explicit/fluent waits, assertions, dropdowns, data-driven negative testing | 21 TestNG tests |
| [`skills-check/`](skills-check/) | Timed, no-AI exercises (Java, Selenium, API, SQL, test design) that track my progress | Written as I go |

Both are Maven projects (Java 17). GitHub Actions runs both on every push.

## Run locally

```bash
cd java-basics && mvn test                 # unit tests, no browser needed
cd selenium-testng-practice && mvn test    # needs Chrome; runs headless
```

## See also

- [qa-portfolio-parabank](https://github.com/Neelima-Gunduboina/qa-portfolio-parabank): banking test framework covering UI, API (REST Assured + Postman) and SQL backend validation, where I applied these skills and found 5 real defects in the demo bank
- [ai-banking-qa-portfolio](https://github.com/Neelima-Gunduboina/ai-banking-qa-portfolio): Playwright + TypeScript framework

## License

MIT, see [LICENSE](LICENSE).
