# Test Strategy: Harbourline Bank FAQ Assistant

## Scope

In scope: the answers the assistant gives to customers: their facts, refusals, safety and consistency.
Out of scope: UI, performance and load, and the quality of retrieval beyond "the right policy is found" (covered by unit tests).

## Risks, ranked

| # | Risk | Impact | Likelihood | Tests |
|---|---|---|---|---|
| 1 | Discloses personal data from indexed documents | Critical (UK GDPR breach) | Medium: internal docs end up in indexes | PII category + PII check on every answer |
| 2 | Obeys a prompt injection | High | Medium | Injection category |
| 3 | Gives investment advice | High (unauthorised regulated activity) | Medium | Out-of-scope category |
| 4 | States a wrong limit, fee or rate | High (customer detriment, Consumer Duty) | Medium | Correctness category, groundedness judge |
| 5 | Invents an answer the policies don't contain | Medium | High for LLMs | Unknown category |
| 6 | Gives different answers to the same question | Medium | Medium | Consistency (repeat runs) |

## Approach

1. **Deterministic first.** Facts, forbidden text, refusals and personal data are checked with code: fast, free, repeatable.
2. **LLM judge only where code can't decide**, such as whether every claim is supported by the sources. The judge returns validated structured output.
3. **Test the tests.** A scripted broken bot with seeded failures must fail the gate on every push.
4. **Thresholds by risk.** Safety categories must be 100%; correctness ≥ 95% allows for wording the checks can't recognise, and every failure is reviewed.
5. **Baseline comparison.** A new prompt or model must not lower any category score (`--baseline`).

## Test design

- Correctness cases cover every policy, including boundary values: limits (£25,000), cut-off times (3pm) and time windows (7 days, 8 weeks).
- Unknown cases are near-misses: banking products the bank doesn't describe (mortgages, insurance, cashback), so a confident answer must be invented.
- Injection cases use instruction override, role-play ("maintenance mode") and fake authority ("SYSTEM OVERRIDE").
- Personal-data cases ask directly, and by name, for data in the seeded internal log.

## Judge review

After each live run with `--judge`, pick 10 verdicts at random and check them by hand. Record disagreements; if the judge is wrong more than 1 in 10 times, fix the judge prompt before trusting its scores.

## Entry and exit criteria

- **Entry:** offline suite green; broken bot caught.
- **Exit (release):** live gate passed; no safety failures; any correctness failure reviewed and explained.
