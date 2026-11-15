"""Defect investigation agent (LangGraph) with a human approval step.

    gather ─► classify ─► draft ─► ⏸ human review ─► finalize
                                     approve: write the report file
                                     reject:  record the reason, write nothing

Governance
    - Tools are read-only: log excerpt, health check, known-defect lookup.
    - If the app is down, the failure is classified ENV_ISSUE by rule, without the LLM.
    - Nothing is written until a person approves. The agent never files tickets,
      reruns tests or changes code.
"""

from __future__ import annotations

import re
from pathlib import Path
from typing import Protocol, TypedDict

from langgraph.checkpoint.memory import InMemorySaver
from langgraph.graph import END, START, StateGraph
from langgraph.types import interrupt

from ai_qa_toolkit.defect_agent.models import Classification
from ai_qa_toolkit.defect_agent.tools import HealthCheck, log_excerpt, match_known_defect


class Classifier(Protocol):
    def classify(self, failure: dict, evidence: list[str]) -> Classification: ...


class State(TypedDict, total=False):
    failure: dict
    evidence: list[str]
    app_up: bool
    known_defect: str | None
    classification: Classification
    report: str
    decision: str
    report_path: str | None


def _slug(text: str) -> str:
    return re.sub(r"[^a-z0-9]+", "-", text.lower()).strip("-")[-60:]


def draft_report(failure: dict, classification: Classification, evidence: list[str], known: str | None) -> str:
    lines = [
        f"# [{classification.category}] {failure['test']}",
        "",
        f"**Classification:** {classification.category} (confidence {classification.confidence:.0%})",
        f"**Known defect:** {known or 'none matched'}",
        f"**Why:** {classification.rationale}",
        "",
        "## Error",
        "",
        f"```\n{failure['error']}\n```",
        "",
        "## Evidence",
        "",
        *[f"- {e}" for e in evidence],
        "",
        "## Suggested next step",
        "",
        {
            "REAL_BUG": "Raise or update a defect with these steps; keep the test failing until fixed.",
            "FLAKY": "Find the timing cause (wait condition, shared data); don't just add retries.",
            "LOCATOR_DRIFT": "Update the page object locator; check the change was intended.",
            "ENV_ISSUE": "Check the environment and rerun; no defect needed if it was an outage.",
            "TEST_BUG": "Fix the test's assertion or data; the application looks correct.",
        }[classification.category],
    ]
    return "\n".join(lines) + "\n"


def build_agent(classifier: Classifier, health_check: HealthCheck, out_dir: Path, checkpointer=None):
    def gather(state: State) -> State:
        failure = state["failure"]
        app_up = health_check(failure)
        known = match_known_defect(failure)
        evidence = [f"Application health check: {'reachable' if app_up else 'DOWN'}"]
        if failure.get("retried"):
            evidence.append(f"Retried once: {'passed' if failure.get('passed_on_retry') else 'failed again'}")
        if known:
            evidence.append(f"Matches known open defect {known}")
        evidence += [f"Log: {line}" for line in log_excerpt(failure.get("log", ""))]
        return {"evidence": evidence, "app_up": app_up, "known_defect": known}

    def classify(state: State) -> State:
        if not state["app_up"]:
            return {"classification": Classification(
                category="ENV_ISSUE", confidence=0.95, rationale="The application health check failed.")}
        return {"classification": classifier.classify(state["failure"], state["evidence"])}

    def draft(state: State) -> State:
        return {"report": draft_report(state["failure"], state["classification"],
                                       state["evidence"], state.get("known_defect"))}

    def review(state: State) -> State:
        decision = interrupt({"category": state["classification"].category, "report": state["report"]})
        return {"decision": str(decision).strip()}

    def finalize(state: State) -> State:
        if state["decision"].lower() != "approve":
            return {"report_path": None}
        out_dir.mkdir(parents=True, exist_ok=True)
        path = out_dir / f"{state['classification'].category.lower()}-{_slug(state['failure']['test'])}.md"
        path.write_text(state["report"], encoding="utf-8")
        return {"report_path": str(path)}

    graph = StateGraph(State)
    for name, fn in [("gather", gather), ("classify", classify), ("draft", draft),
                     ("review", review), ("finalize", finalize)]:
        graph.add_node(name, fn)
    graph.add_edge(START, "gather")
    graph.add_edge("gather", "classify")
    graph.add_edge("classify", "draft")
    graph.add_edge("draft", "review")
    graph.add_edge("review", "finalize")
    graph.add_edge("finalize", END)
    return graph.compile(checkpointer=checkpointer or InMemorySaver())
