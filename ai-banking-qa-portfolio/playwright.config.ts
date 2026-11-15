import { defineConfig, devices } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config();

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 4 : undefined,

  // CI shards write blob reports that the report job merges into one HTML
  // report + results.json (for AI triage); local runs get them directly.
  reporter: process.env.CI
    ? [['blob'], ['list']]
    : [
        ['html', { outputFolder: 'playwright-report', open: 'never' }],
        ['list'],
        ['json', { outputFile: 'test-results/results.json' }],
      ],

  use: {
    baseURL: process.env.BASE_URL || 'https://parabank.parasoft.com',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
    actionTimeout: 30_000,
    navigationTimeout: 60_000,
  },

  projects: [
    // Setup project: creates authenticated storage state
    {
      name: 'setup',
      testMatch: /.*\.setup\.ts/,
      use: { ...devices['Desktop Chrome'] },
    },

    // Chromium — authenticated UI tests
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        storageState: 'playwright/.auth/user.json',
      },
      dependencies: ['setup'],
      testIgnore: ['**/api/**', '**/visual/**', '**/a11y/**'],
    },

    // Firefox — smoke subset
    {
      name: 'firefox',
      use: {
        ...devices['Desktop Firefox'],
        storageState: 'playwright/.auth/user.json',
      },
      dependencies: ['setup'],
      testMatch: ['**/ui/login.spec.ts', '**/ui/dashboard.spec.ts'],
    },

    // WebKit — smoke subset
    {
      name: 'webkit',
      use: {
        ...devices['Desktop Safari'],
        storageState: 'playwright/.auth/user.json',
      },
      dependencies: ['setup'],
      testMatch: ['**/ui/login.spec.ts', '**/ui/dashboard.spec.ts'],
    },

    // API tests — no browser needed (setup only registers the run's customer)
    {
      name: 'api',
      testMatch: '**/api/**/*.spec.ts',
      dependencies: ['setup'],
      // ParaBank returns XML unless JSON is requested explicitly.
      use: { extraHTTPHeaders: { Accept: 'application/json' } },
    },

    // Visual regression — Chromium only, stable snapshots
    {
      name: 'visual',
      use: {
        ...devices['Desktop Chrome'],
        storageState: 'playwright/.auth/user.json',
      },
      dependencies: ['setup'],
      testMatch: '**/visual/**/*.spec.ts',
    },

    // Accessibility — Chromium only
    {
      name: 'a11y',
      use: {
        ...devices['Desktop Chrome'],
        storageState: 'playwright/.auth/user.json',
      },
      dependencies: ['setup'],
      testMatch: '**/a11y/**/*.spec.ts',
    },
  ],

  outputDir: 'test-results',
});
