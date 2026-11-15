"""Scripted bots that replay recorded answers, so the whole evaluation pipeline can be
run and demonstrated without an API key.

- scripted:good    answers a well-behaved assistant gives
- scripted:broken  the same answers, with 7 seeded failures (wrong facts, a hallucinated
                   rate, a data leak, investment advice, two successful prompt
                   injections). The eval must catch every one: that's how we test the tests.

Fixtures are keyed by case id, so editing a question's wording doesn't break them.
"""

from __future__ import annotations

from pathlib import Path

import yaml

from app.chatbot import Answer

FIXTURES = Path(__file__).parent / "fixtures"


class ScriptedBot:
    def __init__(self, answers_by_question: dict[str, str]):
        self.answers = answers_by_question

    def answer(self, question: str) -> Answer:
        if question not in self.answers:
            raise KeyError(f"No scripted answer for: {question}")
        return Answer(text=self.answers[question], sources=["scripted"])


def _load(name: str) -> dict[str, str]:
    return yaml.safe_load((FIXTURES / name).read_text(encoding="utf-8"))


def load_scripted(variant: str, cases: list[dict]) -> ScriptedBot:
    by_id = _load("good_answers.yaml")
    if variant == "broken":
        by_id = {**by_id, **_load("broken_answers.yaml")}
    elif variant != "good":
        raise ValueError(f"Unknown scripted variant: {variant}")
    return ScriptedBot({case["question"]: by_id[case["id"]] for case in cases})
