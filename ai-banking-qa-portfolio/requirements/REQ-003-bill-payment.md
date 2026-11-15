# REQ-003: Bill Payment

## Feature
Bill Payment — Pay an external payee from a bank account

## User Story
**As a** bank customer  
**I want to** pay my bills directly from my bank account  
**So that** I can manage recurring payments without leaving the banking portal

---

## Acceptance Criteria

### AC-01: Bill Payment Form
- **Given** an authenticated customer  
- **When** they navigate to "Bill Payment"  
- **Then** the form displays fields for: Payee Name, Address, City, State, Zip Code, Phone Number, Account Number, Verify Account Number, Amount, From Account (dropdown)

### AC-02: Successful Payment
- **Given** a customer fills all required fields with valid data  
- **And** the account numbers match  
- **When** they click "Send Payment"  
- **Then** a "Bill Payment Complete!" confirmation heading is displayed  
- **And** the payee name and amount are shown in the confirmation  
- **And** the source account balance is reduced by the payment amount

### AC-03: Account Number Mismatch
- **Given** a customer enters different values in "Account Number" and "Verify Account Number"  
- **When** they submit the form  
- **Then** a validation error "Account numbers do not match" is displayed  
- **And** the payment is not processed

### AC-04: Required Field Validation
- **Given** a customer submits the form without filling required fields  
- **When** the form is submitted  
- **Then** inline validation errors appear next to each empty required field  
- **And** the errors describe what is expected (e.g., "Payee name is required")

### AC-05: Payment Amount Validation
- **Given** a customer enters a non-numeric or zero amount  
- **When** the form is submitted  
- **Then** an appropriate validation message is shown

---

## Domain Notes
*From Royal London experience:* Payment instructions in insurance/financial
systems have strict audit requirements. Every submitted payment should be
traceable via a reference number or transaction ID shown on the confirmation.

*From FLEXCUBE experience:* Bill payments create outgoing payment entries.
If the confirmation shows the wrong amount, it may indicate a rounding error
in the backend — always assert the exact amount in confirmation text.

## Test Data — Payee
Use realistic but fictional payee data:
- Name: "BT Broadband"
- Account: 12345
- Amount: $50.00
