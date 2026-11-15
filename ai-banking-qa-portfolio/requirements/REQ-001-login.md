# REQ-001: Customer Login

## Feature
Customer Authentication — Login to ParaBank online banking portal

## User Story
**As a** registered bank customer  
**I want to** log in to the online banking portal with my username and password  
**So that** I can access my accounts, view balances, and initiate transactions

---

## Acceptance Criteria

### AC-01: Successful Login
- **Given** a registered customer with valid username and password  
- **When** they enter their credentials and click "Log In"  
- **Then** they are redirected to the Accounts Overview page  
- **And** a welcome message displaying their name is shown  
- **And** the navigation sidebar shows links to all core banking modules

### AC-02: Invalid Credentials
- **Given** a customer who enters an incorrect username or password  
- **When** they submit the login form  
- **Then** an error message "Error! The username and password could not be verified." is displayed  
- **And** they remain on the login page  
- **And** the password field is cleared

### AC-03: Blank Credentials
- **Given** a customer who submits the login form with empty fields  
- **When** the form is submitted  
- **Then** validation errors appear for the required fields  
- **And** no network request is made to the authentication endpoint

### AC-04: Password Field Security
- **Given** a customer entering their password  
- **When** they type in the password field  
- **Then** the characters are masked (input type="password")  
- **And** the password is not visible in the page source or network requests

### AC-05: Remember Me (Out of Scope)
- ParaBank demo does not implement persistent sessions — excluded from automation scope

### AC-06: Logout
- **Given** an authenticated customer  
- **When** they click "Log Out" in the navigation  
- **Then** the session is terminated  
- **And** they are redirected to the login page  
- **And** navigating back does not restore the session (no cached auth page)

---

## Non-Functional Requirements
- Login response time: < 3 seconds on standard connection
- Brute-force protection: Out of scope for demo app
- HTTPS: All credential transmission over TLS (demo site certificate may be self-signed)

## Domain Notes
*From FLEXCUBE experience:* Core banking login is the gateway to all modules.
Authentication failures in UAT were often caused by user account status (locked, expired)
rather than wrong credentials — worth having test data that covers account states.

## Test Data Requirements
- At least one registered account (`john/demo` on ParaBank demo)
- A non-existent username for negative testing
- An existing username with a wrong password
