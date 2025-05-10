from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Optional

from app.api import deps
from app.db.models.user import User, UserRole
from app.schemas.quiz import QuizCreate, QuizResponse, QuizWithQuestions
from app.schemas.question import QuestionCreate, QuestionResponse, QuestionGenerateRequest
from app.crud.quiz import create_quiz, get_quiz, get_quizzes_by_teacher, generate_qr_code
from app.core.ai import generate_questions

router = APIRouter()

from app.api.endpoints.quiz_results import router as quiz_results_router
router.include_router(quiz_results_router)

@router.post("/", response_model=QuizResponse)
def create_new_quiz(
    quiz_in: QuizCreate,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğretmenler quiz oluşturabilir"
        )
    
    # Quiz oluştu
    quiz = create_quiz(db, obj_in=quiz_in, teacher_id=current_user.id)
    db.commit()
    db.refresh(quiz)

    # QuizCreate ile birlikte sorular ve şıklar geldiyse, ekle
    for question_in in quiz_in.questions:
        question = Question(
            text=question_in.text,
            question_type=question_in.question_type,
            points=question_in.points,
            quiz_id=quiz.id
        )
        db.add(question)
        db.commit()
        db.refresh(question)
        for opt in question_in.options:
            if not opt.text or not opt.text.strip():
                raise HTTPException(status_code=400, detail="Şık metni (option.text) boş olamaz!")
            option = Option(
                text=opt.text.strip(),
                is_correct=opt.is_correct,
                question_id=question.id
            )
            db.add(option)
        db.commit()
    db.refresh(quiz)

    # QR kod oluştur
    qr_code = generate_qr_code(quiz.id)
    
    return quiz

@router.post("/{quiz_id}/generate-questions", response_model=List[QuestionResponse])
def generate_questions(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    question_data: QuestionGenerateRequest,
    current_user: User = Depends(deps.get_current_user)
):
    """
    AI ile quiz soruları oluşturma
    """
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğretmenler soru oluşturabilir"
        )
    
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı"
        )
    
    try:
        questions = generate_ai_questions(
            db=db,
            quiz_id=quiz_id,
            topic=question_data.topic,
            difficulty=question_data.difficulty,
            num_questions=question_data.num_questions
        )
        return questions
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Soru oluşturma hatası: {str(e)}"
        )

@router.get("/my-quizzes", response_model=List[QuizResponse])
def get_my_quizzes(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Sadece öğretmenler kendi quizlerini listeleyebilir"
        )
    
    quizzes = get_quizzes_by_teacher(db, teacher_id=current_user.id)
    return quizzes

@router.get("/{quiz_id}", response_model=QuizWithQuestions)
def get_quiz_details(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    quiz = get_quiz(db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Quiz bulunamadı"
        )
    
    # Öğretmen kendi quizine erişebilir
    if current_user.role == UserRole.TEACHER and quiz.teacher_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Bu quize erişim izniniz yok"
        )
    
    return quiz