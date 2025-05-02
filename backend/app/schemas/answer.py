from pydantic import BaseModel
from typing import Optional

class StudentAnswerBase(BaseModel):
    question_id: int
    selected_option_id: Optional[int] = None
    text_answer: Optional[str] = None

class StudentAnswerCreate(StudentAnswerBase):
    pass

class StudentAnswerUpdate(StudentAnswerBase):
    is_correct: Optional[bool] = None
    points_earned: Optional[int] = None

class StudentAnswerResponse(StudentAnswerBase):
    id: int
    response_id: int
    is_correct: bool
    points_earned: int

    class Config:
        from_attributes = True