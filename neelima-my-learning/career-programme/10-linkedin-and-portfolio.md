# LinkedIn and Portfolio

Nothing here is published until I approve it. I post it myself.

## LinkedIn headline (220 characters max)

**Now:**
> QA Engineer | Banking & Insurance (Citi FLEXCUBE, Royal London) | API, SQL & Test Automation (Java, Selenium, Playwright) | ISTQB | London

**After week 9 (flagship built):**
> Quality Engineer | Test Automation (Java, Playwright) + LLM Testing | Banking Domain | ISTQB | London

## LinkedIn About section (now)

> I test the systems that move money.
>
> For four years at Tata Consultancy Services I tested enterprise banking and insurance systems: Oracle FLEXCUBE core banking for Citi Bank (fund transfers, payments, SWIFT, maker-checker) and migration and API testing for Royal London. I checked results in the database, not just on screen, and managed 100+ defects across four releases to sign-off with zero critical incidents after go-live.
>
> Since moving to London in January 2026, I've been building test automation in public:
> • ParaBank QA framework (Java, Selenium, TestNG, REST Assured, SQL, GitHub Actions), which found 5 real defects in a demo bank, including a transfer API that accepts negative amounts
> • Playwright + TypeScript framework with cross-browser, API, visual and accessibility (WCAG 2.1 AA) tests, plus AI-assisted features with human review
>
> I'm now focused on what's next for testing: evaluating AI applications for hallucination, prompt injection and data leaks, the risks banks face as they put AI in front of customers.
>
> Looking for Quality Engineer roles in UK financial services. Full right to work in the UK.
>
> github.com/Neelima-Gunduboina

**Featured section:** ParaBank repo · DEF-001 defect report · accessibility findings · (later) flagship repo.

## Posts: one every 2 weeks, about what I built or broke

| Week | Post idea |
|---|---|
| 2 | "5 bugs I found in a demo bank, and the test design technique behind each" |
| 4 | "Why my database tests were testing the wrong thing, and how Docker fixed it" (honest learning post) |
| 6 | "Selenium vs Playwright, from someone who's now written both from scratch" |
| 8 | "I asked an LLM to write test cases for a fund transfer. Here's what it missed." |
| 10 | "How do you test a banking chatbot? My evaluation framework" |
| 12 | "12 weeks from manual tester to quality engineer: what worked" |

Format: a hook in the first line, 5–8 short lines, one image (screenshot or diagram), a link in the first comment.

## GitHub profile checklist

- [ ] Pin 4 repos in this order: flagship (after week 9) · ParaBank · Playwright · ai-qa-toolkit
- [ ] Every pinned repo has: one-paragraph "what and why", an architecture diagram, a "how to run" section, a CI badge that's green, a screenshot of a report, and a "what I learned / limitations" section
- [ ] Architecture diagrams drawn with Mermaid inside the README (GitHub renders it, so no image files needed)
- [ ] Profile README updated at weeks 6, 9 and 12 to match what I can actually do
- [ ] Commit little and often; real history beats big drops of code

## Technical articles (optional, after week 9)

Publish on dev.to or LinkedIn articles, cross-linked from README:

1. "Testing an LLM banking assistant: a test strategy" (from flagship `docs/test-strategy.md`)
2. "Self-healing locators: useful or dangerous?" (from the ai-banking repo work)
