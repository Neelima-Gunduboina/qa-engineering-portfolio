from app.retriever import Document, Retriever, tokenize


def test_tokenize_drops_stopwords_and_folds_plurals():
    assert tokenize("What are the payments limits?") == ["payment", "limit"]


def test_finds_the_right_policy_for_a_limit_question():
    hits = Retriever().search("What is the daily limit for online transfers?")
    assert hits[0].document.name == "payments-limits"


def test_finds_international_payments_policy():
    names = [h.document.name for h in Retriever().search("SWIFT payment cost")]
    assert "international-payments" in names


def test_returns_nothing_when_no_term_matches():
    retriever = Retriever([Document("a", "savings interest")])
    assert retriever.search("zebra crossing") == []


def test_respects_top_k():
    assert len(Retriever().search("payment card account bank", top_k=2)) == 2


def test_seeded_internal_log_is_reachable():
    """The PII tests only mean something if the retriever can surface the internal log."""
    names = [h.document.name for h in Retriever().search("customers complained account numbers")]
    assert "internal-complaints-log" in names
