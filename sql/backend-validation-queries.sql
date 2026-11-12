-- =====================================================================
-- Backend validation queries for fund-transfer testing
-- Written for Oracle SQL; they run against the schema in
-- src/test/resources/db/schema.sql (H2 in Oracle mode).
-- Typical use: run after a UI/API transfer to confirm the DB is correct.
-- =====================================================================

-- 1. Balance check: confirm the debit and credit landed on the right accounts
SELECT account_id, account_type, balance
FROM   accounts
WHERE  account_id IN (:from_acct, :to_acct);

-- 2. Audit trail: exactly one transaction row per transfer, with correct amount
SELECT txn_id, from_acct, to_acct, amount, description, created_at
FROM   transactions
WHERE  from_acct = :from_acct
AND    to_acct   = :to_acct
ORDER  BY created_at DESC
FETCH FIRST 1 ROWS ONLY;

-- 3. Reconciliation: total money in the system must not change after internal transfers
SELECT SUM(balance) AS total_balance FROM accounts;

-- 4. Data integrity: no account may be overdrawn
SELECT account_id, balance
FROM   accounts
WHERE  balance < 0;                          -- expected: 0 rows

-- 5. Data integrity: no zero or negative transaction amounts
SELECT txn_id, amount
FROM   transactions
WHERE  amount <= 0;                          -- expected: 0 rows

-- 6. Orphan check: every transaction references existing accounts
SELECT t.txn_id
FROM   transactions t
LEFT   JOIN accounts a_from ON a_from.account_id = t.from_acct
LEFT   JOIN accounts a_to   ON a_to.account_id   = t.to_acct
WHERE  a_from.account_id IS NULL
OR     a_to.account_id   IS NULL;            -- expected: 0 rows

-- 7. Duplicate detection: same transfer posted twice within one minute
SELECT from_acct, to_acct, amount, COUNT(*) AS postings
FROM   transactions
GROUP  BY from_acct, to_acct, amount, TRUNC(created_at, 'MI')
HAVING COUNT(*) > 1;                         -- expected: 0 rows

-- 8. Self-transfer check: from and to account must differ
SELECT txn_id
FROM   transactions
WHERE  from_acct = to_acct;                  -- expected: 0 rows (business rule)

-- 9. Customer position: total balance per customer and account type
SELECT customer_id,
       account_type,
       COUNT(*)     AS accounts,
       SUM(balance) AS total_balance
FROM   accounts
GROUP  BY customer_id, account_type
ORDER  BY customer_id, account_type;
