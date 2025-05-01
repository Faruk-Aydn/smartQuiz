from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.api import deps
from app.db.models.user import User, UserRole
from app.db.models.quiz import Quiz
from app.schemas.quiz import QuizResponse
from app.schemas.response import StudentResponseCreate, StudentResponseWithAnswers
from app.services.scoring_service import calculate_score
from app.db.models.student_response import StudentResponse
from app.db.models.student_answer import StudentAnswer

router = APIRouter()

@router.get("/available-quizzes", response_model=List[QuizResponse])
def get_available_quizzes(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Öğrencinin katılabileceği aktif quizleri listeler
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğrenciler bu endpoint'i kullanabilir"
        )
    
    # Aktif quizleri getir
    quizzes = db.query(Quiz).filter(Quiz.is_active == True).offset(skip).limit(limit).all()
    return quizzes

@router.post("/submit-quiz", response_model=StudentResponseWithAnswers)
def submit_quiz(
    response_in: StudentResponseCreate,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    """
    Öğrencinin quiz yanıtlarını kaydeder ve puanlar
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğrenciler quiz yanıtlayabilir"
        )
    
    # Yanıtı oluştur
    db_response = StudentResponse(
        quiz_id=response_in.quiz_id,
        student_id=current_user.id
    )
    db.add(db_response)
    db.flush()
    
    # Cevapları ekle
    for answer_in in response_in.answers:
        db_answer = StudentAnswer(
            question_id=answer_in.question_id,
            selected_option_id=answer_in.selected_option_id,
            text_answer=answer_in.text_answer,
            response_id=db_response.id
        )
        db.add(db_answer)
    
    db.flush()
    
    # Puanlamayı yap
    calculate_score(db, db_response.id)
    
    db.refresh(db_response)
    return db_response