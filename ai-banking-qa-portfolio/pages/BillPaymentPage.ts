import { Page, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export interface BillPaymentDetails {
  payeeName: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  phone: string;
  accountNumber: string;
  verifyAccount: string;
  amount: string;
}

export class BillPaymentPage extends BasePage {
  private readonly payeeNameInput = () => this.page.locator('input[name="payee.name"]');
  private readonly addressInput = () => this.page.locator('input[name="payee.address.street"]');
  private readonly cityInput = () => this.page.locator('input[name="payee.address.city"]');
  private readonly stateInput = () => this.page.locator('input[name="payee.address.state"]');
  private readonly zipInput = () => this.page.locator('input[name="payee.address.zipCode"]');
  private readonly phoneInput = () => this.page.locator('input[name="payee.phoneNumber"]');
  private readonly accountInput = () => this.page.locator('input[name="payee.accountNumber"]');
  private readonly verifyAccountInput = () => this.page.locator('input[name="verifyAccount"]');
  private readonly amountInput = () => this.page.locator('input[name="amount"]');
  private readonly fromAccountSelect = () => this.page.locator('select[name="fromAccountId"]');
  private readonly sendPaymentButton = () =>
    this.page.getByRole('button', { name: /send payment/i });
  private readonly successHeading = () =>
    this.page.getByRole('heading', { name: /bill payment complete/i });

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await super.goto('/parabank/billpay.htm');
    await this.waitForReady();
  }

  async assertPageLoaded(): Promise<void> {
    await expect(
      this.page.getByRole('heading', { name: /bill payment/i }),
    ).toBeVisible();
  }

  async fillPaymentForm(details: BillPaymentDetails): Promise<void> {
    await this.payeeNameInput().fill(details.payeeName);
    await this.addressInput().fill(details.address);
    await this.cityInput().fill(details.city);
    await this.stateInput().fill(details.state);
    await this.zipInput().fill(details.zipCode);
    await this.phoneInput().fill(details.phone);
    await this.accountInput().fill(details.accountNumber);
    await this.verifyAccountInput().fill(details.verifyAccount);
    await this.amountInput().fill(details.amount);
  }

  async selectFromAccount(accountId: string): Promise<void> {
    await this.fromAccountSelect().selectOption(accountId);
  }

  async submitPayment(): Promise<void> {
    await this.sendPaymentButton().click();
  }

  async assertPaymentSuccess(payeeName: string): Promise<void> {
    await expect(this.successHeading()).toBeVisible({ timeout: 15_000 });
    await expect(this.page.getByText(payeeName)).toBeVisible();
  }
}
