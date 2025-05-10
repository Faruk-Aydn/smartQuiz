from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.api import deps
from app.db.models.user import User, UserRole
from app.db.models.quiz import Quiz
from app.schemas.quiz import QuizResponse
from pydantic import BaseModel
from typing import List

class SolvedQuizInfo(BaseModel):
    quiz_id: int
    quiz_title: str
    response_id: int
    completed_at: str = None

    class Config:
        orm_mode = True
from app.schemas.response import StudentResponseCreate, StudentResponseWithAnswers
from app.services.scoring_service import calculate_score
from app.db.models.student_response import StudentResponse
from app.db.models.student_answer import StudentAnswer

router = APIRouter()

class StudentQuizResultDetail(BaseModel):
    quiz_id: int
    quiz_title: str
    total_score: int
    completed_at: str = None
    questions: list
    answers: list

    class Config:
        orm_mode = True

@router.get("/results/{quiz_id}", response_model=StudentQuizResultDetail)
def get_student_quiz_result_detail(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    """
    Öğrencinin çözdüğü bir quizin detaylı sonucunu getirir
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Sadece öğrenciler görebilir")
    response = db.query(StudentResponse).filter(
        StudentResponse.quiz_id == quiz_id,
        StudentResponse.student_id == current_user.id,
        StudentResponse.completed_at != None
    ).first()
    if not response:
        raise HTTPException(status_code=404, detail="Sonuç bulunamadı")
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    # Sorular ve seçenekler
    questions = []
    for q in quiz.questions:
        questions.append({
            "id": q.id,
            "text": q.text,
            "options": [
                {"id": o.id, "text": o.text, "is_correct": o.is_correct}
                for o in q.options
            ]
        })
    # Öğrencinin verdiği cevaplar (Android modeline uygun şekilde)
    answers = []
    for a in response.answers:
        question = next((q for q in quiz.questions if q.id == a.question_id), None)
        question_text = question.text if question else ""
        selected_option = next((o for o in question.options if o.id == a.selected_option_id), None) if question else None
        selected_option_text = selected_option.text if selected_option else ""
        correct_option = next((o for o in question.options if o.is_correct), None) if question else None
        correct_option_text = correct_option.text if correct_option else ""
        is_correct = selected_option.id == correct_option.id if selected_option and correct_option else False
        answers.append({
            "question_text": question_text,
            "selected_option": selected_option_text,
            "correct_option": correct_option_text,
            "is_correct": is_correct
        })
    return StudentQuizResultDetail(
        quiz_id=quiz.id,
        quiz_title=quiz.title,
        total_score=response.total_score,
        completed_at=response.completed_at.isoformat() if response.completed_at else None,
        questions=questions,
        answers=answers
    )

@router.get("/solved-quizzes", response_model=List[SolvedQuizInfo])
def get_solved_quizzes(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Öğrencinin çözdüğü tüm quizlerin listesini döner
    """
    if current_user.role != UserRole.STUDENT:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğrenciler bu endpoint'i kullanabilir"
        )
    responses = db.query(StudentResponse).filter(
        StudentResponse.student_id == current_user.id,
        StudentResponse.completed_at != None
    ).offset(skip).limit(limit).all()

    solved_quizzes = [
        SolvedQuizInfo(
            quiz_id=r.quiz_id,
            quiz_title=r.quiz.title if r.quiz else "-",
            response_id=r.id,
            completed_at=r.completed_at.isoformat() if r.completed_at else None
        )
        for r in responses
    ]
    return solved_quizzes

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
    
    db_response = StudentResponse(
        quiz_id=response_in.quiz_id,
        student_id=current_user.id
    )
    db.add(db_response)
    db.flush()
    
    for answer_in in response_in.answers:
        db_answer = StudentAnswer(
            question_id=answer_in.question_id,
            selected_option_id=answer_in.selected_option_id,
            text_answer=answer_in.text_answer,
            response_id=db_response.id
        )
        db.add(db_answer)
    
    db.flush()
    calculate_score(db, db_response.id)
    db.commit()
    db.refresh(db_response)
    return db_response
