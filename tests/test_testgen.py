import csv
import io
from pathlib import Path

import pytest
import yaml

from ai_qa_toolkit.testgen.generator import SYSTEM_PROMPT, TestCaseGenerator
from ai_qa_toolkit.testgen.models import Priority, Technique, TestCase, TestSuite
from ai_qa_toolkit.testgen.review import coverage, quality_issues, to_csv
from tests.fakes import FakeClient

STORIES = Path(__file__).resolve().parent.parent / "stories"


def case(id_, title, *, negative=False, steps=("Open transfer screen",),
         expected="Transfer is rejected with a message"):
    return TestCase(id=id_, title=title, technique=Technique.BOUNDARY_VALUE_ANALYSIS, priority=Priority.P1,
                    negative=negative, preconditions=["Customer has two accounts"], steps=list(steps),
                    expected_result=expected)


@pytest.fixture
def must_have():
    return yaml.safe_load((STORIES / "fund-transfer.must-have.yaml").read_text())


def test_generator_sends_story_and_schema():
    suite = TestSuite(story_id="FT-101", test_cases=[case("TC-FT-101-01", "Valid transfer")], open_questions=[])
    client = FakeClient(suite)
    result = TestCaseGenerator(client=client, model="m").generate("story text")
    call = client.messages.calls[0]
    assert result is suite
    assert call["output_format"] is TestSuite
    assert call["system"] == SYSTEM_PROMPT
    assert call["messages"] == [{"role": "user", "content": "story text"}]


def test_generator_raises_when_model_refuses():
    with pytest.raises(RuntimeError):
        TestCaseGenerator(client=FakeClient(None, stop_reason="refusal"), model="m").generate("story")


def test_coverage_counts_scenarios_by_keywords(must_have):
    suite = TestSuite(story_id="FT-101", open_questions=[], test_cases=[
        case("TC-01", "Transfer £25,000.01 is rejected", negative=True),
        case("TC-02", "Transfer to the same account is rejected", negative=True),
        case("TC-03", "Insufficient funds", negative=True, expected="Rejected; both balances unchanged"),
    ])
    result = coverage(suite, must_have)
    assert set(result.covered) == {"upper-boundary-above-limit", "same-account-rejected",
                                   "insufficient-funds-rejected", "failed-transfer-leaves-balances-unchanged"}
    assert result.recall == round(4 / len(must_have), 3)


def test_coverage_keyword_must_start_a_word():
    """Regression: "0.01" must not match inside "25000.01" (found while building this)."""
    suite = TestSuite(story_id="X", open_questions=[], test_cases=[case("TC-01", "Transfer £25,000.01")])
    assert coverage(suite, [{"id": "lowest", "keywords": ["0.01"]}]).missed == ["lowest"]


def test_quality_issues_found():
    suite = TestSuite(story_id="X", open_questions=[], test_cases=[
        case("TC-01", "a", steps=[], expected="ok"),
        case("TC-01", "b"),
    ])
    assert quality_issues(suite) == [
        "duplicate test case ids", "no negative test cases", "TC-01: no steps", "TC-01: expected result too vague",
    ]


def test_csv_export_numbers_steps():
    suite = TestSuite(story_id="X", open_questions=[], test_cases=[
        case("TC-01", "Zero amount rejected", negative=True, steps=["Enter £0.00", "Tap Transfer"]),
    ])
    rows = list(csv.reader(io.StringIO(to_csv(suite))))
    assert rows[0][0] == "ID"
    assert rows[1][:5] == ["TC-01", "Zero amount rejected", "boundary_value_analysis", "P1", "Yes"]
    assert rows[1][6] == "1. Enter £0.00\n2. Tap Transfer"


def test_sample_story_states_the_limits():
    story = (STORIES / "fund-transfer.md").read_text()
    assert "£25,000.00" in story and "different" in story
