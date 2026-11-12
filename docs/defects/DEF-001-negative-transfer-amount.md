# DEF-001: Transfer API accepts a negative amount

| Field | Value |
|---|---|
| **ID** | DEF-001 |
| **Component** | ParaBank REST API – `POST /services/bank/transfer` |
| **Severity** | High (money moves in the wrong direction; possible fraud vector) |
| **Priority** | P1 |
| **Status** | Open |
| **Environment** | https://parabank.parasoft.com (public demo) |
| **Found by** | Negative API testing (boundary / invalid input) |

## Summary
The transfer endpoint accepts a negative `amount`. It returns HTTP 200 and moves money **from the target account to the source account**, which reverses the transfer the customer asked for.

## Steps to reproduce
1. `GET /services/bank/login/john/demo` and note the customer `id`.
2. Open two accounts with `POST /services/bank/createAccount?customerId={id}&newAccountType=0&fromAccountId={existing}`, then note account ids **A** and **B** and their balances.
3. `POST /services/bank/transfer?fromAccountId=A&toAccountId=B&amount=-5`

## Expected result
- HTTP **400 Bad Request** with a validation message such as "Amount must be greater than zero".
- Balances of A and B unchanged.
- No transaction row created.

## Actual result
- HTTP **200 OK**: `Successfully transferred $-5 from account #A to account #B`
- Account A balance **increases** by 5 and account B balance **decreases** by 5.

## Evidence
- Automated: `AccountApiTest.negativeAmountShouldBeRejected` (TestNG group `known-defect`)
- Postman: folder **99 Known defects** → "DEF-001 Transfer - negative amount should be rejected"
- Run: `mvn test -Dgroups=known-defect -DsuiteXmlFile=src/test/resources/api-db.xml`

## Suggested fix
Validate `amount > 0` on the server before posting the debit/credit. The DB layer in this project shows the same rule enforced with `CHECK (amount > 0)` (see `TransactionDbTest.negativeAmountIsRejected`).

## Retest plan
Once fixed, move the test out of the `known-defect` group so it runs in regression, and re-run the Postman folder.
