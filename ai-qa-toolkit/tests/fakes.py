"""Fake Anthropic client: records calls to messages.parse and returns a canned parsed_output."""

from __future__ import annotations

from types import SimpleNamespace


class FakeMessages:
    def __init__(self, parsed, stop_reason: str = "end_turn"):
        self.parsed = parsed
        self.stop_reason = stop_reason
        self.calls: list[dict] = []

    def parse(self, **kwargs):
        self.calls.append(kwargs)
        return SimpleNamespace(stop_reason=self.stop_reason, parsed_output=self.parsed)


class FakeClient:
    def __init__(self, parsed, stop_reason: str = "end_turn"):
        self.messages = FakeMessages(parsed, stop_reason)
