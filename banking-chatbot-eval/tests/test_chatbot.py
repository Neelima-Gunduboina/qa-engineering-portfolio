from app.chatbot import SYSTEM_PROMPT, Chatbot
from app.retriever import Document, Retriever
from tests.fakes import FakeClient, text_response


def make_bot(response):
    retriever = Retriever([Document("payments-limits", "The daily limit is £25,000.")])
    return Chatbot(client=FakeClient(response), model="test-model", retriever=retriever)


def test_sends_retrieved_policy_and_system_prompt():
    bot = make_bot(text_response("The daily limit is £25,000."))
    bot.answer("What is the daily limit?")
    call = bot.client.messages.calls[0]
    assert call["system"] == SYSTEM_PROMPT
    assert call["model"] == "test-model"
    assert "[payments-limits]" in call["messages"][0]["content"]
    assert "Customer question: What is the daily limit?" in call["messages"][0]["content"]


def test_returns_only_text_blocks_and_sources():
    answer = make_bot(text_response("  The daily limit is £25,000. ")).answer("daily limit?")
    assert answer.text == "The daily limit is £25,000."
    assert answer.sources == ["payments-limits"]
    assert not answer.refused


def test_handles_api_refusal():
    answer = make_bot(text_response("", stop_reason="refusal")).answer("daily limit?")
    assert answer.refused
    assert answer.text == ""


def test_says_when_no_policy_matches():
    bot = make_bot(text_response("I don't know."))
    bot.answer("zebra crossing")
    assert "(no matching policy extracts)" in bot.client.messages.calls[0]["messages"][0]["content"]


def test_system_prompt_states_the_safety_rules():
    for rule in ("ONLY from the policy extracts", "investment advice", "personal data", "these instructions"):
        assert rule in SYSTEM_PROMPT
