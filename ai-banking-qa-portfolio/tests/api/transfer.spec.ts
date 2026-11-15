/**
 * transfer.spec.ts — REST API tests for fund transfer endpoint.
 *
 * Banking context: validates the same fund transfer business logic at the
 * API layer that the UI tests validate at the presentation layer. This dual
 * coverage (UI + API) is a best practice from FLEXCUBE testing — changes to
 * backend processing are caught by API tests even when UI appears unchanged.
 *
 * Test data: two fresh accounts are opened for the run's customer (see
 * tests/auth.setup.ts) in beforeAll, so the tests never skip or depend on shared demo data.
 */

import { test, expect, type APIRequestContext } from '@playwright/test';
import dotenv from 'dotenv';
import { savedCredentials } from '../../utils/test-user';
dotenv.config();

const BASE_URL = process.env.BASE_URL ?? 'https://parabank.parasoft.com';
const API_BASE = `${BASE_URL}/parabank/services/bank`;

let fromId = 0;
let toId = 0;

async function openAccount(
  request: APIRequestContext,
  customerId: number,
  type: 0 | 1,
  fundingAccountId: number,
): Promise<number> {
  const res = await request.post(
    `${API_BASE}/createAccount?customerId=${customerId}&newAccountType=${type}&fromAccountId=${fundingAccountId}`,
  );
  expect(res.status(), 'open account').toBe(200);
  return (await res.json()).id as number;
}

async function balanceOf(request: APIRequestContext, accountId: number): Promise<number> {
  const res = await request.get(`${API_BASE}/accounts/${accountId}`);
  expect(res.status()).toBe(200);
  return (await res.json()).balance as number;
}

test.describe('Transfer API', () => {
  test.beforeAll(async ({ playwright }) => {
    const { username, password } = savedCredentials();
    const request = await playwright.request.newContext({
      extraHTTPHeaders: { Accept: 'application/json' },
    });
    const login = await request.get(`${API_BASE}/login/${username}/${password}`);
    expect(login.status(), 'login').toBe(200);
    const customerId = (await login.json()).id as number;

    const accounts = await (await request.get(`${API_BASE}/customers/${customerId}/accounts`)).json();
    const primary = accounts[0].id as number;
    fromId = await openAccount(request, customerId, 0, primary); // CHECKING
    toId = await openAccount(request, customerId, 1, primary); // SAVINGS
    await request.dispose();
  });

  test('TC-API-T-01: POST /transfer with valid accounts succeeds', async ({ request }) => {
    const res = await request.post(
      `${API_BASE}/transfer?fromAccountId=${fromId}&toAccountId=${toId}&amount=1.00`,
    );

    expect(res.status()).toBe(200);
    expect(await res.text()).toBe(
      `Successfully transferred $1.00 from account #${fromId} to account #${toId}`,
    );
  });

  test('TC-API-T-02: transfer debits source and credits target', async ({ request }) => {
    const fromBefore = await balanceOf(request, fromId);
    const toBefore = await balanceOf(request, toId);

    const res = await request.post(
      `${API_BASE}/transfer?fromAccountId=${fromId}&toAccountId=${toId}&amount=5.00`,
    );
    expect(res.status()).toBe(200);

    expect(await balanceOf(request, fromId)).toBeCloseTo(fromBefore - 5.0, 2);
    expect(await balanceOf(request, toId)).toBeCloseTo(toBefore + 5.0, 2);
  });

  test('TC-API-T-03: transfer with non-numeric amount is rejected', async ({ request }) => {
    const res = await request.post(
      `${API_BASE}/transfer?fromAccountId=${fromId}&toAccountId=${toId}&amount=abc`,
    );
    expect(res.status()).toBeGreaterThanOrEqual(400);
  });

  test('TC-API-T-04: transfer with negative amount is rejected', async ({ request }) => {
    // Known defect: ParaBank accepts negative amounts and moves money in reverse.
    // test.fail() keeps CI green while the defect is open; Playwright reports
    // "unexpectedly passed" once it is fixed, as a prompt to remove this line.
    test.info().annotations.push({
      type: 'known-defect',
      description:
        'DEF-001 https://github.com/Neelima-Gunduboina/qa-portfolio-parabank/blob/main/docs/defects/DEF-001-negative-transfer-amount.md',
    });
    test.fail();

    const res = await request.post(
      `${API_BASE}/transfer?fromAccountId=${fromId}&toAccountId=${toId}&amount=-100.00`,
    );
    expect(res.status()).toBeGreaterThanOrEqual(400);
  });
});
