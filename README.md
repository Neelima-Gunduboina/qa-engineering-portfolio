# QA Engineering Portfolio — Neelima Gunduboina

QA Engineer with 4 years in banking and insurance testing (Citi Bank on Oracle FLEXCUBE, Royal London) at Tata Consultancy Services, building towards AI-enabled quality engineering. This repo holds my projects, one folder each, with their own tests and CI.

## Projects

| Project | What it shows | Stack | CI |
|---|---|---|---|
| [**banking-chatbot-eval**](banking-chatbot-eval/) | Evaluation framework for a RAG banking FAQ assistant: wrong facts, hallucinations, investment advice, prompt injection and personal-data leaks, with a release quality gate. The eval is itself tested: it must catch 7 seeded failures. | Python, Claude API, pytest, Pydantic | [![banking-chatbot-eval](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/banking-chatbot-eval.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/banking-chatbot-eval.yml) |
| [**ai-qa-toolkit**](ai-qa-toolkit/) | Test case generator from user stories (with coverage scoring), an MCP server that lets AI assistants run allow-listed test suites, and a LangGraph defect investigation agent that needs human approval. | Python, Claude API, LangGraph, MCP | [![ai-qa-toolkit](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-qa-toolkit.yml/badge.svg)](https://github.com/Neelima-Gunduboina/qa-engineering-portfolio/actions/workflows/ai-qa-toolkit.yml) |

## Earlier projects (separate repos)

| Repo | What it shows |
|---|---|
| [qa-portfolio-parabank](https://github.com/Neelima-Gunduboina/qa-portfolio-parabank) | Java, Selenium, TestNG, REST Assured, Postman and SQL testing of a demo bank; 5 documented defects |
| [ai-banking-qa-portfolio](https://github.com/Neelima-Gunduboina/ai-banking-qa-portfolio) | Playwright + TypeScript: UI, API, visual and WCAG accessibility tests, with AI-assisted features |
| [neelima-my-learning](https://github.com/Neelima-Gunduboina/neelima-my-learning) | Core Java and Selenium practice, and timed skills checks |

## How this repo is organised

- Every project is a top-level folder with its own README, dependencies and tests.
- Each project has its own GitHub Actions workflow in [`.github/workflows/`](.github/workflows/), triggered only by changes to that folder.
- New projects are added here as new folders, not as new repos.

## Contact

[LinkedIn](https://www.linkedin.com/in/neelimagunduboina/) · London, UK · Full right to work in the UK
