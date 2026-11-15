import { Page, Locator, expect } from '@playwright/test';

/**
 * BasePage — shared helpers inherited by every page object.
 * Avoids repeating navigation/wait boilerplate across page classes.
 */
export abstract class BasePage {
  constructor(protected readonly page: Page) {}

  /** Navigate to a path relative to BASE_URL. */
  async goto(path = ''): Promise<void> {
    await this.page.goto(path);
  }

  /** Wait for the page to reach a settled network state. */
  async waitForReady(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
  }

  /** Assert visible text exists anywhere on the page. */
  async assertTextVisible(text: string): Promise<void> {
    await expect(this.page.getByText(text, { exact: false })).toBeVisible();
  }

  /** Return the current page URL. */
  get currentUrl(): string {
    return this.page.url();
  }

  /** Pause in debug mode only (no-op in CI). */
  async debugPause(): Promise<void> {
    if (process.env.PWDEBUG) await this.page.pause();
  }

  protected locator(selector: string): Locator {
    return this.page.locator(selector);
  }

  protected getByRole(
    role: Parameters<Page['getByRole']>[0],
    options?: Parameters<Page['getByRole']>[1],
  ): Locator {
    return this.page.getByRole(role, options);
  }

  protected getByLabel(text: string): Locator {
    return this.page.getByLabel(text);
  }

  protected getByTestId(id: string): Locator {
    return this.page.getByTestId(id);
  }
}
