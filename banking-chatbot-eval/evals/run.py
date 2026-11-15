"""Run the evaluation suite and apply the quality gate.

Examples:
    python -m evals.run --bot scripted:good          # offline, should pass
    python -m evals.run --bot scripted:broken        # offline, must fail and list 7 problems
    python -m evals.run --bot live --judge --repeat 3 # real Claude calls (needs ANTHROPIC_API_KEY)

Exit code 0 = gate passed, 1 = gate failed. Results are written to reports/latest.json.
"""

from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict
from dataclasses import asdict, dataclass, field
from pathlib import Path

import yaml

from app.chatbot import Bot, Chatbot
from app.retriever import Retriever
from evals.checks import check_answer, normalise
from evals.fakes import load_scripted
from evals.judge import GroundednessJudge

ROOT = Path(__file__).resolve().parent.parent
DATASET = ROOT / "evals" / "dataset" / "golden.yaml"
THRESHOLDS = ROOT / "evals" / "thresholds.yaml"


@dataclass
class CaseResult:
    id: str
    category: str
    question: str
    answer: str
    passed: bool
    failures: list[str] = field(default_factory=list)
    grounded: bool | None = None
    consistent: bool | None = None


def load_cases(path: Path = DATASET) -> list[dict]:
    return yaml.safe_load(path.read_text(encoding="utf-8"))["cases"]


def load_thresholds(path: Path = THRESHOLDS) -> dict[str, float]:
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def evaluate(bot: Bot, cases: list[dict], judge: GroundednessJudge | None = None,
             retriever: Retriever | None = None, repeat: int = 1) -> list[CaseResult]:
    retriever = retriever or Retriever()
    results = []
    for case in cases:
        answer = bot.answer(case["question"])
        check = check_answer(case, answer.text, answer.refused)
        result = CaseResult(case["id"], case["category"], case["question"], answer.text,
                            check.passed, check.failures)

        if repeat > 1 and case["category"] == "correctness":
            extra = [bot.answer(case["question"]) for _ in range(repeat - 1)]
            result.consistent = all(check_answer(case, a.text, a.refused).passed for a in extra)
            if not result.consistent:
                result.failures.append(f"inconsistent across {repeat} runs")

        if judge and case.get("grounded") and answer.text:
            hits = retriever.search(case["question"], top_k=3)
            sources = "\n\n".join(f"[{h.document.name}]\n{h.document.text}" for h in hits)
            verdict = judge.judge(sources, answer.text)
            result.grounded = verdict.grounded
            if not verdict.grounded:
                result.failures.append(f"unsupported claims: {verdict.unsupported_claims}")

        results.append(result)
    return results


def score(results: list[CaseResult]) -> dict[str, float]:
    totals: dict[str, list[bool]] = defaultdict(list)
    for r in results:
        totals[r.category].append(r.passed)
        if r.grounded is not None:
            totals["groundedness"].append(r.grounded)
        if r.consistent is not None:
            totals["consistency"].append(r.consistent)
    return {category: round(sum(v) / len(v), 3) for category, v in sorted(totals.items())}


def gate(scores: dict[str, float], thresholds: dict[str, float],
         baseline: dict[str, float] | None = None) -> list[str]:
    """Return the reasons the gate fails (empty list = pass)."""
    problems = [
        f"{category}: {value:.0%} is below the {thresholds[category]:.0%} threshold"
        for category, value in scores.items()
        if category in thresholds and value < thresholds[category]
    ]
    if baseline:
        problems += [
            f"{category}: dropped from {baseline[category]:.0%} to {value:.0%} (baseline)"
            for category, value in scores.items()
            if category in baseline and value < baseline[category]
        ]
    return problems


def make_bot(name: str, cases: list[dict]) -> Bot:
    if name.startswith("scripted:"):
        return load_scripted(name.split(":", 1)[1], cases)
    if name == "live":
        return Chatbot()
    raise ValueError(f"Unknown bot: {name}")


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--bot", default="scripted:good", help="scripted:good | scripted:broken | live")
    parser.add_argument("--judge", action="store_true", help="run the LLM groundedness judge (live only)")
    parser.add_argument("--repeat", type=int, default=1, help="ask correctness questions N times")
    parser.add_argument("--baseline", type=Path, help="fail if any score drops below this earlier run")
    parser.add_argument("--out", type=Path, default=ROOT / "reports" / "latest.json")
    args = parser.parse_args(argv)

    cases = load_cases()
    judge = GroundednessJudge() if args.judge else None
    results = evaluate(make_bot(args.bot, cases), cases, judge=judge, repeat=args.repeat)
    scores = score(results)
    baseline = json.loads(args.baseline.read_text())["scores"] if args.baseline else None
    problems = gate(scores, load_thresholds(), baseline)

    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(json.dumps(
        {"bot": args.bot, "scores": scores, "gate_passed": not problems,
         "results": [asdict(r) for r in results]}, indent=2), encoding="utf-8")

    print(f"\nBot: {args.bot}   Cases: {len(results)}\n")
    for category, value in scores.items():
        print(f"  {category:<14} {value:>6.0%}")
    failed = [r for r in results if r.failures]
    if failed:
        print(f"\nFailed cases ({len(failed)}):")
        for r in failed:
            print(f"  {r.id}  {'; '.join(r.failures)}")
            print(f"         answer: {normalise(r.answer)[:110]}")
    print("\nQUALITY GATE:", "PASSED" if not problems else "FAILED")
    for p in problems:
        print(f"  - {p}")
    return 0 if not problems else 1


if __name__ == "__main__":
    sys.exit(main())
