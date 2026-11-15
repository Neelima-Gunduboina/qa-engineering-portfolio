/**
 * billpay.spec.ts — Bill payment flows.
 *
 * Banking context: mirrors payment initiation in FLEXCUBE and Royal London
 * policy payment modules. Key validations: form completeness, payee data
 * integrity, confirmation page content.
 */

import { test, expect } from '../../fixtures';

test.describe('Bill Payment', () => {
  test.beforeEach(async ({ billPayPage }) => {
    await billPayPage.goto();
    await billPayPage.assertPageLoaded();
  });

  test('TC-B-01: bill payment page loads with complete form', async ({
    page,
  }) => {
    await expect(page.locator('input[name="payee.name"]')).toBeVisible();
    await expect(page.locator('input[name="payee.address.street"]')).toBeVisible();
    await expect(page.locator('input[name="amount"]')).toBeVisible();
    await expect(page.getByRole('button', { name: /send payment/i })).toBeVisible();
  });

  test('TC-B-02: bill payment submission with valid details succeeds', async ({
    page,
    billPayPage,
    testData,
  }) => {
    // Wait for account dropdown to populate
    await page.waitForFunction(
      () => (document.querySelector('select[name="fromAccountId"]') as HTMLSelectElement)?.options.length > 0,
      { timeout: 15_000 },
    );

    const bp = testData.billPayment;
    await billPayPage.fillPaymentForm({
      payeeName: bp.payeeName,
      address: bp.address,
      city: bp.city,
      state: bp.state,
      zipCode: bp.zipCode,
      phone: bp.phone,
      accountNumber: bp.accountNumber,
      verifyAccount: bp.accountNumber,
      amount: bp.amount,
    });

    // Select first available account
    await page.locator('select[name="fromAccountId"]').selectOption({ index: 0 });
    await billPayPage.submitPayment();

    await billPayPage.assertPaymentSuccess(bp.payeeName);
  });

  test('TC-B-03: mismatched account numbers show validation error', async ({
    page,
    billPayPage,
    testData,
  }) => {
    const bp = testData.billPayment;
    await billPayPage.fillPaymentForm({
      payeeName: bp.payeeName,
      address: bp.address,
      city: bp.city,
      state: bp.state,
      zipCode: bp.zipCode,
      phone: bp.phone,
      accountNumber: '11111',
      verifyAccount: '22222', // Intentional mismatch
      amount: bp.amount,
    });

    await billPayPage.submitPayment();

    await expect(page.locator('#validationModel-verifyAccount-mismatch')).toHaveText(
      /the account numbers do not match/i,
    );
  });

  test('TC-B-04: empty form submission shows required field errors', async ({
    billPayPage,
  }) => {
    await billPayPage.submitPayment();
    // Every required field shows its own validation message
    const page = billPayPage['page'];
    await expect(page.locator('#validationModel-name')).toBeVisible();
    await expect(page.locator('#validationModel-amount-empty')).toBeVisible();
  });
});
