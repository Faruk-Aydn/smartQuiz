from typing import Any, List

from fastapi import APIRouter, Depends, HTTPException, status, Body
from sqlalchemy.orm import Session, joinedload

from app.api import deps
from app.db.models.user import User, UserRole
from app.schemas.quiz import QuizResponse, QuizCreate, QuizUpdate, QuizWithQuestions
from app.schemas.question import QuestionCreate, QuestionResponse
from app.db.models.quiz import Quiz
from app.db.models.question import Question
from app.db.models.option import Option
from app.crud.quiz import create_quiz, get_quiz, update_quiz, generate_qr_code, get_quizzes
from app.services.ai_service import get_ai_service
from pydantic import BaseModel
from app.services.quiz_service import generate_ai_questions
from app.schemas.score import StudentQuizResult, ScoreCreate
from app.crud.score import get_quiz_results, create_score

router = APIRouter()

from sqlalchemy.orm import joinedload


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
    quiz = create_quiz(db=db, obj_in=quiz_in, teacher_id=current_user.id)
    db.commit()
    db.refresh(quiz)

    # QuizCreate ile birlikte sorular ve şıklar geldiyse, ekle
    for question_in in getattr(quiz_in, "questions", []):
        question = Question(
            text=question_in.text.strip(),
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
    return quiz

@router.get("/", response_model=List[QuizResponse])
def read_quizzes(
    db: Session = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quizleri getir. (Öğretmen için sadece kendi oluşturduğu quizler gelir)
    Android uygulamasında bu endpoint ile quizler listelenmeli. Kullanıcı birine tıklayınca aşağıdaki detaylı sonuç endpointine istek atılmalı:
    
    GET /api/v1/quizzes/{quiz_id}/detailed-results
    Authorization: Bearer <token>
    
    Böylece istenen quizin detaylı sonucu alınır.
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

class AIGenerateQuizRequest(BaseModel):
    topic: str
    difficulty: str = "medium"
    num_questions: int = 5

@router.post("/ai-generate", response_model=List[dict])
def ai_generate_quiz(
    *,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    req: AIGenerateQuizRequest = Body(...)
):
    """
    Yapay zeka ile quiz soruları oluştur (sadece öğretmen)
    """
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Yeterli izniniz yok")
    ai_service = get_ai_service()
    questions = ai_service.generate_quiz_questions(req.topic, req.difficulty, req.num_questions)
    return questions

from typing import Optional

class AIGenerateAndSaveQuizRequest(BaseModel):
    topic: str
    difficulty: str = "medium"
    num_questions: int = 5
    title: str
    description: str = ""
    duration_minutes: Optional[int] = None

@router.post("/ai-generate-and-save", response_model=QuizResponse)
def ai_generate_and_save_quiz(
    *,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    req: AIGenerateAndSaveQuizRequest = Body(...)
):
    """
    AI ile oluşturulan soruları quiz olarak kaydeder (sadece öğretmen)
    """
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Yeterli izniniz yok")
    # 1. Quiz oluştur
    quiz_in = QuizCreate(
        title=req.title,
        description=req.description,
        subject=req.topic,
        grade_level="",  # None yerine boş string
        duration_minutes=req.duration_minutes
    )
    quiz = create_quiz(db=db, obj_in=quiz_in, teacher_id=current_user.id)
    # 2. AI ile soruları oluşturup kaydet
    generate_ai_questions(db=db, quiz_id=quiz.id, topic=req.topic, difficulty=req.difficulty, num_questions=req.num_questions)
    db.refresh(quiz)
    return quiz

@router.get("/{quiz_id}/with-questions", response_model=QuizWithQuestions)
def read_quiz_with_questions(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz detaylarını ve sorularını getir
    """
    quiz = db.query(Quiz).options(joinedload(Quiz.questions)).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    if current_user.role == UserRole.TEACHER and quiz.teacher_id != current_user.id:
        raise HTTPException(status_code=403, detail="Yeterli izniniz yok")
    quiz_response = QuizResponse.from_orm(quiz)
    questions = [QuestionResponse.from_orm(q) for q in quiz.questions]
    return {"quiz": quiz_response, "questions": questions}

@router.post("/{quiz_id}/questions", response_model=QuestionResponse)
def add_question_to_quiz(
    *,
    db: Session = Depends(deps.get_db),
    quiz_id: int,
    question_in: QuestionCreate,
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Quiz'e yeni soru ekle
    """
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    if current_user.role != UserRole.TEACHER or quiz.teacher_id != current_user.id:
        raise HTTPException(status_code=403, detail="Yeterli izniniz yok")
    if len(question_in.options) != 5:
        raise HTTPException(status_code=400, detail="Her soru için tam olarak 5 seçenek girilmelidir.")
    # Soru metni boşsa hata ver
    if not question_in.text or not question_in.text.strip():
        raise HTTPException(status_code=400, detail="Soru metni (text) boş olamaz!")
    question = Question(
        text=question_in.text.strip(),
        question_type=question_in.question_type,
        points=question_in.points,
        quiz_id=quiz_id
    )
    db.add(question)
    db.commit()
    db.refresh(question)
    # Gelen optionsları ekle
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
    db.refresh(question)
    return QuestionResponse.from_orm(question)

class AnswerSubmit(BaseModel):
    question_id: int
    selected_option: int

class QuizSubmitRequest(BaseModel):
    answers: List[AnswerSubmit]

class QuizSubmitResult(BaseModel):
    correct: int
    wrong: int
    score: int

@router.post("/{quiz_id}/submit", response_model=QuizSubmitResult)
def submit_quiz(
    quiz_id: int,
    req: QuizSubmitRequest,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    from app.db.models.student_response import StudentResponse
    from app.db.models.student_answer import StudentAnswer
    quiz = get_quiz(db, quiz_id)
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    correct = 0
    wrong = 0
    # 1. StudentResponse oluştur
    db_response = StudentResponse(
        quiz_id=quiz_id,
        student_id=current_user.id
    )
    db.add(db_response)
    db.flush()  # id oluşsun diye
    # 2. StudentAnswer kayıtlarını oluştur
    for answer in req.answers:
        question = next((q for q in quiz.questions if q.id == answer.question_id), None)
        selected_option_obj = next((opt for opt in question.options if opt.id == answer.selected_option), None) if question else None
        is_correct = selected_option_obj.is_correct if selected_option_obj else False
        if is_correct:
            correct += 1
        else:
            wrong += 1
        db_answer = StudentAnswer(
            question_id=answer.question_id,
            selected_option_id=answer.selected_option,
            is_correct=is_correct,
            response_id=db_response.id
        )
        db.add(db_answer)
    db.flush()
    score = int((correct / len(quiz.questions)) * 100) if quiz.questions else 0
    db_response.total_score = score
    import datetime
    db_response.completed_at = datetime.datetime.utcnow()
    # 3. Score kaydı oluştur
    if current_user.role == UserRole.STUDENT:
        score_obj = ScoreCreate(
            student_id=current_user.id,
            total_points=score,
            quizzes_completed=1,  # veya uygun şekilde
            correct=correct,
            wrong=wrong
        )
        create_score(db, score_obj, student_id=current_user.id, quiz_id=quiz_id)
    db.commit()
    db.refresh(db_response)
    return QuizSubmitResult(correct=correct, wrong=wrong, score=score)

@router.get("/{quiz_id}/results", response_model=List[StudentQuizResult])
def quiz_results(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    if current_user.role != UserRole.TEACHER or quiz.teacher_id != current_user.id:
        raise HTTPException(status_code=403, detail="Yetkiniz yok")
    return get_quiz_detailed_results(db, quiz_id)

from app.crud.score import get_quiz_detailed_results
from app.schemas.quiz_result import StudentQuizResult
