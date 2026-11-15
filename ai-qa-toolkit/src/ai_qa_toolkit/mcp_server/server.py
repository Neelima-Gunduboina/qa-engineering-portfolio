"""MCP server that lets an AI assistant (Claude Code, Claude Desktop) work with a Java/TestNG
test repository safely.

Tools
    list_test_suites   which suites may be run
    run_suite          run an ALLOW-LISTED suite (no shell, no free-form commands)
    get_failures       failed tests from the last run's Surefire XML reports
    get_defect         read one defect report, e.g. DEF-001

Safety
    - Only suites in the allow-list can run; the assistant can't pass its own command.
    - Commands run without a shell, with a timeout.
    - Defect ids are validated (DEF-NNN), so a request can't read files outside docs/defects.
    - Everything else is read-only.
    - Deliberate refusals raise ToolError, so the assistant sees the reason; any other
      exception is reported as a crash without leaking its details.

Run against the ParaBank portfolio repo:
    QA_REPO=../qa-portfolio-parabank python -m ai_qa_toolkit.mcp_server
"""

from __future__ import annotations

import os
import re
import subprocess
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from pathlib import Path

from mcp.server.mcpserver import MCPServer
from mcp.server.mcpserver.exceptions import ToolError

DEFECT_ID = re.compile(r"^DEF-\d{3}$")

DEFAULT_SUITES = {
    "smoke": ["mvn", "-q", "test", "-Psmoke"],
    "api-db": ["mvn", "-q", "test", "-Papi-db"],
}


@dataclass
class QaConfig:
    repo: Path
    suites: dict[str, list[str]] = field(default_factory=lambda: dict(DEFAULT_SUITES))
    timeout_seconds: int = 900

    @property
    def defects_dir(self) -> Path:
        return self.repo / "docs" / "defects"

    @property
    def reports_dir(self) -> Path:
        return self.repo / "target" / "surefire-reports"


def list_suites(config: QaConfig) -> list[str]:
    return sorted(config.suites)


def run_suite(config: QaConfig, name: str) -> dict:
    if name not in config.suites:
        raise ToolError(f"Suite '{name}' is not allowed. Allowed: {', '.join(list_suites(config))}")
    completed = subprocess.run(
        config.suites[name], cwd=config.repo, capture_output=True, text=True,
        timeout=config.timeout_seconds, check=False,
    )
    output = (completed.stdout + completed.stderr).strip().splitlines()
    return {"suite": name, "passed": completed.returncode == 0,
            "exit_code": completed.returncode, "output_tail": output[-30:]}


def get_failures(config: QaConfig) -> list[dict]:
    failures = []
    for report in sorted(config.reports_dir.glob("TEST-*.xml")):
        root = ET.parse(report).getroot()
        for case in root.iter("testcase"):
            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            if problem is not None:
                failures.append({
                    "test": f"{case.get('classname')}.{case.get('name')}",
                    "type": problem.get("type", ""),
                    "message": (problem.get("message") or "").strip()[:500],
                })
    return failures


def get_defect(config: QaConfig, defect_id: str) -> str:
    if not DEFECT_ID.match(defect_id):
        raise ToolError("Defect id must look like DEF-001")
    matches = sorted(config.defects_dir.glob(f"{defect_id}-*.md"))
    if not matches:
        raise ToolError(f"No defect report found for {defect_id}")
    return matches[0].read_text(encoding="utf-8")


def build_server(config: QaConfig) -> MCPServer:
    server = MCPServer(
        "qa-tools",
        instructions="Run allow-listed test suites and read test results and defect reports.",
    )

    @server.tool()
    def list_test_suites() -> list[str]:
        """List the test suites this server is allowed to run."""
        return list_suites(config)

    @server.tool(name="run_suite")
    def run_suite_tool(name: str) -> dict:
        """Run one allow-listed test suite and return whether it passed, plus the last lines of output."""
        return run_suite(config, name)

    @server.tool(name="get_failures")
    def get_failures_tool() -> list[dict]:
        """Return the failed tests from the most recent run (Surefire XML reports)."""
        return get_failures(config)

    @server.tool(name="get_defect")
    def get_defect_tool(defect_id: str) -> str:
        """Return the full text of a defect report, e.g. DEF-001."""
        return get_defect(config, defect_id)

    return server


def main() -> None:
    repo = Path(os.environ.get("QA_REPO", ".")).resolve()
    build_server(QaConfig(repo=repo)).run()
