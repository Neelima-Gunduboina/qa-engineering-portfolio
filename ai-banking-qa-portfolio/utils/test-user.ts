/**
 * test-user.ts — Shares the ParaBank customer the suite runs as.
 *
 * The public ParaBank demo resets its data regularly (the old john/demo
 * login no longer exists), so the suite never depends on a shared account.
 * Instead tests/auth.setup.ts registers ONE customer per run (unless
 * PARABANK_USERNAME and PARABANK_PASSWORD are set) and saves the credentials;
 * every UI and API test reuses that customer. One registration per run also
 * keeps the suite well under the demo site's rate limit (HTTP 429).
 */

import * as fs from 'fs';
import * as path from 'path';
import dotenv from 'dotenv';

dotenv.config();

export interface Credentials {
  username: string;
  password: string;
}

/** Written by tests/auth.setup.ts and read by UI tests that need to log in again. */
export const CREDENTIALS_FILE = 'playwright/.auth/credentials.json';

/** Credentials from PARABANK_USERNAME / PARABANK_PASSWORD, if both are set. */
export function envCredentials(): Credentials | undefined {
  const username = process.env.PARABANK_USERNAME?.trim();
  const password = process.env.PARABANK_PASSWORD?.trim();
  return username && password ? { username, password } : undefined;
}

/**
 * A random username of at most 20 characters (prefix up to 7 + 4 letters + 9
 * digits). ParaBank wrongly reports longer new usernames as "already exists"
 * (DEF-005).
 */
export function uniqueUsername(prefix = 'qa'): string {
  const letters = Array.from({ length: 4 }, () =>
    String.fromCharCode(97 + Math.floor(Math.random() * 26)),
  ).join('');
  return `${prefix}${letters}${Date.now() % 1_000_000_000}`;
}

export function saveCredentials(credentials: Credentials): void {
  fs.mkdirSync(path.dirname(CREDENTIALS_FILE), { recursive: true });
  fs.writeFileSync(CREDENTIALS_FILE, JSON.stringify(credentials, null, 2));
}

/** Credentials of the customer the auth setup created (or the env account). */
export function savedCredentials(): Credentials {
  const fromEnv = envCredentials();
  if (fromEnv) return fromEnv;
  if (fs.existsSync(CREDENTIALS_FILE)) {
    return JSON.parse(fs.readFileSync(CREDENTIALS_FILE, 'utf8')) as Credentials;
  }
  throw new Error(
    `No test customer found. Run the "setup" project first (npx playwright test) or set PARABANK_USERNAME/PARABANK_PASSWORD.`,
  );
}
