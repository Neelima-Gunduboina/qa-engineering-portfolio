/**
 * auth.setup.ts — Global setup that signs in once and saves storage state.
 * All authenticated test projects depend on this setup (see playwright.config.ts).
 * This avoids logging in before every single test, making the suite much faster.
 *
 * The public demo resets its data regularly, so by default the setup registers
 * a brand-new customer through the UI (which also logs it in). Set
 * PARABANK_USERNAME / PARABANK_PASSWORD to use an existing customer instead.
 */

import { test as setup, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { makeTestData } from '../fixtures/testData';
import { envCredentials, saveCredentials, uniqueUsername } from '../utils/test-user';

const AUTH_FILE = 'playwright/.auth/user.json';

setup('authenticate', async ({ page }) => {
  const existing = envCredentials();

  if (existing) {
    await new LoginPage(page).goto();
    await new LoginPage(page).login(existing.username, existing.password);
    saveCredentials(existing);
  } else {
    const data = makeTestData().registration;
    const credentials = { username: uniqueUsername('pw'), password: data.password };
    const register = new RegisterPage(page);
    await register.goto();
    await register.fillRegistrationForm({ ...data, ...credentials });
    await register.submit();
    await register.assertRegistrationSuccess();
    saveCredentials(credentials);

    // A new customer starts with one account; open a SAVINGS account too so
    // transfer tests have a second account to move money into.
    await page.goto('/parabank/openaccount.htm');
    await page.locator('#type').selectOption('SAVINGS');
    await page.waitForFunction(
      () => (document.querySelector('#fromAccountId') as HTMLSelectElement)?.options.length > 0,
    );
    await page.getByRole('button', { name: /open new account/i }).click();
    await expect(page.locator('#newAccountId')).toBeVisible({ timeout: 20_000 });
  }

  // Verify we are signed in, then persist cookies + localStorage
  await expect(page.getByRole('link', { name: /log out/i })).toBeVisible({ timeout: 20_000 });
  await page.context().storageState({ path: AUTH_FILE });
});
