from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session, joinedload

from app.api import deps
from app.db.models.user import User, UserRole
from app.db.models.question import Question

router = APIRouter()

from fastapi import Body
from app.db.models.option import Option

@router.put("/{question_id}/set-correct-option")
def set_correct_option(
    question_id: int,
    body: dict = Body(...),
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
):
    """
    Sorunun doğru şıkkını güncelle (sadece öğretmen ve kendi quizine aitse).
    """
    question = db.query(Question).filter(Question.id == question_id).first()
    if not question:
        raise HTTPException(status_code=404, detail="Question not found")
    if current_user.role != UserRole.TEACHER or question.quiz is None or question.quiz.teacher_id != current_user.id:
        raise HTTPException(status_code=403, detail="Permission denied")

    correct_option_id = body.get("correct_option_id")
    option = db.query(Option).filter(Option.id == correct_option_id, Option.question_id == question_id).first()
    if not option:
        raise HTTPException(status_code=404, detail="Option not found for this question")

    # Tüm şıkları önce yanlış yap
    for opt in question.options:
        opt.is_correct = False
    # Doğru şıkkı işaretle
    option.is_correct = True
    db.commit()
    return {"success": True}

@router.delete("/{question_id}", status_code=204)
def delete_question(
    question_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
):
    """
    Soru sil (sadece öğretmen ve kendi quizine aitse). Tüm ilişkili studentanswer ve option kayıtlarını da siler.
    """
    from app.db.models.question import Question
    from app.db.models.option import Option
    from app.db.models.student_answer import StudentAnswer

    question = db.query(Question).options(joinedload(Question.quiz), joinedload(Question.options)).filter(Question.id == question_id).first()
    if not question:
        raise HTTPException(status_code=404, detail="Question not found")
    if current_user.role != UserRole.TEACHER or question.quiz is None or question.quiz.teacher_id != current_user.id:
        raise HTTPException(status_code=403, detail="Permission denied")

    # Önce bu soruya bağlı tüm seçenekler için, ilgili studentanswer kayıtlarını sil
    for option in question.options:
        db.query(StudentAnswer).filter(StudentAnswer.selected_option_id == option.id).delete(synchronize_session=False)
    db.commit()
    # Sonra seçenekleri sil
    db.query(Option).filter(Option.question_id == question.id).delete(synchronize_session=False)
    db.commit()
    # Son olarak soruya bağlı studentanswer varsa onları da sil (örn. kısa cevap)
    db.query(StudentAnswer).filter(StudentAnswer.question_id == question.id).delete(synchronize_session=False)
    db.commit()
    # Son olarak soruyu sil
    db.delete(question)
    db.commit()
    return
