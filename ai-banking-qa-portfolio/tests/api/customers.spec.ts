/**
 * customers.spec.ts — REST API tests for customer/profile endpoints.
 *
 * Covers: authentication, profile retrieval, chained workflows.
 * "Chained workflow" pattern: login → get customer id → get accounts →
 * perform operation — mimics the API test chains I built in Postman for
 * FLEXCUBE integration testing.
 */

import { test, expect } from '@playwright/test';
import dotenv from 'dotenv';
import { savedCredentials } from '../../utils/test-user';
dotenv.config();

const BASE_URL = process.env.BASE_URL ?? 'https://parabank.parasoft.com';
const API_BASE = `${BASE_URL}/parabank/services/bank`;

// The customer registered by tests/auth.setup.ts (or PARABANK_USERNAME/PASSWORD)
let USERNAME = '';
let PASSWORD = '';
test.beforeAll(() => {
  ({ username: USERNAME, password: PASSWORD } = savedCredentials());
});

test.describe('Customers API', () => {
  test('TC-API-C-01: login endpoint returns customer object', async ({
    request,
  }) => {
    const res = await request.get(
      `${API_BASE}/login/${USERNAME}/${PASSWORD}`,
    );
    expect(res.status()).toBe(200);

    const customer = await res.json();
    expect(customer).toHaveProperty('id');
    expect(customer).toHaveProperty('firstName');
    expect(customer).toHaveProperty('lastName');
    expect(customer).toHaveProperty('address');
  });

  test('TC-API-C-02: invalid credentials return error', async ({ request }) => {
    const res = await request.get(
      `${API_BASE}/login/invalid_user_999/wrongpass`,
    );
    expect([400, 401, 403, 500]).toContain(res.status());
  });

  test('TC-API-C-03: GET /customers/{id} returns customer profile', async ({
    request,
  }) => {
    const loginRes = await request.get(
      `${API_BASE}/login/${USERNAME}/${PASSWORD}`,
    );
    const customer = await loginRes.json();

    const profileRes = await request.get(`${API_BASE}/customers/${customer.id}`);
    expect(profileRes.status()).toBe(200);

    const profile = await profileRes.json();
    expect(profile.id).toBe(customer.id);
    expect(profile.firstName).toBeTruthy();
  });

  test('TC-API-C-04: chained workflow — login → accounts → transaction', async ({
    request,
  }) => {
    // Step 1: authenticate
    const loginRes = await request.get(
      `${API_BASE}/login/${USERNAME}/${PASSWORD}`,
    );
    expect(loginRes.status()).toBe(200);
    const customer = await loginRes.json();

    // Step 2: list accounts
    const accountsRes = await request.get(
      `${API_BASE}/customers/${customer.id}/accounts`,
    );
    expect(accountsRes.status()).toBe(200);
    const accounts = await accountsRes.json();
    expect(accounts.length).toBeGreaterThan(0);

    // Step 3: get transaction history for first account
    const txRes = await request.get(
      `${API_BASE}/accounts/${accounts[0].id}/transactions`,
    );
    expect(txRes.status()).toBe(200);

    const transactions = await txRes.json();
    expect(Array.isArray(transactions)).toBe(true);
  });

  test('TC-API-C-05: response Content-Type is JSON', async ({ request }) => {
    const res = await request.get(
      `${API_BASE}/login/${USERNAME}/${PASSWORD}`,
    );
    const contentType = res.headers()['content-type'];
    expect(contentType).toContain('json');
  });
});
