// =============================================================================
// AI-GENERATED TEST SPEC — REQUIRES HUMAN REVIEW BEFORE USE IN CI
// =============================================================================
// Source requirement: requirements/REQ-001-login.md
// Generated with scripts/generate-tests.ts (Claude), then reviewed by hand.
//
// REVIEW (2026-11-15), after running the draft against the live site:
// [!] The draft used the john/demo account, which no longer exists (the demo
//     resets) — tests now use the customer registered by tests/auth.setup.ts
// [!] The LLM located inputs with getByLabel('Username'/'Password'), but
//     ParaBank's login inputs have no <label> (see docs/accessibility-findings.md
//     A11Y-02), so those locators matched nothing — replaced with name selectors.
// Lesson: "looks plausible" is not "verified" — run generated tests against the
// real application before trusting them.
// =============================================================================

/**
 * REQ-001: Customer Login — AI-generated spec, human-reviewed.
 *
 * This file demonstrates the AI test generation workflow:
 * 1. Requirements in /requirements/REQ-001-login.md were fed to the LLM
 * 2. LLM produced this draft spec covering all acceptance criteria
 * 3. QA engineer reviewed: verified selectors, error messages, flows
 * 4. Minor adjustments made (error message text, timeout values)
 *
 * The human-in-the-loop step is critical: LLMs generate plausible-looking
 * tests that may have incorrect selectors or wrong assertions. QA judgement
 * remains the final quality gate.
 */

import { test, expect } from '../../../fixtures';

test.describe('REQ-001: Customer Login (AI-generated, human-reviewed)', () => {
  test.use({ storageState: { cookies: [], origins: [] } });

  // AC-01: Successful Login
  test('TC-REQ001-AC01: successful login redirects to accounts overview', async ({
    page,
    loginPage,
    testData,
  }) => {
    // Validates the happy path — the most critical banking authentication flow
    await loginPage.goto();
    await loginPage.login(
      testData.credentials.validUsername,
      testData.credentials.validPassword,
    );
    // Post-login: verify accounts overview is accessible
    await expect(
      page.getByRole('link', { name: /accounts overview/i }),
    ).toBeVisible({ timeout: 15_000 });
  });

  // AC-02: Invalid Credentials
  test('TC-REQ001-AC02: invalid username and password shows error message', async ({
    loginPage,
    testData,
  }) => {
    // Security test: confirms system rejects invalid credentials gracefully
    await loginPage.goto();
    await loginPage.loginExpectFailure(
      testData.credentials.invalidUsername,
      testData.credentials.invalidPassword,
    );
    await loginPage.assertLoginError();
  });

  // AC-02 variant: correct username, wrong password
  test('TC-REQ001-AC02b: correct username with wrong password shows error', async ({
    loginPage,
    testData,
  }) => {
    await loginPage.goto();
    await loginPage.loginExpectFailure(
      testData.credentials.validUsername,
      'thisIsNotTheRightPassword!',
    );
    await loginPage.assertLoginError();
  });

  // AC-03: Blank Credentials
  test('TC-REQ001-AC03: empty form submission shows required field errors', async ({
    loginPage,
    page,
  }) => {
    await loginPage.goto();
    await loginPage.loginExpectFailure('', '');
    // Should show error — either validation or auth failure
    const errorOrValidation = page.locator('.error, [class*="error"], .ng-invalid');
    await expect(errorOrValidation.first()).toBeVisible({ timeout: 5_000 });
  });

  // AC-04: Password Field Security
  test('TC-REQ001-AC04: password input type is "password" (characters masked)', async ({
    loginPage,
    page,
  }) => {
    await loginPage.goto();
    // Reviewer fix: the LLM assumed a <label>; ParaBank has none (see a11y tests)
    const passwordInput = page.locator('input[name="password"]');
    await expect(passwordInput).toBeVisible();
    // Verify password masking at DOM level — a security regression test
    const inputType = await passwordInput.getAttribute('type');
    expect(inputType).toBe('password');
  });

  // AC-06: Logout
  test('TC-REQ001-AC06: authenticated user can log out and session is terminated', async ({
    page,
    loginPage,
    dashboardPage,
    testData,
  }) => {
    // Login first (can't use storageState since we need to test the logout flow)
    await loginPage.goto();
    await loginPage.login(
      testData.credentials.validUsername,
      testData.credentials.validPassword,
    );
    await expect(
      page.getByRole('link', { name: /accounts overview/i }),
    ).toBeVisible({ timeout: 15_000 });

    // Logout
    await dashboardPage.logout();

    // Verify session terminated
    await expect(page.locator('input[name="username"]')).toBeVisible();
  });
});
