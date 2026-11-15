/**
 * snapshots.spec.ts — Visual regression tests using Playwright's built-in
 * screenshot comparison. Baselines are committed to source control.
 *
 * Why visual testing matters in banking:
 * UI changes in banking portals can have regulatory implications (required
 * disclosures, accessibility). Visual regression catches unintended layout
 * changes that functional tests miss.
 *
 * Run `npm run update-snapshots` to update baselines after intentional changes.
 */

import { test, expect } from '@playwright/test';

test.describe('Visual Regression', () => {
  test.describe('anonymous', () => {
    test.use({ storageState: { cookies: [], origins: [] } }); // the login page is for signed-out visitors

    test('TC-VIS-01: login page matches baseline', async ({ page }) => {
      await page.goto('/parabank/index.htm');
      await page.waitForLoadState('networkidle');
      // Mask the "Latest News" date, which changes every day
      await expect(page).toHaveScreenshot('login-page.png', {
        maxDiffPixelRatio: 0.02,
        mask: [page.locator('ul.events li.captionthree')],
      });
    });
  });

  test('TC-VIS-02: accounts overview matches baseline', async ({ page }) => {
    await page.goto('/parabank/overview.htm');
    await page.waitForLoadState('networkidle');
    // Hide the account rows: their number and balances change as tests run,
    // which would change the page height as well as the pixels
    await page.addStyleTag({ content: '#accountTable tbody tr { display: none; }' });
    await expect(page).toHaveScreenshot('accounts-overview.png', { maxDiffPixelRatio: 0.03 });
  });

  test('TC-VIS-03: transfer funds page matches baseline', async ({ page }) => {
    await page.goto('/parabank/transfer.htm');
    await page.waitForLoadState('networkidle');
    await page.waitForFunction(
      () => (document.querySelector('#fromAccountId') as HTMLSelectElement)?.options.length > 0,
      { timeout: 10_000 },
    ).catch(() => null); // Non-fatal if accounts don't load

    await expect(page).toHaveScreenshot('transfer-funds-page.png', {
      maxDiffPixelRatio: 0.02,
      mask: [page.locator('#fromAccountId'), page.locator('#toAccountId')],
    });
  });

  test('TC-VIS-04: bill payment page matches baseline', async ({ page }) => {
    await page.goto('/parabank/billpay.htm');
    await page.waitForLoadState('networkidle');

    await expect(page).toHaveScreenshot('bill-payment-page.png', {
      maxDiffPixelRatio: 0.02,
      mask: [page.locator('select[name="fromAccountId"]')],
    });
  });
});
