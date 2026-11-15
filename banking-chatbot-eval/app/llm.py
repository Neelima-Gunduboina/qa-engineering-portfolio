"""Shared Claude client settings.

The model is configurable so experiments can use a cheaper model:
    LLM_MODEL=claude-haiku-5-5 python -m evals.run --bot live
"""

from __future__ import annotations

import os

import anthropic

MODEL = os.environ.get("LLM_MODEL", "claude-opus-5-5")
JUDGE_MODEL = os.environ.get("JUDGE_MODEL", "claude-opus-5-5")


def make_client() -> anthropic.Anthropic:
    """Reads ANTHROPIC_API_KEY (or another configured credential) from the environment."""
    return anthropic.Anthropic()


def has_credentials() -> bool:
    return bool(os.environ.get("ANTHROPIC_API_KEY") or os.environ.get("ANTHROPIC_AUTH_TOKEN"))
