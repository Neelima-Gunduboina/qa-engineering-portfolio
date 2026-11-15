import { Page, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export interface RegistrationData {
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  phone: string;
  ssn: string;
  username: string;
  password: string;
}

export class RegisterPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await super.goto('/parabank/register.htm');
    await this.waitForReady();
  }

  async fillRegistrationForm(data: RegistrationData): Promise<void> {
    await this.page.locator('input[name="customer.firstName"]').fill(data.firstName);
    await this.page.locator('input[name="customer.lastName"]').fill(data.lastName);
    await this.page.locator('input[name="customer.address.street"]').fill(data.address);
    await this.page.locator('input[name="customer.address.city"]').fill(data.city);
    await this.page.locator('input[name="customer.address.state"]').fill(data.state);
    await this.page.locator('input[name="customer.address.zipCode"]').fill(data.zipCode);
    await this.page.locator('input[name="customer.phoneNumber"]').fill(data.phone);
    await this.page.locator('input[name="customer.ssn"]').fill(data.ssn);
    await this.page.locator('input[name="customer.username"]').fill(data.username);
    await this.page.locator('input[name="customer.password"]').fill(data.password);
    await this.page.locator('input[name="repeatedPassword"]').fill(data.password);
  }

  async submit(): Promise<void> {
    await this.page.getByRole('button', { name: /register/i }).click();
  }

  async assertRegistrationSuccess(): Promise<void> {
    await expect(
      this.page.getByText(/your account was created successfully/i),
    ).toBeVisible({ timeout: 15_000 });
  }

  async assertUsernameError(): Promise<void> {
    await expect(
      this.page.getByText(/username already exists|please try another/i),
    ).toBeVisible();
  }
}
