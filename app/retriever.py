"""Keyword retriever over the bank's policy documents.

Deliberately simple (no embeddings, no vector database) so that retrieval is
deterministic and easy to test. The point of this project is testing the
assistant, not building the best search.
"""

from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path

POLICY_DIR = Path(__file__).parent / "policies"

STOPWORDS = {
    "a", "an", "the", "is", "are", "was", "be", "to", "of", "and", "or", "in", "on",
    "for", "with", "my", "i", "me", "you", "your", "can", "do", "does", "how", "what",
    "when", "which", "who", "it", "if", "at", "by", "much", "many", "there", "this",
    "that", "have", "has", "will", "would", "should", "please", "tell", "about",
}


@dataclass(frozen=True)
class Document:
    name: str
    text: str


@dataclass(frozen=True)
class Hit:
    document: Document
    score: int


def tokenize(text: str) -> list[str]:
    words = re.findall(r"[a-z0-9£%]+", text.lower())
    return [_stem(w) for w in words if w not in STOPWORDS]


def _stem(word: str) -> str:
    """Crude plural folding so 'payments' matches 'payment'."""
    if len(word) > 3 and word.endswith("s") and not word.endswith("ss"):
        return word[:-1]
    return word


def load_documents(directory: Path = POLICY_DIR) -> list[Document]:
    return [Document(p.stem, p.read_text(encoding="utf-8")) for p in sorted(directory.glob("*.md"))]


class Retriever:
    def __init__(self, documents: list[Document] | None = None):
        self.documents = documents if documents is not None else load_documents()
        self._tokens = {d.name: set(tokenize(d.text)) for d in self.documents}

    def search(self, query: str, top_k: int = 3) -> list[Hit]:
        """Return up to top_k documents sharing at least one term with the query, best first."""
        terms = set(tokenize(query))
        hits = [
            Hit(doc, len(terms & self._tokens[doc.name]))
            for doc in self.documents
        ]
        hits = [h for h in hits if h.score > 0]
        hits.sort(key=lambda h: (-h.score, h.document.name))
        return hits[:top_k]
