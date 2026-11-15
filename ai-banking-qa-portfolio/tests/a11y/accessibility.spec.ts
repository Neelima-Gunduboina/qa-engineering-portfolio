/**
 * accessibility.spec.ts — WCAG 2.1 AA accessibility checks using axe-core.
 *
 * Banking and insurance context:
 * - UK financial services are legally required to meet WCAG 2.1 AA under the
 *   Equality Act 2010 and FCA guidance (Royal London domain knowledge).
 * - FLEXCUBE deployments in regulated markets require accessibility sign-off.
 *
 * Approach — baseline of known issues:
 * ParaBank already has serious/critical WCAG violations that this project
 * cannot fix. They are documented in docs/accessibility-findings.md and listed
 * in KNOWN_VIOLATIONS below. Each test fails only on a NEW serious/critical
 * violation (a regression), and attaches the full axe report to every run so
 * nothing is hidden. When ParaBank fixes an issue, remove it from the baseline.
 */

import { test, expect, type Page, type TestInfo } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

/** Serious/critical axe rule ids already present per page (audited 15 Nov 2026). */
const KNOWN_VIOLATIONS: Record<string, string[]> = {
  login: ['color-contrast', 'html-has-lang', 'image-alt', 'label', 'link-name'],
  overview: ['color-contrast', 'html-has-lang', 'image-alt', 'link-name'],
  transfer: ['color-contrast', 'html-has-lang', 'image-alt', 'label', 'link-name', 'select-name'],
  billpay: ['color-contrast', 'html-has-lang', 'image-alt', 'label', 'link-name', 'select-name'],
};

async function auditPage(page: Page, pageName: string, testInfo: TestInfo): Promise<void> {
  await page.waitForLoadState('networkidle');
  const results = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa']).analyze();

  await testInfo.attach(`axe-${pageName}.json`, {
    body: JSON.stringify(results.violations, null, 2),
    contentType: 'application/json',
  });

  const serious = results.violations.filter(
    (v) => v.impact === 'critical' || v.impact === 'serious',
  );
  for (const v of serious) {
    testInfo.annotations.push({
      type: KNOWN_VIOLATIONS[pageName].includes(v.id) ? 'known-a11y-issue' : 'NEW-a11y-issue',
      description: `[${v.impact}] ${v.id} (${v.nodes.length} element(s)): ${v.help}`,
    });
  }

  const newViolations = serious
    .filter((v) => !KNOWN_VIOLATIONS[pageName].includes(v.id))
    .map((v) => `${v.id}: ${v.help}`);
  expect(newViolations, 'New serious/critical WCAG 2.1 AA violations').toEqual([]);
}

test.describe('Accessibility (WCAG 2.1 AA)', () => {
  test.describe('anonymous pages', () => {
    test.use({ storageState: { cookies: [], origins: [] } });

    test('TC-A11Y-01: login page has no new critical accessibility violations', async ({
      page,
    }, testInfo) => {
      await page.goto('/parabank/index.htm');
      await auditPage(page, 'login', testInfo);
    });

    test('TC-A11Y-05: login inputs have programmatic labels', async ({ page }) => {
      // Known issue A11Y-02 (docs/accessibility-findings.md): the Username and
      // Password captions are plain <b> text, not <label> elements, so screen
      // readers announce two unnamed edit fields.
      test.info().annotations.push({
        type: 'known-defect',
        description: 'A11Y-02 docs/accessibility-findings.md',
      });
      test.fail();

      await page.goto('/parabank/index.htm');
      const unlabelled = await page.evaluate(() =>
        Array.from(document.querySelectorAll('input:not([type="hidden"]):not([type="submit"])'))
          .filter((input) => {
            const id = input.getAttribute('id');
            const labelFor = id ? document.querySelector(`label[for="${id}"]`) : null;
            return (
              !input.getAttribute('aria-label') &&
              !input.getAttribute('aria-labelledby') &&
              !labelFor &&
              !input.closest('label')
            );
          })
          .map((input) => input.getAttribute('name')),
      );
      expect(unlabelled).toEqual([]);
    });
  });

  test('TC-A11Y-02: accounts overview has no new critical accessibility violations', async ({
    page,
  }, testInfo) => {
    await page.goto('/parabank/overview.htm');
    await auditPage(page, 'overview', testInfo);
  });

  test('TC-A11Y-03: transfer funds has no new critical accessibility violations', async ({
    page,
  }, testInfo) => {
    await page.goto('/parabank/transfer.htm');
    await auditPage(page, 'transfer', testInfo);
  });

  test('TC-A11Y-04: bill payment has no new critical accessibility violations', async ({
    page,
  }, testInfo) => {
    await page.goto('/parabank/billpay.htm');
    await auditPage(page, 'billpay', testInfo);
  });
});
