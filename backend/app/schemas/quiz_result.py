from pydantic import BaseModel
from typing import List

class StudentAnswerDetail(BaseModel):
    question_text: str
    selected_option: str
    correct_option: str
    is_correct: bool

    class Config:
        orm_mode = True
        alias_generator = lambda s: s.replace("_", "") + ''.join(''.join(c if c.islower() else c.lower() for c in word) for word in s.split("_")[1:])

class StudentQuizResult(BaseModel):
    student_name: str
    correct: int
    wrong: int
    score: int
    answers: List[StudentAnswerDetail]

    class Config:
        orm_mode = True
        alias_generator = lambda s: s.replace("_", "") + ''.join(''.join(c if c.islower() else c.lower() for c in word) for word in s.split("_")[1:])
