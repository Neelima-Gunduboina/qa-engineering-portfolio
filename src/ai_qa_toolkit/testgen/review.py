"""Measure generated test cases instead of trusting them.

- coverage(): recall against a hand-written list of must-have scenarios
- quality_issues(): structural problems a reviewer would reject
- to_csv(): export for import into Jira/Zephyr/Azure DevOps
"""

from __future__ import annotations

import csv
import io
import re
from dataclasses import dataclass

from ai_qa_toolkit.testgen.models import TestCase, TestSuite


@dataclass
class Coverage:
    covered: list[str]
    missed: list[str]

    @property
    def recall(self) -> float:
        total = len(self.covered) + len(self.missed)
        return round(len(self.covered) / total, 3) if total else 0.0


def _case_text(case: TestCase) -> str:
    parts = [case.title, case.expected_result, *case.preconditions, *case.steps]
    return " ".join(parts).lower().replace(",", "")


def coverage(suite: TestSuite, must_have: list[dict]) -> Coverage:
    """A must-have scenario is covered when one case mentions all its keywords."""
    texts = [_case_text(c) for c in suite.test_cases]
    covered, missed = [], []
    for item in must_have:
        # Match at the start of a word, so "0.01" doesn't match inside "25000.01"
        # but "reject" still matches "rejected".
        patterns = [re.compile(r"(?<![\w.])" + re.escape(k.lower().replace(",", ""))) for k in item["keywords"]]
        hit = any(all(p.search(text) for p in patterns) for text in texts)
        (covered if hit else missed).append(item["id"])
    return Coverage(covered, missed)


def quality_issues(suite: TestSuite) -> list[str]:
    issues = []
    ids = [c.id for c in suite.test_cases]
    if len(ids) != len(set(ids)):
        issues.append("duplicate test case ids")
    if not any(c.negative for c in suite.test_cases):
        issues.append("no negative test cases")
    for c in suite.test_cases:
        if not c.steps:
            issues.append(f"{c.id}: no steps")
        if len(c.expected_result.strip()) < 10:
            issues.append(f"{c.id}: expected result too vague")
    return issues


def to_csv(suite: TestSuite) -> str:
    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(["ID", "Title", "Technique", "Priority", "Negative", "Preconditions", "Steps", "Expected Result"])
    for c in suite.test_cases:
        writer.writerow([
            c.id, c.title, c.technique.value, c.priority.value, "Yes" if c.negative else "No",
            "\n".join(c.preconditions),
            "\n".join(f"{i}. {step}" for i, step in enumerate(c.steps, 1)),
            c.expected_result,
        ])
    return buffer.getvalue()
