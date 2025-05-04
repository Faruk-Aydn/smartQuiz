from pydantic import BaseModel
from datetime import datetime

class ScoreBase(BaseModel):
    total_points: int
    quizzes_completed: int

class ScoreCreate(ScoreBase):
    student_id: int
    correct: int
    wrong: int

class ScoreUpdate(ScoreBase):
    pass

class ScoreResponse(ScoreBase):
    id: int
    student_id: int
    student_name: str  # Bu alan CRUD işlemlerinde eklenecek
    rank: int  # Bu alan CRUD işlemlerinde eklenecek
    last_updated: datetime

    class Config:
        from_attributes = True

class StudentQuizResult(BaseModel):
    studentName: str
    correct: int
    wrong: int
    score: int