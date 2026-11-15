import { Page, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class TransferFundsPage extends BasePage {
  private readonly amountInput = () =>
    this.page.locator('#amount');
  private readonly fromAccountSelect = () =>
    this.page.locator('#fromAccountId');
  private readonly toAccountSelect = () =>
    this.page.locator('#toAccountId');
  private readonly transferButton = () =>
    this.page.getByRole('button', { name: /transfer/i });
  private readonly successHeading = () =>
    this.page.getByRole('heading', { name: /transfer complete/i });
  private readonly errorMessage = () =>
    this.page.locator('.error, #error-container');

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await super.goto('/parabank/transfer.htm');
    await this.waitForReady();
  }

  async assertPageLoaded(): Promise<void> {
    await expect(this.page.getByRole('heading', { name: /transfer funds/i })).toBeVisible();
    await expect(this.amountInput()).toBeVisible();
  }

  /** Transfer an amount from one account to another by displayed option text. */
  async transfer(
    amount: string,
    fromAccount: string,
    toAccount: string,
  ): Promise<void> {
    await this.amountInput().fill(amount);
    await this.fromAccountSelect().selectOption({ label: fromAccount });
    await this.toAccountSelect().selectOption({ label: toAccount });
    await this.transferButton().click();
  }

  async assertTransferSuccess(amount: string): Promise<void> {
    await expect(this.successHeading()).toBeVisible({ timeout: 15_000 });
    await expect(this.page.getByText(amount)).toBeVisible();
  }

  async assertTransferError(): Promise<void> {
    await expect(this.errorMessage()).toBeVisible();
  }
}
