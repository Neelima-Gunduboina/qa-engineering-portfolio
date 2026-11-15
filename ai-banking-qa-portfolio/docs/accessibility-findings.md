# Accessibility Findings — ParaBank (WCAG 2.1 AA)

Audited 15 Nov 2026 with axe-core (`@axe-core/playwright`, tags `wcag2a` + `wcag2aa`) on the public demo
https://parabank.parasoft.com. Only **serious** and **critical** issues are listed.

The automated suite in [`tests/a11y/`](../tests/a11y/accessibility.spec.ts) treats these as a **known baseline**:
a test fails only if a *new* serious/critical violation appears, and every run attaches the full axe report.

## Summary

| ID | Issue | WCAG | Impact | Pages | Elements |
|---|---|---|---|---|---|
| A11Y-01 | Form fields without programmatic labels (amount, payee details) | 1.3.1, 4.1.2 | Critical | Transfer, Bill Pay | 1 / 9 |
| A11Y-02 | Login Username/Password captions are plain bold text, not `<label>` | 1.3.1, 4.1.2 | Critical | Login | 2 |
| A11Y-03 | Account dropdowns have no accessible name | 4.1.2 | Critical | Transfer, Bill Pay | 2 / 1 |
| A11Y-04 | Header image (inside the Admin link) has no `alt` text | 1.1.1 | Critical | All pages | 1 |
| A11Y-05 | Insufficient colour contrast | 1.4.3 | Serious | All pages | 3–13 |
| A11Y-06 | `<html>` element has no `lang` attribute | 3.1.1 | Serious | All pages | 1 |
| A11Y-07 | Admin link has no discernible text (its only content is that image) | 2.4.4, 4.1.2 | Serious | All pages | 1 |

## Customer impact

- **Screen reader users cannot log in reliably**: the two login fields are announced as "edit text" with no name (A11Y-02).
- **Transfers and bill payments are hard to complete without sight**: the amount field and the From/To account
  dropdowns have no name, so a user cannot tell which account they are moving money from (A11Y-01/03).
- **Low-vision users** may not be able to read some text at the current contrast (A11Y-05).
- Missing `lang` means screen readers may use the wrong pronunciation rules (A11Y-06).

In UK financial services these would block an accessibility sign-off (Equality Act 2010, FCA Consumer Duty).

## Suggested fixes

| ID | Fix |
|---|---|
| A11Y-01/02 | Wrap captions in `<label for="…">` and give each input an `id`, e.g. `<label for="username">Username</label>` |
| A11Y-03 | `<label for="fromAccountId">From account</label>` (or `aria-label`) on each `<select>` |
| A11Y-04 | Give the header image a meaningful `alt` (e.g. `alt="Admin page"`) |
| A11Y-05 | Darken text colours to reach a 4.5:1 ratio (3:1 for large text) |
| A11Y-06 | `<html lang="en">` |
| A11Y-07 | Fixed by A11Y-04, or add `aria-label="Admin page"` to the link |

## Re-test

```bash
npx playwright test --project=a11y
```

Open the HTML report: each test has an `axe-<page>.json` attachment with every violation and the affected elements.
When an issue is fixed, remove its rule id from `KNOWN_VIOLATIONS` in the spec so it is guarded against regressions.
