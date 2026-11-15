"""CLI for the defect investigation agent (needs ANTHROPIC_API_KEY).

    python -m ai_qa_toolkit.defect_agent investigate F-06   # investigate, then ask you to approve
    python -m ai_qa_toolkit.defect_agent eval               # accuracy on the 10 recorded failures
"""

from __future__ import annotations

import argparse
import sys
from importlib import resources
from pathlib import Path

import yaml
from langgraph.types import Command

from ai_qa_toolkit.defect_agent.graph import build_agent
from ai_qa_toolkit.defect_agent.tools import LlmClassifier, recorded_health


def load_failures() -> list[dict]:
    text = resources.files("ai_qa_toolkit.defect_agent").joinpath("data/failures.yaml").read_text(encoding="utf-8")
    return yaml.safe_load(text)["failures"]


def investigate(failure_id: str, out_dir: Path) -> int:
    failure = next((f for f in load_failures() if f["id"] == failure_id), None)
    if failure is None:
        print(f"Unknown failure id {failure_id}")
        return 2
    agent = build_agent(LlmClassifier(), recorded_health, out_dir)
    config = {"configurable": {"thread_id": failure_id}}
    paused = agent.invoke({"failure": failure}, config)
    proposal = paused["__interrupt__"][0].value
    print(proposal["report"])
    decision = input("Approve this report? Type 'approve' or a reason to reject: ").strip()
    result = agent.invoke(Command(resume=decision), config)
    print(f"\nSaved to {result['report_path']}" if result.get("report_path") else "\nRejected: nothing written.")
    return 0


def evaluate(out_dir: Path) -> int:
    failures = load_failures()
    agent = build_agent(LlmClassifier(), recorded_health, out_dir)
    correct = 0
    for failure in failures:
        state = agent.invoke({"failure": failure}, {"configurable": {"thread_id": failure["id"]}})
        got = state["classification"].category
        ok = got == failure["expected"]
        correct += ok
        print(f"  {failure['id']}  expected {failure['expected']:<14} got {got:<14} {'OK' if ok else 'WRONG'}")
    print(f"\nAccuracy: {correct}/{len(failures)}  (reports are never written during eval: no approval given)")
    return 0


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)
    inv = sub.add_parser("investigate")
    inv.add_argument("failure_id")
    sub.add_parser("eval")
    parser.add_argument("--out", type=Path, default=Path("out/defects"))
    args = parser.parse_args(argv)
    return investigate(args.failure_id, args.out) if args.command == "investigate" else evaluate(args.out)


if __name__ == "__main__":
    sys.exit(main())
