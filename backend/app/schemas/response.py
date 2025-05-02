from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

from app.schemas.answer import StudentAnswerCreate, StudentAnswerResponse

class StudentResponseBase(BaseModel):
    quiz_id: int

class StudentResponseCreate(StudentResponseBase):
    answers: List[StudentAnswerCreate] = []

class StudentResponseUpdate(StudentResponseBase):
    completed_at: Optional[datetime] = None
    total_score: Optional[int] = None

class StudentResponseResponse(StudentResponseBase):
    id: int
    student_id: int
    started_at: datetime
    completed_at: Optional[datetime] = None
    total_score: int

    class Config:
        from_attributes = True

class StudentResponseWithAnswers(StudentResponseResponse):
    answers: List[StudentAnswerResponse] = []