/**
 * dashboard.spec.ts — Post-login dashboard and accounts overview.
 *
 * Banking context: mirrors FLEXCUBE account summary screen where a user sees
 * their accounts, balances, and available navigation to key modules.
 */

import { test, expect } from '../../fixtures';

test.describe('Dashboard & Accounts Overview', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/parabank/overview.htm');
    await page.waitForLoadState('networkidle');
  });

  test('TC-D-01: accounts overview page loads with account data', async ({
    accountsPage,
  }) => {
    await accountsPage.assertPageLoaded();
    await accountsPage.assertAtLeastOneAccount();
  });

  test('TC-D-02: navigation sidebar contains all key banking links', async ({
    dashboardPage,
  }) => {
    await dashboardPage.assertNavigationLinksVisible();
  });

  test('TC-D-03: accounts table shows account IDs and balances', async ({
    accountsPage,
  }) => {
    await accountsPage.assertPageLoaded();
    const accounts = await accountsPage.getAccounts();

    expect(accounts.length).toBeGreaterThan(0);

    for (const account of accounts) {
      expect(account.id).toBeTruthy();
      // Balance should be a monetary value ($x,xxx.xx format)
      expect(account.balance).toMatch(/\$[\d,]+\.\d{2}/);
    }
  });

  test('TC-D-04: clicking an account navigates to account detail', async ({
    accountsPage,
    page,
  }) => {
    const accounts = await accountsPage.getAccounts();
    expect(accounts.length).toBeGreaterThan(0);

    await accountsPage.clickAccount(accounts[0].id);
    // Account activity page should load
    await expect(page.getByRole('heading', { name: /account activity/i })).toBeVisible();
  });

  test('TC-D-05: total balance is displayed in footer', async ({
    accountsPage,
  }) => {
    await accountsPage.assertPageLoaded();
    const total = await accountsPage.getTotalBalance();
    expect(total).toMatch(/\$[\d,]+\.\d{2}/);
  });
});
