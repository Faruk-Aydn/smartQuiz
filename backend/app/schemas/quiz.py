from pydantic import BaseModel
from typing import Optional, List, Dict, Any
from datetime import datetime

from app.schemas.question import QuestionResponse

class QuizBase(BaseModel):
    title: str
    description: Optional[str] = None
    subject: str
    grade_level: str

class QuizCreate(QuizBase):
    pass

class QuizUpdate(QuizBase):
    is_active: Optional[bool] = None

class QuizResponse(QuizBase):
    id: int
    created_at: datetime
    is_active: bool
    teacher_id: int
    qr_code: Optional[str] = None

    class Config:
        from_attributes = True

class QuizWithQuestions(BaseModel):
    quiz: QuizResponse
    questions: List[QuestionResponse]

    class Config:
        from_attributes = True