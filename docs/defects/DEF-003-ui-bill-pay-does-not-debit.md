# DEF-003: Bill Pay in the web UI confirms the payment but never debits the account

| Field | Value |
|---|---|
| **ID** | DEF-003 |
| **Component** | ParaBank web UI – Bill Pay (`billpay.htm` → `POST /services_proxy/bank/billpay`) |
| **Severity** | High (customer is told a payment was made when it was not) |
| **Priority** | P1 |
| **Status** | Open |
| **Environment** | https://parabank.parasoft.com (public demo), found 14 Nov 2026 |
| **Found by** | UI end-to-end test with balance reconciliation (`BillPayTest.payBillDebitsAccount`) |

## Summary
After a customer submits Bill Pay, the page shows **"Bill Payment Complete"** with the payee and amount, but
the account balance does not change and no transaction appears in the account history.
The same request sent straight to the REST API (`POST /services/bank/billpay`) **does** debit the account,
so the defect is in the web UI's proxy path.

## Steps to reproduce
1. Register a new customer (the first account opens with $515.50).
2. Open **Bill Pay**, enter payee *British Gas*, account number *12345678*, amount *25.50*, pick the account, click **Send Payment**.
3. Open **Accounts Overview**, then open the account's activity.

## Expected result
- Balance is $490.00 (515.50 − 25.50).
- Activity shows a debit *"Bill Payment to British Gas"* for $25.50.

## Actual result
- Confirmation page: *Bill Payment Complete – British Gas – $25.50* ✅
- Balance is still **$515.50** ❌
- Activity is **empty** ❌
- Reproduced in Selenium (2 of 2 attempts) and with curl against `services_proxy/bank/billpay` (2 of 2).

## Comparison across layers
| Layer | Request | Debited? |
|---|---|---|
| Web UI (proxy) | `POST /parabank/services_proxy/bank/billpay?accountId=…&amount=25.50` | ❌ No |
| REST API | `POST /parabank/services/bank/billpay?accountId=…&amount=25.50` | ✅ Yes |

## Evidence
- Automated (fails, group `known-defect`): `BillPayTest.payBillDebitsAccount`
- Automated (passes): `AccountApiTest.billPayDebitsAccount`

## Suggested fix
Make the UI proxy call the same posting logic as the REST service, and only show the confirmation after the debit is committed.

## Retest plan
Once fixed, move `payBillDebitsAccount` back to the `regression` group and run `mvn test -Pregression`.
