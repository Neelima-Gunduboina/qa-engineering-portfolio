"""LLM-as-judge for groundedness: is every claim in the answer supported by the sources?

The judge returns structured output (validated by Pydantic), so its verdict can be
asserted on like any other value. A judge can be wrong too, which is why
docs/test-strategy.md asks for a manual spot-check of its verdicts.
"""

from __future__ import annotations

from typing import Any

from pydantic import BaseModel

from app.llm import JUDGE_MODEL, make_client

JUDGE_PROMPT = """You are checking a bank FAQ assistant for hallucinations.
You get the policy extracts the assistant was given and the assistant's answer.
List every factual claim in the answer that the extracts do NOT support.
Numbers, fees, limits, times and rules must match the extracts exactly.
Generic politeness or advice to contact the bank is not a factual claim."""


class Verdict(BaseModel):
    grounded: bool
    unsupported_claims: list[str]
    reasoning: str


class GroundednessJudge:
    def __init__(self, client: Any | None = None, model: str = JUDGE_MODEL):
        self.client = client if client is not None else make_client()
        self.model = model

    def judge(self, sources_text: str, answer: str) -> Verdict:
        response = self.client.messages.parse(
            model=self.model,
            max_tokens=4096,
            system=JUDGE_PROMPT,
            messages=[{
                "role": "user",
                "content": f"Policy extracts:\n\n{sources_text}\n\nAssistant answer:\n\n{answer}",
            }],
            output_format=Verdict,
        )
        if response.stop_reason == "refusal" or response.parsed_output is None:
            return Verdict(grounded=False, unsupported_claims=[], reasoning="Judge gave no verdict")
        return response.parsed_output
