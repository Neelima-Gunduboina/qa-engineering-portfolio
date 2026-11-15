from __future__ import annotations

from typing import Literal

from pydantic import BaseModel

Category = Literal["REAL_BUG", "FLAKY", "LOCATOR_DRIFT", "ENV_ISSUE", "TEST_BUG"]


class Classification(BaseModel):
    category: Category
    confidence: float
    rationale: str
