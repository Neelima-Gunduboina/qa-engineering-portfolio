# FT-101: Transfer money between my own accounts

**As a** personal banking customer
**I want to** transfer money between my own accounts in the mobile app
**So that** I can move money to where I need it

## Acceptance criteria

1. The customer chooses a "from" account, a "to" account and an amount in pounds and pence.
2. The amount must be greater than £0.00 and no more than £25,000.00 per day across all transfers.
3. The "from" and "to" accounts must be different.
4. The transfer is rejected if the "from" account does not have enough available balance (including any arranged overdraft).
5. On success, the "from" balance goes down and the "to" balance goes up by exactly the amount, and both accounts show the transaction in their history.
6. A failed transfer changes neither balance.
7. The customer sees a confirmation screen with a reference number.
