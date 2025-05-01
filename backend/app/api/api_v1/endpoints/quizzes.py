from typing import Any, List

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api import deps
from app.db.models.user import User, UserRole
from app.schemas.quiz import QuizResponse, QuizCreate, QuizUpdate
from app.crud.quiz import create_quiz, get_quiz, update_quiz, generate_qr_code

router = APIRouter()

@router.post("/", response_model=QuizResponse)
def create_quiz_endpoint(
    *,
    db: Session = Depends(deps.get_db),
    quiz_in: QuizCreate,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Yeni quiz oluştur
    """
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Yeterli izniniz yok",
        )
    quiz = create_quiz(db=db, quiz_in=quiz_in, teacher_id=current_user.id)
    return quiz

@router.get("/", response_model=List[QuizResponse])
def read_quizzes(
    db: Session = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quizleri getir
    """
    if current_user.role == UserRole.TEACHER:
        quizzes = get_quizzes(db, teacher_id=current_user.id, skip=skip, limit=limit)
    else:
        quizzes = get_quizzes(db, skip=skip, limit=limit)
    return quizzes

@router.get("/{quiz_id}", response_model=QuizResponse)
def read_quiz(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz detaylarını getir
    """
    quiz = get_quiz(db=db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı",
        )
    if current_user.role == UserRole.TEACHER and quiz.teacher_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Yeterli izniniz yok",
        )
    return quiz

@router.put("/{quiz_id}", response_model=QuizResponse)
def update_quiz_endpoint(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    quiz_in: QuizUpdate,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz güncelle
    """
    quiz = get_quiz(db=db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı",
        )
    if current_user.role != UserRole.TEACHER or quiz.teacher_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Yeterli izniniz yok",
        )
    quiz = update_quiz(db=db, quiz=quiz, quiz_in=quiz_in)
    return quiz

@router.delete("/{quiz_id}", response_model=QuizResponse)
def delete_quiz_endpoint(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz sil
    """
    quiz = get_quiz(db=db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı",
        )
    if current_user.role != UserRole.TEACHER or quiz.teacher_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Yeterli izniniz yok",
        )
    # Quiz'i doğrudan sil
    db.delete(quiz)
    db.commit()
    return quiz

@router.post("/{quiz_id}/generate-qr", response_model=QuizResponse)
def generate_qr_code_endpoint(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz için QR kod oluştur
    """
    quiz = get_quiz(db=db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı",
        )
    if current_user.role != UserRole.TEACHER or quiz.teacher_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Yeterli izniniz yok",
        )
    quiz = generate_qr_code(db=db, quiz=quiz)
    return quiz