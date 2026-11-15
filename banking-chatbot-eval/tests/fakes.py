"""Fake Anthropic client for offline unit tests: records requests, returns canned responses."""

from __future__ import annotations

from types import SimpleNamespace


def text_response(text: str, stop_reason: str = "end_turn") -> SimpleNamespace:
    return SimpleNamespace(stop_reason=stop_reason,
                           content=[SimpleNamespace(type="thinking", thinking=""),
                                    SimpleNamespace(type="text", text=text)])


class FakeMessages:
    def __init__(self, response=None, parsed=None):
        self.response = response
        self.parsed = parsed
        self.calls: list[dict] = []

    def create(self, **kwargs):
        self.calls.append(kwargs)
        return self.response

    def parse(self, **kwargs):
        self.calls.append(kwargs)
        return SimpleNamespace(stop_reason="end_turn", parsed_output=self.parsed)


class FakeClient:
    def __init__(self, response=None, parsed=None):
        self.messages = FakeMessages(response, parsed)
