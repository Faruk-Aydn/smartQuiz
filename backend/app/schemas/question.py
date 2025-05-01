from pydantic import BaseModel
from typing import Optional, List

from app.db.models.question import QuestionType
from app.schemas.option import OptionResponse, OptionCreate

class QuestionBase(BaseModel):
    text: str
    question_type: QuestionType
    points: Optional[int] = 1

class QuestionCreate(QuestionBase):
    options: List[OptionCreate] = []

class QuestionUpdate(QuestionBase):
    pass

class QuestionResponse(QuestionBase):
    id: int
    quiz_id: int
    options: List[OptionResponse] = []

    class Config:
        from_attributes = True  # orm_mode yerine from_attributes kullanılıyor

class QuestionWithOptions(QuestionResponse):
    options: List[OptionResponse] = []

class QuestionGenerateRequest(BaseModel):
    topic: str
    difficulty: str
    num_questions: int = 5