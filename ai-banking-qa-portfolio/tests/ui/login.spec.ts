/**
 * login.spec.ts — Login / authentication journeys.
 *
 * Maps to manual test experience:
 * - FLEXCUBE: user authentication gateway before accessing core banking modules
 * - Royal London: portal login with credential validation
 *
 * Covers: positive login, negative login, logout, session management.
 */

import { test, expect } from '../../fixtures';

test.describe('Login', () => {
  test.use({ storageState: { cookies: [], origins: [] } }); // Force unauthenticated

  test('TC-L-01: successful login with valid credentials', async ({
    page,
    loginPage,
    testData,
  }) => {
    await loginPage.goto();
    await loginPage.login(
      testData.credentials.validUsername,
      testData.credentials.validPassword,
    );
    // Verify we're on the accounts page post-login
    await expect(
      page.getByRole('link', { name: /accounts overview/i }),
    ).toBeVisible();
  });

  test('TC-L-02: login fails with invalid credentials', async ({
    loginPage,
    testData,
  }) => {
    // Regression guard for DEF-002: on 14 Nov 2026 ParaBank signed unknown
    // users in as another customer. Not reproducible since the demo reset.
    test.info().annotations.push({
      type: 'regression-guard',
      description:
        'DEF-002 https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/blob/main/docs/defects/DEF-002-login-accepts-unknown-credentials.md',
    });
    await loginPage.goto();
    await loginPage.loginExpectFailure(
      testData.credentials.invalidUsername,
      testData.credentials.invalidPassword,
    );
    await loginPage.assertLoginError(/could not be verified/i);
  });

  test('TC-L-03: login fails with valid username and wrong password', async ({
    loginPage,
    testData,
  }) => {
    await loginPage.goto();
    await loginPage.loginExpectFailure(
      testData.credentials.validUsername,
      testData.credentials.invalidPassword,
    );
    await loginPage.assertLoginError(/could not be verified/i);
  });

  test('TC-L-04: login fails with blank credentials', async ({
    loginPage,
  }) => {
    await loginPage.goto();
    await loginPage.loginExpectFailure('', '');
    await loginPage.assertLoginError(/please enter a username and password/i);
  });

  test('TC-L-05: login page renders required form elements', async ({
    loginPage,
    page,
  }) => {
    await loginPage.goto();
    await expect(loginPage.usernameInput()).toBeVisible();
    await expect(loginPage.passwordInput()).toBeVisible();
    await expect(loginPage.loginButton()).toBeVisible();
    await expect(page.getByRole('link', { name: /register/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /forgot/i })).toBeVisible();
  });
});

test.describe('Logout', () => {
  // Log in with a separate session: logging out of the shared storageState
  // session would sign every other test in the run out too.
  test.use({ storageState: { cookies: [], origins: [] } });

  test('TC-L-06: authenticated user can log out', async ({
    page,
    loginPage,
    dashboardPage,
    testData,
  }) => {
    await loginPage.goto();
    await loginPage.login(testData.credentials.validUsername, testData.credentials.validPassword);
    await expect(
      page.getByRole('link', { name: /accounts overview/i }),
    ).toBeVisible();

    await dashboardPage.logout();

    // Verify session terminated — login form visible
    await expect(page.locator('input[name="username"]')).toBeVisible();
  });
});
