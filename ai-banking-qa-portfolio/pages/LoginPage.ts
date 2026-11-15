import { Page, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class LoginPage extends BasePage {
  // Selectors — role-based where the markup allows it. ParaBank's login inputs
  // have no <label> (the captions are plain <b> text), so they are located by
  // name; tests/a11y flags the missing labels as an accessibility defect.
  readonly usernameInput = () => this.locator('input[name="username"]');
  readonly passwordInput = () => this.locator('input[name="password"]');
  readonly loginButton = () =>
    this.getByRole('button', { name: /log in/i });
  private readonly errorMessage = () => this.locator('#rightPanel p.error');
  private readonly registerLink = () =>
    this.page.getByRole('link', { name: /register/i });

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await super.goto('/parabank/index.htm');
    await this.waitForReady();
  }

  /** Login with credentials; resolves when the dashboard is loaded. */
  async login(username: string, password: string): Promise<void> {
    await this.usernameInput().fill(username);
    await this.passwordInput().fill(password);
    await this.loginButton().click();
    // A successful login redirects to the Accounts Overview page
    await this.page.waitForURL(/overview\.htm/i, { timeout: 15_000 });
  }

  /** Attempt login and expect failure (negative test helper). */
  async loginExpectFailure(username: string, password: string): Promise<void> {
    await this.usernameInput().fill(username);
    await this.passwordInput().fill(password);
    await this.loginButton().click();
  }

  async assertLoginError(message?: RegExp): Promise<void> {
    await expect(this.errorMessage()).toBeVisible();
    if (message) await expect(this.errorMessage()).toHaveText(message);
  }

  async assertLoggedOut(): Promise<void> {
    await expect(this.usernameInput()).toBeVisible();
  }

  async clickRegister(): Promise<void> {
    await this.registerLink().click();
  }
}
