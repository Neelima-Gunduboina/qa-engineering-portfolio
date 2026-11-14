# DEF-002: Web login accepts unknown credentials and signs in as another customer

| Field | Value |
|---|---|
| **ID** | DEF-002 |
| **Component** | ParaBank web UI – `POST /parabank/login.htm` |
| **Severity** | Critical (authentication bypass; another customer's accounts are exposed) |
| **Priority** | P1 |
| **Status** | Open |
| **Environment** | https://parabank.parasoft.com (public demo), found 14 Nov 2026 |
| **Found by** | Negative UI testing (`LoginTest.invalidCredentialsAreRejected`) |

## Summary
Submitting the login form with a username that does not exist and a wrong password does **not** show an error.
Instead the visitor lands on **Accounts Overview, logged in as the customer who most recently registered** on the site.

The REST endpoint for the same credentials behaves correctly (`GET /services/bank/login/{user}/{pass}` returns
`400 Invalid username and/or password`), so the defect is specific to the web login flow.

## Steps to reproduce
1. Open a new private browser window (no existing session).
2. Go to https://parabank.parasoft.com/parabank/index.htm
3. Enter username `zz_does_not_exist_123` and password `wrong`, then click **Log In**.

## Expected result
- The user stays on the login page with the error *"The username and password could not be verified."*
- No session is created.

## Actual result
- The page title is **ParaBank | Accounts Overview**.
- The left panel shows *"Welcome &lt;first name&gt; &lt;last name&gt;"* of a different, real customer, whose accounts and balances are visible.
- Reproduced 3/3 times with fresh sessions and random credentials.

## Impact
- Any visitor can see and act on another customer's accounts without credentials.
- It also suggests the server keeps a single "current customer" shared across sessions. This explains intermittent
  failures in the end-to-end UI tests when other people use the public demo at the same time, so those tests retry once
  (`RetryOnce`) and the UI suites run sequentially.

## Evidence
- Automated: `LoginTest.invalidCredentialsAreRejected` (TestNG group `known-defect`) times out waiting for the error message.
- Manual check with curl:
  ```bash
  B=https://parabank.parasoft.com/parabank; J=$(mktemp)
  curl -s -c $J -b $J $B/index.htm -o /dev/null
  curl -s -L -c $J -b $J -X POST $B/login.htm --data "username=zz$RANDOM&password=wrong" | grep -E "<title>|Welcome</b>"
  ```

## Suggested fix
Reject the login when no customer matches the credentials, and never fall back to a previously loaded customer.
Customer state should be scoped to the HTTP session, not held in a shared object.

## Retest plan
Once fixed, move the test back into the `regression` group and run `mvn test -Pregression`.
