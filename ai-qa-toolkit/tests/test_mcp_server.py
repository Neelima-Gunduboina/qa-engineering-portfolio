import asyncio
import json
import sys

import pytest
from mcp.server.mcpserver.exceptions import ToolError

from ai_qa_toolkit.mcp_server.server import QaConfig, build_server

SUREFIRE = """<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="com.neelima.qa.tests.api.AccountApiTest" tests="3" failures="1" errors="1">
  <testcase classname="com.neelima.qa.tests.api.AccountApiTest" name="validLoginReturnsCustomer"/>
  <testcase classname="com.neelima.qa.tests.api.AccountApiTest" name="negativeAmountShouldBeRejected">
    <failure type="java.lang.AssertionError" message="DEF-001 reproduced: negative transfer accepted"/>
  </testcase>
  <testcase classname="com.neelima.qa.tests.api.AccountApiTest" name="customerAccountsHaveValidFields">
    <error type="java.net.ConnectException" message="Connection refused"/>
  </testcase>
</testsuite>
"""


@pytest.fixture
def config(tmp_path):
    (tmp_path / "docs" / "defects").mkdir(parents=True)
    (tmp_path / "docs" / "defects" / "DEF-001-negative-transfer-amount.md").write_text("# DEF-001\nNegative amount")
    (tmp_path / "target" / "surefire-reports").mkdir(parents=True)
    (tmp_path / "target" / "surefire-reports" / "TEST-AccountApiTest.xml").write_text(SUREFIRE)
    (tmp_path / "secret.txt").write_text("do not read")
    suites = {"ok": [sys.executable, "-c", "print('BUILD SUCCESS')"],
              "broken": [sys.executable, "-c", "import sys; print('BUILD FAILURE'); sys.exit(1)"]}
    return QaConfig(repo=tmp_path, suites=suites, timeout_seconds=30)


def call(config, tool, args=None):
    return asyncio.run(build_server(config).call_tool(tool, args or {}))


def test_lists_only_allowed_suites(config):
    names = [t.name for t in asyncio.run(build_server(config).list_tools())]
    assert set(names) == {"list_test_suites", "run_suite", "get_failures", "get_defect"}
    assert call(config, "list_test_suites").structured_content == {"result": ["broken", "ok"]}


def test_runs_allowed_suite_and_reports_result(config):
    ok = json.loads(call(config, "run_suite", {"name": "ok"}).content[0].text)
    assert ok["passed"] is True and ok["output_tail"] == ["BUILD SUCCESS"]
    broken = json.loads(call(config, "run_suite", {"name": "broken"}).content[0].text)
    assert broken["passed"] is False and broken["exit_code"] == 1


def test_refuses_suite_not_on_allow_list(config):
    with pytest.raises(ToolError, match="not allowed"):
        call(config, "run_suite", {"name": "rm -rf /"})


def test_reads_failures_and_errors_from_surefire(config):
    failures = call(config, "get_failures").structured_content["result"]
    assert [f["test"].split(".")[-1] for f in failures] == ["negativeAmountShouldBeRejected",
                                                             "customerAccountsHaveValidFields"]
    assert failures[1]["type"] == "java.net.ConnectException"


def test_reads_defect_report(config):
    assert "Negative amount" in call(config, "get_defect", {"defect_id": "DEF-001"}).content[0].text


@pytest.mark.parametrize("bad_id", ["../secret", "DEF-001/../../secret", "secret.txt", "DEF-1"])
def test_rejects_path_traversal_and_bad_ids(config, bad_id):
    with pytest.raises(ToolError, match="must look like DEF-001") as error:
        call(config, "get_defect", {"defect_id": bad_id})
    assert "do not read" not in str(error.value)
