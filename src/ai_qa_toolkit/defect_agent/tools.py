"""Read-only investigation tools and the LLM classifier."""

from __future__ import annotations

import re
import urllib.request
from typing import Any, Protocol

from ai_qa_toolkit.defect_agent.models import Classification
from ai_qa_toolkit.llm import MODEL, make_client

# Open defects in qa-portfolio-parabank, matched by phrases in the failure text.
KNOWN_DEFECTS = {
    "DEF-001": ["negative transfer", "$-"],
    "DEF-004": ["$0 transfer", "same-account transfer"],
    "DEF-005": ["already exist"],
}

LOG_SIGNAL = re.compile(r"(error|exception|assert|fail|timeout|resolved to 0|\b[45]\d\d\b|passed)", re.IGNORECASE)


def log_excerpt(log: str, max_lines: int = 8) -> list[str]:
    """The log lines that carry a signal, so the classifier isn't flooded with noise."""
    lines = [line.strip() for line in log.splitlines() if line.strip()]
    return [line for line in lines if LOG_SIGNAL.search(line)][:max_lines]


def match_known_defect(failure: dict) -> str | None:
    text = f"{failure.get('error', '')} {failure.get('log', '')}".lower()
    for defect_id, phrases in KNOWN_DEFECTS.items():
        if defect_id.lower() in text or any(p.lower() in text for p in phrases):
            return defect_id
    return None


class HealthCheck(Protocol):
    def __call__(self, failure: dict) -> bool: ...


def recorded_health(failure: dict) -> bool:
    """For recorded failures: use the health result captured at the time."""
    return bool(failure.get("app_up", True))


def http_health(url: str, timeout: float = 5.0) -> HealthCheck:
    """For live use: is the application answering right now?"""
    def check(_failure: dict) -> bool:
        try:
            with urllib.request.urlopen(url, timeout=timeout) as response:  # noqa: S310 - fixed, configured URL
                return response.status < 500
        except Exception:
            return False
    return check


CLASSIFIER_PROMPT = """You triage failed automated tests for a banking test suite.
Classify the failure as exactly one of:
- REAL_BUG: the application behaves wrongly; the test is right.
- FLAKY: timing or shared-environment noise; typically passes on retry.
- LOCATOR_DRIFT: the page changed and the test's selector no longer matches.
- ENV_ISSUE: the application or a dependency is down or unreachable.
- TEST_BUG: the test itself is wrong (bad assertion, wrong data, wrong expectation).
Use the evidence. Say how confident you are (0 to 1) and why, in one or two sentences."""


class LlmClassifier:
    def __init__(self, client: Any | None = None, model: str = MODEL):
        self.client = client if client is not None else make_client()
        self.model = model

    def classify(self, failure: dict, evidence: list[str]) -> Classification:
        details = "\n".join([
            f"Test: {failure['test']}",
            f"Error: {failure['error']}",
            f"Retried: {failure.get('retried')}, passed on retry: {failure.get('passed_on_retry')}",
            "Evidence:", *[f"- {e}" for e in evidence],
        ])
        response = self.client.messages.parse(
            model=self.model,
            max_tokens=4096,
            system=CLASSIFIER_PROMPT,
            messages=[{"role": "user", "content": details}],
            output_format=Classification,
        )
        if response.stop_reason == "refusal" or response.parsed_output is None:
            return Classification(category="TEST_BUG", confidence=0.0, rationale="Classifier gave no answer")
        return response.parsed_output
