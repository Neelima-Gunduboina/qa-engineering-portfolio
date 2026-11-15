"""Turn a user story into structured test cases with Claude."""

from __future__ import annotations

from typing import Any

from ai_qa_toolkit.llm import MODEL, make_client
from ai_qa_toolkit.testgen.models import TestSuite

SYSTEM_PROMPT = """You are a senior QA engineer in UK retail banking.
Derive test cases from the user story and its acceptance criteria.

- Use named test design techniques: equivalence partitioning, boundary value analysis,
  decision tables, state transitions, error guessing.
- Test every boundary on both sides (for a £25,000 limit: £24,999.99, £25,000.00, £25,000.01).
- Include negative cases (invalid input, unauthorised users, failures) and mark them negative=true.
- Steps are short imperative actions; the expected result is observable and specific.
- Priority: P1 = money or security at risk, P2 = core function, P3 = cosmetic or rare.
- IDs: TC-<story id>-<two-digit number>.
- Do NOT guess where the story is unclear: put the ambiguity in open_questions instead."""


class TestCaseGenerator:
    __test__ = False

    def __init__(self, client: Any | None = None, model: str = MODEL):
        self.client = client if client is not None else make_client()
        self.model = model

    def generate(self, story: str) -> TestSuite:
        response = self.client.messages.parse(
            model=self.model,
            max_tokens=16000,
            system=SYSTEM_PROMPT,
            messages=[{"role": "user", "content": story}],
            output_format=TestSuite,
        )
        if response.stop_reason == "refusal" or response.parsed_output is None:
            raise RuntimeError(f"No test suite returned (stop_reason={response.stop_reason})")
        return response.parsed_output
