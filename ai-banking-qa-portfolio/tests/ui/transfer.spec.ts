/**
 * transfer.spec.ts — Fund transfer between accounts.
 *
 * Banking context: directly mirrors the FLEXCUBE internal fund transfer
 * module used at client banks — same flow: select source, destination, amount,
 * submit, verify confirmation. Negative paths test validation rules.
 */

import { test, expect } from '../../fixtures';

test.describe('Fund Transfer', () => {
  test.beforeEach(async ({ transferPage }) => {
    await transferPage.goto();
    await transferPage.assertPageLoaded();
  });

  test('TC-T-01: transfer page loads with required form elements', async ({
    page,
  }) => {
    await expect(page.locator('#amount')).toBeVisible();
    await expect(page.locator('#fromAccountId')).toBeVisible();
    await expect(page.locator('#toAccountId')).toBeVisible();
    await expect(page.getByRole('button', { name: /transfer/i })).toBeVisible();
  });

  test('TC-T-02: transfer dropdowns are populated with accounts', async ({
    page,
  }) => {
    // Wait for async account load
    await page.waitForFunction(
      () => (document.querySelector('#fromAccountId') as HTMLSelectElement)?.options.length > 0,
      { timeout: 10_000 },
    );
    const fromOptions = await page.locator('#fromAccountId option').count();
    const toOptions = await page.locator('#toAccountId option').count();
    expect(fromOptions).toBeGreaterThan(0);
    expect(toOptions).toBeGreaterThan(0);
  });

  test('TC-T-03: transfer with zero amount shows validation error', async ({
    page,
  }) => {
    // Known defect: ParaBank has no amount validation and completes a $0.00
    // transfer. test.fail() keeps CI green and flags when it is fixed.
    test.info().annotations.push({
      type: 'known-defect',
      description:
        'DEF-004 https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/blob/main/docs/defects/DEF-004-zero-and-same-account-transfer.md',
    });
    test.fail();
    await page.waitForFunction(
      () => (document.querySelector('#fromAccountId') as HTMLSelectElement)?.options.length > 0,
    );
    await page.locator('#amount').fill('0');
    await page.getByRole('button', { name: /transfer/i }).click();
    await expect(page.getByText(/please enter a valid amount/i)).toBeVisible({ timeout: 10_000 });
  });

  test('TC-T-04: transfer form shows a caption for every field', async ({
    page,
  }) => {
    // Captions are checked inside the form only: the page also holds hidden
    // error messages that mention "amount". Programmatic label association
    // (WCAG 2.1 AA) is checked separately by the axe-core suite in tests/a11y.
    const form = page.locator('#transferForm');
    await expect(form.getByText('Amount:')).toBeVisible();
    await expect(form.getByText(/from account #/i)).toBeVisible();
    await expect(form.getByText(/to account #/i)).toBeVisible();
  });

  test('TC-T-05: successful fund transfer completes with confirmation', async ({
    page,
  }) => {
    // Wait for accounts to load
    await page.waitForFunction(
      () => (document.querySelector('#fromAccountId') as HTMLSelectElement)?.options.length > 1,
      { timeout: 15_000 },
    );

    // Get account IDs from dropdowns
    const fromOptions = await page.locator('#fromAccountId option').allTextContents();
    const toOptions = await page.locator('#toAccountId option').allTextContents();

    // Need at least 2 accounts for a different-account transfer
    if (fromOptions.length < 2 || toOptions.length < 2) {
      test.skip(true, 'Need at least 2 accounts for inter-account transfer');
      return;
    }

    await page.locator('#amount').fill('10.00');
    await page.locator('#fromAccountId').selectOption({ index: 0 });
    await page.locator('#toAccountId').selectOption({ index: 1 });
    await page.getByRole('button', { name: /transfer/i }).click();

    await expect(
      page.getByRole('heading', { name: /transfer complete/i }),
    ).toBeVisible({ timeout: 20_000 });

    await expect(page.getByText(/10\.00/)).toBeVisible();
  });
});
