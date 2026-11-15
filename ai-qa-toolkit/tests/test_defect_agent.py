import pytest
from langgraph.types import Command

from ai_qa_toolkit.defect_agent.__main__ import load_failures
from ai_qa_toolkit.defect_agent.graph import build_agent
from ai_qa_toolkit.defect_agent.models import Classification
from ai_qa_toolkit.defect_agent.tools import LlmClassifier, log_excerpt, match_known_defect, recorded_health
from tests.fakes import FakeClient


class ExpectedClassifier:
    """Fake classifier that returns the recorded correct answer, and counts calls."""

    def __init__(self):
        self.calls = 0

    def classify(self, failure, evidence):
        self.calls += 1
        return Classification(category=failure["expected"], confidence=0.9, rationale="fake")


def failure(fid):
    return next(f for f in load_failures() if f["id"] == fid)


def run_until_review(agent, f, thread="t1"):
    config = {"configurable": {"thread_id": thread}}
    paused = agent.invoke({"failure": f}, config)
    return paused, config


def test_dataset_covers_every_category():
    failures = load_failures()
    assert len(failures) == 10
    assert {f["expected"] for f in failures} == {"REAL_BUG", "FLAKY", "LOCATOR_DRIFT", "ENV_ISSUE", "TEST_BUG"}


@pytest.mark.parametrize("fid, defect", [("F-01", "DEF-001"), ("F-02", "DEF-004"), ("F-03", "DEF-005"),
                                         ("F-06", None)])
def test_known_defect_lookup(fid, defect):
    assert match_known_defect(failure(fid)) == defect


def test_log_excerpt_keeps_signal_lines_only():
    assert log_excerpt("navigated to /a\nNoSuchElementException: x\nall fine\nattempt 2: PASSED") == [
        "NoSuchElementException: x", "attempt 2: PASSED"]


def test_pauses_for_human_before_writing_anything(tmp_path):
    agent = build_agent(ExpectedClassifier(), recorded_health, tmp_path)
    paused, _ = run_until_review(agent, failure("F-01"))
    proposal = paused["__interrupt__"][0].value
    assert proposal["category"] == "REAL_BUG"
    assert "Matches known open defect DEF-001" in proposal["report"]
    assert list(tmp_path.iterdir()) == []


def test_approval_writes_the_report(tmp_path):
    agent = build_agent(ExpectedClassifier(), recorded_health, tmp_path)
    _, config = run_until_review(agent, failure("F-06"))
    result = agent.invoke(Command(resume="approve"), config)
    written = list(tmp_path.iterdir())
    assert [p.name for p in written] == [result["report_path"].split("/")[-1]]
    assert written[0].read_text().startswith("# [LOCATOR_DRIFT]")


def test_rejection_writes_nothing(tmp_path):
    agent = build_agent(ExpectedClassifier(), recorded_health, tmp_path)
    _, config = run_until_review(agent, failure("F-06"))
    result = agent.invoke(Command(resume="wrong locator, the button was renamed on purpose"), config)
    assert result["report_path"] is None
    assert list(tmp_path.iterdir()) == []


def test_app_down_is_env_issue_without_calling_llm(tmp_path):
    classifier = ExpectedClassifier()
    agent = build_agent(classifier, recorded_health, tmp_path)
    paused, _ = run_until_review(agent, failure("F-04"))
    assert paused["classification"].category == "ENV_ISSUE"
    assert classifier.calls == 0


def test_llm_classifier_sends_evidence_and_schema():
    client = FakeClient(Classification(category="FLAKY", confidence=0.8, rationale="passed on retry"))
    result = LlmClassifier(client=client, model="m").classify(failure("F-08"), ["Retried once: passed"])
    call = client.messages.calls[0]
    assert result.category == "FLAKY"
    assert call["output_format"] is Classification
    assert "Retried once: passed" in call["messages"][0]["content"]


def test_llm_classifier_handles_refusal():
    result = LlmClassifier(client=FakeClient(None, stop_reason="refusal"), model="m").classify(failure("F-08"), [])
    assert result.confidence == 0.0
