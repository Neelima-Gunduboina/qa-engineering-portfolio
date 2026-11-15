# DEF-005: Usernames longer than 20 characters are rejected as "already exists"

| Field | Value |
|---|---|
| **ID** | DEF-005 |
| **Component** | ParaBank web UI – Register (`POST /parabank/register.htm`), username validation |
| **Severity** | Medium (customers cannot sign up and are given a false reason) |
| **Priority** | P2 |
| **Status** | Open |
| **Environment** | https://parabank.parasoft.com (public demo), found 15 Nov 2026 |
| **Found by** | Investigating a failing registration test whose username was unique (`neelima_<13-digit timestamp>`, 21 characters) |

## Summary
Registering with a username of **21 or more characters** fails with *"This username already exists."*, even when
no customer has that username. There is no maximum-length hint on the form and no length-specific message,
so the customer is told something untrue and has no way to work out the real rule (20 characters maximum).

## Steps to reproduce
1. Open https://parabank.parasoft.com/parabank/register.htm in a new session.
2. Fill every field with valid data and choose a never-used username of 21 characters, e.g. `cvotbkqbmjofwfnhu1858`.
3. Click **Register**.

## Expected result
Either the account is created, or a clear validation message such as *"Username must be 20 characters or fewer"*,
ideally with `maxlength="20"` on the input so the limit cannot be exceeded.

## Actual result
*"This username already exists."* under the Username field; no account is created.

Boundary check on 15 Nov 2026 (fresh random usernames, one new session each):

| Length | Username | Result |
|---|---|---|
| 18 | `cvotbkqbmjofwf1858` | created |
| 19 | `cvotbkqbmjofwfn1858` | created |
| 20 | `cvotbkqbmjofwfnh1858` | created |
| **21** | `cvotbkqbmjofwfnhu1858` | **"already exists"** ❌ |
| 22 | `cvotbkqbmjofwfnhuy1858` | **"already exists"** ❌ |
| 25 | `cvotbkqbmjofwfnhuyqns1858` | **"already exists"** ❌ |

## Evidence
- Java (group `known-defect`): `AccountApiTest.longUsernameIsNotReportedAsDuplicate`
- Playwright: `TC-R-05` in [ai-banking-qa-portfolio](https://github.com/Neelima-Gunduboina/ai-banking-qa-portfolio) (marked `test.fail()`)
- Run: `mvn test -Pknown-defects`

## Suggested fix
Validate the length explicitly and return a length error before the duplicate check; add `maxlength="20"` to the
input. The current behaviour suggests a failed insert into a 20-character column is being reported as a duplicate.

## Investigation note
This was first logged as "usernames that start with an existing username are rejected", because the failing
examples happened to extend existing names. A boundary-value test on length with brand-new random usernames
showed the real cause; the report was corrected the same day. Test-data generators in both suites now keep
usernames at 20 characters or fewer.
