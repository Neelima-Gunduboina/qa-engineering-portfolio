# AI and Testing Tools

**Maturity:**
- 🟢 **Mature:** widely used in production; safe to put on a CV.
- 🟡 **Established but moving fast:** APIs change, so pin versions.
- 🔴 **Experimental:** fine for learning, don't build a portfolio on it.

**Cost:** Free / Free tier / 💷 Paid.
**Local:** ✅ = runs on my Mac.

Versions and features change quickly in this area. Check each tool's own docs before starting a project.

## Tools I'll use in the plan

| Tool | Category | Maturity | Cost | Local | Used in |
|---|---|---|---|---|---|
| Playwright | Test automation | 🟢 | Free | ✅ | ai-banking repo (week 6) |
| Selenium + TestNG | Test automation | 🟢 | Free | ✅ | ParaBank repo |
| REST Assured | API testing | 🟢 | Free | ✅ | ParaBank repo |
| Postman / Newman | API testing | 🟢 | Free tier | ✅ | ParaBank repo |
| pytest | Python testing | 🟢 | Free | ✅ | Projects 1–4 |
| Docker Desktop | Test environments | 🟢 | Free for personal use | ✅ | Week 4 |
| GitHub Actions | CI/CD | 🟢 | Free for public repos | — | All repos |
| Anthropic Claude API | LLM | 🟢 | 💷 pay per use | — | Projects 1–4 |
| Claude Code | AI coding assistant | 🟢 | 💷 | ✅ | Daily, **after** the no-AI skills checks |
| Promptfoo | LLM evaluation and red-teaming | 🟡 | Free (open source) | ✅ | Flagship |
| DeepEval | LLM evaluation (pytest-style) | 🟡 | Free (open source) | ✅ | Flagship |
| Playwright MCP | MCP server for browser control | 🟡 | Free | ✅ | Week 10 demo |
| MCP Python SDK | Build MCP servers | 🟡 | Free | ✅ | Project 4 |
| LangGraph | Agent workflows | 🟡 | Free (open source) | ✅ | Project 3 |
| axe-core | Accessibility | 🟢 | Free | ✅ | ai-banking repo |

## Worth knowing for interviews (don't build with them yet)

| Tool | Category | Maturity | Why it matters |
|---|---|---|---|
| RAGAS | RAG evaluation | 🟡 | Named in UK banking AI-testing adverts |
| LangSmith | LLM tracing and evaluation | 🟡 | 💷 Free tier. Named in adverts; observability for LLM apps |
| Langfuse | Open-source LLM observability | 🟡 | Self-hostable alternative to LangSmith |
| OWASP Top 10 for LLM Apps | Security checklist | 🟢 | The reference list for LLM security testing |
| Ollama | Run open-source LLMs locally | 🟡 | ✅ Free local models for cheap experiments; weaker than Claude for judging |
| k6 | Performance testing | 🟢 | JavaScript load tests; common in modern teams |
| Pact | Contract testing | 🟢 | Microservice API contracts |
| Allure | Test reporting | 🟢 | Common in Java shops |
| Cucumber / BDD | Test specification | 🟢 | Still common in UK banks; learn after week 12 if adverts ask |
| CrewAI, OpenAI Agents SDK | Agent frameworks | 🟡 | Alternatives to LangGraph; one framework is enough to show the concepts |
| Applitools | Visual AI testing | 🟢 | 💷 Commercial; Playwright screenshots cover the concept for free |
| Self-healing platforms (Testim, Mabl, Functionize) | Commercial AI test tools | 🟢 | 💷 Know they exist; my own self-heal utility shows I understand the mechanism |
| Autonomous browser agents (Browser Use etc.) | Agentic testing | 🔴 | Impressive demos, unreliable for regression; good interview talking point about risk |

## Using AI without weakening my fundamentals

1. **Skills checks and katas:** no AI, ever.
2. **Learning a new topic:** write the first version myself, then ask AI to review it and explain what's better.
3. **Projects:** AI can draft. I must be able to explain and change every line before committing.
4. **Every AI feature I build has a human approval step**, and tests that prove it.
