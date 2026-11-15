# REQ-002: Fund Transfer

## Feature
Internal Fund Transfer — Move money between a customer's own accounts

## User Story
**As a** bank customer  
**I want to** transfer funds between my accounts  
**So that** I can manage my money across checking and savings accounts

---

## Acceptance Criteria

### AC-01: Transfer Page Accessibility
- **Given** an authenticated customer  
- **When** they navigate to "Transfer Funds" from the sidebar  
- **Then** the transfer form is displayed with: Amount field, From Account dropdown, To Account dropdown, Transfer button

### AC-02: Account Dropdowns Populated
- **Given** a customer with multiple accounts  
- **When** the Transfer Funds page loads  
- **Then** both "From Account" and "To Account" dropdowns list all the customer's account IDs  
- **And** the dropdowns are populated within 5 seconds (async load)

### AC-03: Successful Transfer
- **Given** a customer selects a valid source and destination account with sufficient funds  
- **When** they enter a positive amount and click "Transfer"  
- **Then** a confirmation message "Transfer Complete!" is displayed  
- **And** the transferred amount is shown in the confirmation  
- **And** the source account balance decreases by the transfer amount  
- **And** the destination account balance increases by the transfer amount

### AC-04: Zero Amount Validation
- **Given** a customer enters 0 or leaves the amount blank  
- **When** they submit the transfer form  
- **Then** an error message about invalid amount is displayed  
- **And** no transfer is executed

### AC-05: Insufficient Funds
- **Given** a customer attempts to transfer more than their available balance  
- **When** the transfer is submitted  
- **Then** an error message about insufficient funds is displayed

### AC-06: Same Account Transfer
- **Given** a customer selects the same account for both From and To  
- **When** they submit the transfer  
- **Then** the system should either prevent it or return a meaningful error

---

## Domain Notes
*From FLEXCUBE experience:* Fund transfers in core banking systems have
GL (General Ledger) entries on both debit and credit sides. API-level tests
should verify balance changes, not just the UI confirmation — a confirmation
screen can show "success" even if the GL posting fails silently.

## API Endpoint
`POST /parabank/services/bank/transfer?fromAccountId={}&toAccountId={}&amount={}`

## Test Data Requirements
- Customer with at least 2 accounts
- Minimum balance of $100 in source account for positive transfer tests
- A transfer amount of $10.00 (safe for repeated test runs)
