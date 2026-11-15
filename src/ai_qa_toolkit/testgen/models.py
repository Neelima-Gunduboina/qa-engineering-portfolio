"""The test case schema. The LLM must return exactly this shape (Claude structured outputs),
so generated cases can be validated, scored and imported like any other data."""

from __future__ import annotations

from enum import StrEnum

from pydantic import BaseModel


class Technique(StrEnum):
    EQUIVALENCE_PARTITIONING = "equivalence_partitioning"
    BOUNDARY_VALUE_ANALYSIS = "boundary_value_analysis"
    DECISION_TABLE = "decision_table"
    STATE_TRANSITION = "state_transition"
    ERROR_GUESSING = "error_guessing"


class Priority(StrEnum):
    P1 = "P1"
    P2 = "P2"
    P3 = "P3"


class TestCase(BaseModel):
    __test__ = False  # stop pytest collecting this model as a test class

    id: str
    title: str
    technique: Technique
    priority: Priority
    negative: bool
    preconditions: list[str]
    steps: list[str]
    expected_result: str


class TestSuite(BaseModel):
    __test__ = False

    story_id: str
    test_cases: list[TestCase]
    open_questions: list[str]
