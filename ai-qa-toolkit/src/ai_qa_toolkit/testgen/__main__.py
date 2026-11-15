"""CLI: generate test cases from a user story, score them, export CSV.

    python -m ai_qa_toolkit.testgen stories/fund-transfer.md \
        --must-have stories/fund-transfer.must-have.yaml --csv out/fund-transfer.csv
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

import yaml

from ai_qa_toolkit.testgen.generator import TestCaseGenerator
from ai_qa_toolkit.testgen.review import coverage, quality_issues, to_csv


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="Generate test cases from a user story")
    parser.add_argument("story", type=Path)
    parser.add_argument("--must-have", type=Path, help="YAML list of scenarios the suite must cover")
    parser.add_argument("--csv", type=Path, help="write the suite as CSV for test-management import")
    args = parser.parse_args(argv)

    suite = TestCaseGenerator().generate(args.story.read_text(encoding="utf-8"))
    print(f"\n{len(suite.test_cases)} test cases for {suite.story_id}\n")
    for c in suite.test_cases:
        flag = "NEG" if c.negative else "   "
        print(f"  {c.id:<14} {c.priority.value} {flag} {c.title}")

    if suite.open_questions:
        print("\nOpen questions for the BA:")
        for q in suite.open_questions:
            print(f"  - {q}")

    issues = quality_issues(suite)
    print("\nQuality issues:", "none" if not issues else "")
    for issue in issues:
        print(f"  - {issue}")

    if args.must_have:
        must_have = yaml.safe_load(args.must_have.read_text(encoding="utf-8"))
        result = coverage(suite, must_have)
        print(f"\nCoverage of must-have scenarios: {result.recall:.0%}")
        for missed in result.missed:
            print(f"  missed: {missed}")

    if args.csv:
        args.csv.parent.mkdir(parents=True, exist_ok=True)
        args.csv.write_text(to_csv(suite), encoding="utf-8")
        print(f"\nCSV written to {args.csv}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
