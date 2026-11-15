"""End-to-end tests of the evaluation pipeline, offline.

The most important test here is `test_broken_bot_is_caught`: it proves the eval catches
every seeded failure. An eval that can't fail is not a test.
"""

import json

from evals.fakes import load_scripted
from evals.judge import GroundednessJudge, Verdict
from evals.run import CaseResult, evaluate, gate, load_cases, load_thresholds, main, score
from tests.fakes import FakeClient

SEEDED_FAILURES = {"COR-01", "COR-09", "UNK-01", "OOS-01", "INJ-01", "INJ-02", "PII-01"}


def test_dataset_is_well_formed():
    cases = load_cases()
    ids = [c["id"] for c in cases]
    assert len(ids) == len(set(ids)), "duplicate case ids"
    assert {c["category"] for c in cases} == {"correctness", "unknown", "out_of_scope", "injection", "pii"}
    for c in cases:
        assert c.get("expect") or c.get("forbid") or c.get("must_decline"), f"{c['id']} checks nothing"


def test_good_bot_passes_every_case():
    cases = load_cases()
    results = evaluate(load_scripted("good", cases), cases)
    assert [r.id for r in results if not r.passed] == []


def test_broken_bot_is_caught():
    cases = load_cases()
    results = evaluate(load_scripted("broken", cases), cases)
    assert {r.id for r in results if not r.passed} == SEEDED_FAILURES


def test_gate_passes_for_good_and_fails_for_broken():
    cases, thresholds = load_cases(), load_thresholds()
    assert gate(score(evaluate(load_scripted("good", cases), cases)), thresholds) == []
    problems = gate(score(evaluate(load_scripted("broken", cases), cases)), thresholds)
    assert {p.split(":")[0] for p in problems} == {"correctness", "unknown", "out_of_scope", "injection", "pii"}


def test_gate_flags_drop_against_baseline():
    assert gate({"correctness": 0.96}, {"correctness": 0.95}, baseline={"correctness": 1.0}) == [
        "correctness: dropped from 100% to 96% (baseline)"
    ]


def test_score_includes_judge_and_consistency():
    results = [
        CaseResult("A", "correctness", "q", "a", True, grounded=True, consistent=True),
        CaseResult("B", "correctness", "q", "a", True, grounded=False, consistent=False),
    ]
    assert score(results) == {"consistency": 0.5, "correctness": 1.0, "groundedness": 0.5}


def test_judge_failure_is_reported():
    cases = [c for c in load_cases() if c["id"] == "COR-01"]
    judge = GroundednessJudge(client=FakeClient(parsed=Verdict(
        grounded=False, unsupported_claims=["limit applies to branches"], reasoning="x")), model="m")
    [result] = evaluate(load_scripted("good", cases), cases, judge=judge)
    assert result.grounded is False
    assert "unsupported claims" in result.failures[0]
    assert judge.client.messages.calls[0]["output_format"] is Verdict


def test_cli_writes_report_and_sets_exit_code(tmp_path):
    out = tmp_path / "report.json"
    assert main(["--bot", "scripted:good", "--out", str(out)]) == 0
    assert json.loads(out.read_text())["gate_passed"] is True
    assert main(["--bot", "scripted:broken", "--out", str(out)]) == 1
