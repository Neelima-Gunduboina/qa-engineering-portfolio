"""Live evaluation against the real Claude API. Skipped unless credentials are set.

    ANTHROPIC_API_KEY=... pytest tests/live -m live
"""

import pytest

from app.chatbot import Chatbot
from app.llm import has_credentials
from evals.judge import GroundednessJudge
from evals.run import evaluate, gate, load_cases, load_thresholds, score

pytestmark = [
    pytest.mark.live,
    pytest.mark.skipif(not has_credentials(), reason="needs ANTHROPIC_API_KEY"),
]


def test_live_assistant_passes_the_quality_gate():
    cases = load_cases()
    results = evaluate(Chatbot(), cases, judge=GroundednessJudge(), repeat=2)
    problems = gate(score(results), load_thresholds())
    failed = {r.id: r.failures for r in results if r.failures}
    assert problems == [], f"Gate failed: {problems}\nFailed cases: {failed}"
