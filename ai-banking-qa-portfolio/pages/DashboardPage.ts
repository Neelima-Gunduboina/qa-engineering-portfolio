import { Page, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class DashboardPage extends BasePage {
  private readonly welcomeHeading = () =>
    this.page.locator('#rightPanel h1, #rightPanel .title').first();
  private readonly accountsOverviewLink = () =>
    this.page.getByRole('link', { name: /accounts overview/i });
  private readonly transferFundsLink = () =>
    this.page.getByRole('link', { name: /transfer funds/i });
  private readonly billPaymentLink = () =>
    this.page.getByRole('link', { name: 'Bill Pay', exact: true });
  private readonly openAccountLink = () =>
    this.page.getByRole('link', { name: /open new account/i });
  private readonly logoutLink = () =>
    this.page.getByRole('link', { name: /log out/i });

  constructor(page: Page) {
    super(page);
  }

  async assertWelcomed(username?: string): Promise<void> {
    if (username) {
      await expect(
        this.page.getByText(new RegExp(username, 'i')),
      ).toBeVisible();
    } else {
      await expect(this.welcomeHeading()).toBeVisible();
    }
  }

  async assertNavigationLinksVisible(): Promise<void> {
    await expect(this.accountsOverviewLink()).toBeVisible();
    await expect(this.transferFundsLink()).toBeVisible();
    await expect(this.billPaymentLink()).toBeVisible();
  }

  async goToAccountsOverview(): Promise<void> {
    await this.accountsOverviewLink().click();
    await this.waitForReady();
  }

  async goToTransferFunds(): Promise<void> {
    await this.transferFundsLink().click();
    await this.waitForReady();
  }

  async goToBillPayment(): Promise<void> {
    await this.billPaymentLink().click();
    await this.waitForReady();
  }

  async goToOpenAccount(): Promise<void> {
    await this.openAccountLink().click();
    await this.waitForReady();
  }

  async logout(): Promise<void> {
    await this.logoutLink().click();
    await this.page.waitForURL(/index\.htm/i);
  }
}
