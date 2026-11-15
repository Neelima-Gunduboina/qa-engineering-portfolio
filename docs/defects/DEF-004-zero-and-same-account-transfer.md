# DEF-004: Transfers of $0 and transfers to the same account are accepted

| Field | Value |
|---|---|
| **ID** | DEF-004 |
| **Component** | ParaBank transfer – web UI (`transfer.htm`) and REST API (`POST /services/bank/transfer`) |
| **Severity** | Medium (meaningless transactions are posted and confirmed to the customer) |
| **Priority** | P2 |
| **Status** | Open |
| **Environment** | https://parabank.parasoft.com (public demo), found 15 Nov 2026 |
| **Found by** | Boundary-value testing of the amount field; related to [DEF-001](DEF-001-negative-transfer-amount.md) |

## Summary
The transfer flow has no business-rule validation on the amount or the accounts:
- An amount of **0** is accepted and confirmed as *"Transfer Complete! $0.00 has been transferred…"*.
- **From** and **To** can be the **same account**, and the transfer is reported as successful.

Together with DEF-001 (negative amounts), this shows the transfer service never checks `amount > 0` or `from ≠ to`.

## Steps to reproduce (UI)
1. Log in and open **Transfer Funds**.
2. Enter amount `0`, keep any From/To accounts, click **Transfer**.

## Steps to reproduce (API)
```
POST /parabank/services/bank/transfer?fromAccountId=A&toAccountId=B&amount=0   -> 200 "Successfully transferred $0 …"
POST /parabank/services/bank/transfer?fromAccountId=A&toAccountId=A&amount=1   -> 200 "Successfully transferred $1 …"
```

## Expected result
- Amount 0: validation error *"Please enter a valid amount"* (the page already contains this message, but never shows it for 0).
- Same account: validation error such as *"Choose two different accounts"*.
- No transaction rows created.

## Actual result
HTTP 200 / "Transfer Complete!" in both cases, and transaction rows are written.

## Evidence
- Java (group `known-defect`): `AccountApiTest.zeroAmountShouldBeRejected`, `AccountApiTest.sameAccountTransferShouldBeRejected`
- Playwright: `TC-T-03` in [ai-banking-qa-portfolio](https://github.com/Neelima-Gunduboina/ai-banking-qa-portfolio) (marked `test.fail()`)
- Run: `mvn test -Pknown-defects`

## Suggested fix
Validate on the server: `amount > 0`, `fromAccountId != toAccountId`, and enough available balance. Mirror the amount rule in the UI.
