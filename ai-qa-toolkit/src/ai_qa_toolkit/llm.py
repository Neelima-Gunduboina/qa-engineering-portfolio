"""Shared Claude settings. Override the model with LLM_MODEL (e.g. a cheaper one for experiments)."""

from __future__ import annotations

import os

import anthropic

MODEL = os.environ.get("LLM_MODEL", "claude-opus-5-5")


def make_client() -> anthropic.Anthropic:
    """Reads ANTHROPIC_API_KEY (or another configured credential) from the environment."""
    return anthropic.Anthropic()


def has_credentials() -> bool:
    return bool(os.environ.get("ANTHROPIC_API_KEY") or os.environ.get("ANTHROPIC_AUTH_TOKEN"))
