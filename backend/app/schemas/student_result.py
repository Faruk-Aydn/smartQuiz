from pydantic import BaseModel

class StudentAnswerDetail(BaseModel):
    question_text: str
    selected_option: str
    correct_option: str
    is_correct: bool
