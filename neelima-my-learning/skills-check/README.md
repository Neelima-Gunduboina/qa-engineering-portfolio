# Skills check

Short exercises I complete **without AI help**, to measure where I really am.
Each one is reviewed and the result is recorded below, so my progress is visible over time.

## Rules

- No AI assistants, no copying from my other repos. Official docs (Java API, Selenium, Playwright) are allowed.
- Stay within the time box. Unfinished or non-compiling code still counts.
- Predict the expected output **before** running the code.
- Write the tests myself in `src/test/java`.

## Exercises

| # | Area | Task | Status |
|---|---|---|---|
| 1 | Java | `TransactionSummary.totalValidAmount`: sum valid positive amounts from text input (25 min) | To do |
| 2 | Selenium + POM | Login page object and a positive and negative test, from a blank file | Not started |
| 3 | API | REST Assured test chaining two calls | Not started |
| 4 | SQL | Reconciliation queries on a small schema | Not started |
| 5 | Testing thinking | Test design for a banking user story | Not started |

### Exercise 1: Java fundamentals

A payments system receives transaction amounts as text. Implement
`TransactionSummary.totalValidAmount(List<String> amounts)` so that it:

- adds up only valid, positive amounts
- skips anything that is not a number (`"abc"`, `""`, `null`)
- skips zero and negative amounts
- returns the total rounded to 2 decimal places

In `main`, print the result for `"100.50", "abc", "-20", "0", "49.5", "", "10.005"`.
Write down the expected output before running it.

Then answer:

1. Why use `BigDecimal` instead of `double` for money?
2. What happens if the list itself is `null`? Should the method handle that?
3. Name 3 test cases you would write for this method, and write them as JUnit 5 tests.

## Run

```bash
cd skills-check && mvn test
```
