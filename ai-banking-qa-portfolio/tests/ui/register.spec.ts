/**
 * register.spec.ts — New customer registration.
 *
 * Banking context: onboarding / KYC data entry — same category of form
 * validation tests performed in insurance (Royal London) for policy holder
 * registration. Tests data integrity, field validation, and duplicate prevention.
 */

import { test, expect } from '../../fixtures';

test.describe('Customer Registration', () => {
  test.use({ storageState: { cookies: [], origins: [] } }); // Unauthenticated

  test('TC-R-01: registration page renders complete form', async ({
    registerPage,
    page,
  }) => {
    await registerPage.goto();
    await expect(page.locator('input[name="customer.firstName"]')).toBeVisible();
    await expect(page.locator('input[name="customer.lastName"]')).toBeVisible();
    await expect(page.locator('input[name="customer.username"]')).toBeVisible();
    await expect(page.locator('input[name="customer.password"]')).toBeVisible();
    await expect(page.getByRole('button', { name: /register/i })).toBeVisible();
  });

  test('TC-R-02: successful registration with unique username', async ({
    registerPage,
    testData,
  }) => {
    await registerPage.goto();

    const reg = testData.registration;
    await registerPage.fillRegistrationForm({
      firstName: reg.firstName,
      lastName: reg.lastName,
      address: reg.address,
      city: reg.city,
      state: reg.state,
      zipCode: reg.zipCode,
      phone: reg.phone,
      ssn: reg.ssn,
      username: reg.uniqueUsername(),
      password: reg.password,
    });

    await registerPage.submit();
    await registerPage.assertRegistrationSuccess();
  });

  test('TC-R-03: empty form submission shows required field errors', async ({
    registerPage,
    page,
  }) => {
    await registerPage.goto();
    await registerPage.submit();

    // The server re-renders the form with one message per required field
    const errors = page.locator('#rightPanel span.error');
    await expect(errors.first()).toBeVisible();
    await expect(page.locator('[id="customer.firstName.errors"]')).toHaveText(/first name is required/i);
    expect(await errors.count()).toBeGreaterThan(5);
  });

  test('TC-R-05: a new username over 20 characters is not reported as a duplicate', async ({
    registerPage,
    page,
    testData,
  }) => {
    // Known defect: a never-used 21+ character username is rejected with
    // "This username already exists" instead of a length message.
    test.info().annotations.push({
      type: 'known-defect',
      description:
        'DEF-005 https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/blob/main/docs/defects/DEF-005-long-username-reported-as-duplicate.md',
    });
    test.fail();
    const reg = testData.registration;
    const longUsername = `${reg.uniqueUsername()}abcdef`; // 26 characters, never used

    await registerPage.goto();
    await registerPage.fillRegistrationForm({ ...reg, username: longUsername, password: reg.password });
    await registerPage.submit();

    // Wait for the server's response page, then check it does not give the false reason.
    // Fixed behaviour is either "account created" or a clear length message.
    await page.waitForLoadState();
    await expect(page.locator('#rightPanel h1')).toBeVisible();
    await expect(page.getByText(/this username already exists/i)).toHaveCount(0);
  });

  test('TC-R-04: register link visible from login page', async ({
    loginPage,
    page,
  }) => {
    await loginPage.goto();
    await loginPage.clickRegister();
    await expect(page.getByRole('heading', { name: /signing up/i })).toBeVisible({ timeout: 10_000 });
  });
});
