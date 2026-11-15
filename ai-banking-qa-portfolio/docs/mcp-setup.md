# Playwright MCP Setup Guide

## What is Playwright MCP?

Playwright MCP (Model Context Protocol) is an integration that lets an AI assistant
(like Claude) drive a real browser through the **accessibility tree** rather than
raw DOM manipulation. Instead of writing `page.click('#btn-transfer')`, you instruct
the AI in natural language: *"Click the Transfer button on the fund transfer page"*
and Claude resolves and executes the action.

**Why this matters for QA:**
- Exploratory testing at scale — AI can navigate flows from natural-language test scripts
- Accessibility tree interaction is more resilient than CSS selectors
- Demonstrates LLM-in-the-loop test execution, increasingly required in modern QA roles

---

## Installation

```bash
# Install the MCP server package
npm install -D @playwright/mcp

# Or use the Playwright init command (if available in your version):
npx playwright install-deps
```

---

## Claude Code Configuration

Add the following to your Claude Code MCP configuration file.

**Location:** `~/.claude/claude_desktop_config.json` (Claude Desktop)  
or your project's `.claude/mcp_servers.json`

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": [
        "@playwright/mcp",
        "--browser", "chromium",
        "--headless"
      ],
      "env": {
        "BASE_URL": "https://parabank.parasoft.com"
      }
    }
  }
}
```

For **headed** (visible browser) mode during development:

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": [
        "@playwright/mcp",
        "--browser", "chromium"
      ]
    }
  }
}
```

---

## Demo: Natural Language → Browser Actions

Once configured, you can instruct Claude to perform banking flows directly.

### Example Prompt to Claude

```
Using the Playwright browser tool:
1. Navigate to https://parabank.parasoft.com/parabank/index.htm
2. Log in with username "<test username>" and password "<test password>"
   (a run of `npx playwright test --project=setup` registers one and saves it to playwright/.auth/credentials.json)
3. Click on "Accounts Overview"
4. Tell me how many accounts are listed and what the total balance is
5. Navigate to "Transfer Funds" and verify the form loads with account dropdowns
```

Claude will execute each step through the accessibility tree and report what it observes.

### What Claude sees (accessibility snapshot excerpt)

```json
{
  "role": "main",
  "children": [
    {
      "role": "form",
      "name": "Login",
      "children": [
        { "role": "textbox", "name": "Username", "focused": false },
        { "role": "textbox", "name": "Password" },
        { "role": "button", "name": "Log In" }
      ]
    }
  ]
}
```

This is exactly what the self-healing locator utility also uses — the same
accessible representation that NVDA/JAWS screen readers see.

---

## Integration with This Framework

The MCP server and the self-heal utility share the same underlying mechanism:
**accessibility tree snapshots**. When `self-heal.ts` asks the LLM to find a broken
locator, it sends the same JSON snapshot that MCP uses for navigation.

```
Browser → Accessibility Tree → LLM → Action
          ↑ same snapshot ↑
              self-heal.ts
```

---

## CI Consideration

MCP-driven tests are **exploratory** and **non-deterministic** — they are not
suitable for the main CI regression suite. Use them for:

- Sprint-start exploratory testing
- Generating test ideas from feature descriptions
- Accessibility audits driven by natural language

Keep deterministic Playwright specs (`tests/ui/*.spec.ts`) as your CI gate.

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `npx @playwright/mcp` not found | Run `npm install` in project root |
| Browser doesn't launch | Check `--browser chromium` matches installed browsers (`npx playwright install`) |
| Claude says it can't see the page | Check `BASE_URL` env var and that the site is reachable |
| Accessibility tree empty | Some pages use iframes — ask Claude to switch frame context |
