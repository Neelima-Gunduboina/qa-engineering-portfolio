/**
 * fixtures/index.ts — Custom Playwright fixtures.
 *
 * Every test imports { test, expect } from here instead of @playwright/test
 * to get the enriched fixture set automatically.
 *
 * Fixtures provided:
 * - loginPage, dashboardPage, accountsPage, transferPage, billPayPage
 *   (pre-constructed page objects bound to the test's page)
 * - authenticatedPage  (page already logged in via storage state)
 * - testData           (typed test data factory)
 */

import { test as base, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { AccountsPage } from '../pages/AccountsPage';
import { TransferFundsPage } from '../pages/TransferFundsPage';
import { BillPaymentPage } from '../pages/BillPaymentPage';
import { RegisterPage } from '../pages/RegisterPage';
import { makeTestData, TestData } from './testData';

type Fixtures = {
  loginPage: LoginPage;
  dashboardPage: DashboardPage;
  accountsPage: AccountsPage;
  transferPage: TransferFundsPage;
  billPayPage: BillPaymentPage;
  registerPage: RegisterPage;
  testData: TestData;
};

export const test = base.extend<Fixtures>({
  loginPage: async ({ page }, use) => {
    await use(new LoginPage(page));
  },
  dashboardPage: async ({ page }, use) => {
    await use(new DashboardPage(page));
  },
  accountsPage: async ({ page }, use) => {
    await use(new AccountsPage(page));
  },
  transferPage: async ({ page }, use) => {
    await use(new TransferFundsPage(page));
  },
  billPayPage: async ({ page }, use) => {
    await use(new BillPaymentPage(page));
  },
  registerPage: async ({ page }, use) => {
    await use(new RegisterPage(page));
  },
  testData: async ({}, use) => {
    await use(makeTestData());
  },
});

export { expect };
