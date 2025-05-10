from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List
from app.schemas.student_result import StudentAnswerDetail
from app.api.deps import get_db, get_current_user
from app.db.models.answer import Answer
from app.db.models.question import Question
from app.db.models.user import User

router = APIRouter()

@router.get("/student/results/{quiz_id}", response_model=List[StudentAnswerDetail])
def get_student_results(quiz_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    answers = db.query(Answer).join(Question).filter(
        Answer.quiz_id == quiz_id,
        Answer.student_id == current_user.id
    ).all()
    result = []
    for answer in answers:
        result.append(StudentAnswerDetail(
            question_text=answer.question.text,
            selected_option=answer.selected_option,
            correct_option=answer.question.correct_option,
            is_correct=(answer.selected_option == answer.question.correct_option)
        ))
    return result
