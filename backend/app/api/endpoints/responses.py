from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.api import deps
from app.db.models.user import User, UserRole
from app.schemas.response import StudentResponseCreate, StudentResponseWithAnswers
from app.db.models.student_response import StudentResponse
from app.services.scoring_service import calculate_score

router = APIRouter()

@router.post("/", response_model=StudentResponseWithAnswers)
def submit_quiz_response(
    *,
    db: Session = Depends(deps.get_db),
    response_in: StudentResponseCreate,
    current_user: User = Depends(deps.get_current_user)
):
    """
    Öğrenci quiz yanıtlarını gönderme
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğrenciler yanıt gönderebilir"
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
    
    db.commit()
    db.refresh(db_response)
    return db_response

@router.get("/my-responses", response_model=List[StudentResponseWithAnswers])
def get_my_responses(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Öğrencinin kendi yanıtlarını görüntüleme
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğrenciler kendi yanıtlarını görüntüleyebilir"
        )
    
    responses = db.query(StudentResponse).filter(
        StudentResponse.student_id == current_user.id
    ).offset(skip).limit(limit).all()
    
    return responses