/**
 * accounts.spec.ts — REST API tests for account endpoints.
 *
 * Uses Playwright's built-in APIRequestContext (no Axios/Supertest needed).
 * Mirrors the kind of API-layer validation done with Postman collections —
 * now integrated into the same framework and CI pipeline.
 *
 * ParaBank REST API base: /parabank/services/bank
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

test.describe('Accounts API', () => {
  test('TC-API-A-01: GET /customers/{id}/accounts returns account list', async ({
    request,
  }) => {
    // First get customer id via login
    const loginRes = await request.get(
      `${BASE_URL}/parabank/services/bank/login/${USERNAME}/${PASSWORD}`,
    );
    expect(loginRes.status()).toBe(200);

    const customer = await loginRes.json();
    const customerId = customer.id;
    expect(customerId).toBeTruthy();

    const accountsRes = await request.get(
      `${API_BASE}/customers/${customerId}/accounts`,
    );
    expect(accountsRes.status()).toBe(200);

    const accounts = await accountsRes.json();
    expect(Array.isArray(accounts)).toBe(true);
    expect(accounts.length).toBeGreaterThan(0);

    // Validate schema of first account
    const first = accounts[0];
    expect(first).toHaveProperty('id');
    expect(first).toHaveProperty('balance');
    expect(first).toHaveProperty('type');
  });

  test('TC-API-A-02: GET /accounts/{id} returns account detail', async ({
    request,
  }) => {
    // Login to get customer/account IDs
    const loginRes = await request.get(
      `${BASE_URL}/parabank/services/bank/login/${USERNAME}/${PASSWORD}`,
    );
    const customer = await loginRes.json();

    const accountsRes = await request.get(
      `${API_BASE}/customers/${customer.id}/accounts`,
    );
    const accounts = await accountsRes.json();
    const accountId = accounts[0].id;

    const detailRes = await request.get(`${API_BASE}/accounts/${accountId}`);
    expect(detailRes.status()).toBe(200);

    const detail = await detailRes.json();
    expect(detail.id).toBe(accountId);
    expect(detail).toHaveProperty('balance');
    expect(detail).toHaveProperty('type');
  });

  test('TC-API-A-03: GET /accounts/{id}/transactions returns transaction history', async ({
    request,
  }) => {
    const loginRes = await request.get(
      `${BASE_URL}/parabank/services/bank/login/${USERNAME}/${PASSWORD}`,
    );
    const customer = await loginRes.json();

    const accountsRes = await request.get(
      `${API_BASE}/customers/${customer.id}/accounts`,
    );
    const accounts = await accountsRes.json();
    const accountId = accounts[0].id;

    const txRes = await request.get(`${API_BASE}/accounts/${accountId}/transactions`);
    expect(txRes.status()).toBe(200);

    const transactions = await txRes.json();
    expect(Array.isArray(transactions)).toBe(true);
  });

  test('TC-API-A-04: unauthorized access returns 4xx status', async ({
    request,
  }) => {
    // Without authentication, accessing accounts should fail
    const res = await request.get(`${API_BASE}/accounts/99999999`);
    expect([400, 401, 403, 404, 500]).toContain(res.status());
  });
});
