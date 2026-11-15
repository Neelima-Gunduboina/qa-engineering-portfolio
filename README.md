# QA Engineering Portfolio — Neelima Gunduboina

QA Engineer with 4 years in banking and insurance testing (Citi Bank on Oracle FLEXCUBE, Royal London) at Tata Consultancy Services, building towards AI-enabled quality engineering. This repo holds my projects, one folder each, with their own tests and CI.

## Projects

| Project | What it shows | Stack | CI |
|---|---|---|---|
| [**qa-portfolio-parabank**](qa-portfolio-parabank/) | UI, API and SQL testing of a demo bank, reconciling balances before and after every transfer; 5 documented defects, each with a report and a reproduction test | Java, Selenium, TestNG, REST Assured, Postman/Newman, SQL | [![qa-portfolio-parabank](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/qa-portfolio-parabank.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/qa-portfolio-parabank.yml) |
| [**ai-banking-qa-portfolio**](ai-banking-qa-portfolio/) | UI, API, visual and WCAG 2.1 AA accessibility tests across 3 browsers in sharded CI; AI test generation, failure triage and tested self-healing locators | TypeScript, Playwright, axe-core, Claude API | [![ai-banking-qa-portfolio](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-banking-qa-portfolio.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-banking-qa-portfolio.yml) |
| [**banking-chatbot-eval**](banking-chatbot-eval/) | Evaluation framework for a RAG banking FAQ assistant: wrong facts, hallucinations, investment advice, prompt injection and personal-data leaks, with a release quality gate. The eval is itself tested: it must catch 7 seeded failures. | Python, Claude API, pytest, Pydantic | [![banking-chatbot-eval](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/banking-chatbot-eval.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/banking-chatbot-eval.yml) |
| [**ai-qa-toolkit**](ai-qa-toolkit/) | Test case generator from user stories (with coverage scoring), an MCP server that lets AI assistants run allow-listed test suites, and a LangGraph defect investigation agent that needs human approval | Python, Claude API, LangGraph, MCP | [![ai-qa-toolkit](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-qa-toolkit.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-qa-toolkit.yml) |
| [**neelima-my-learning**](neelima-my-learning/) | Core Java (70 JUnit tests), Selenium + TestNG practice (21 tests) and timed no-AI skills checks | Java, JUnit 5, Selenium, TestNG | [![neelima-my-learning](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/neelima-my-learning.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/neelima-my-learning.yml) |

## How this repo is organised

- Every project is a top-level folder with its own README, dependencies and tests.
- Each project has its own GitHub Actions workflow in [`.github/workflows/`](.github/workflows/), triggered only by changes to that folder.
- New projects are added here as new folders, not as new repos.

## Contact

[LinkedIn](https://www.linkedin.com/in/neelimagunduboina/) · London, UK · Full right to work in the UK
