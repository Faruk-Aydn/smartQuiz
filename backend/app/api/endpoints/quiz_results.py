from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import func
from typing import List, Dict

from app.api import deps
from app.db.models.user import User, UserRole
from app.db.models.quiz import Quiz
from app.db.models.question import Question
from app.db.models.option import Option
from app.db.models.student_response import StudentResponse
from app.db.models.student_answer import StudentAnswer
from app.db.models.score import Score

router = APIRouter()

@router.get("/{quiz_id}/detailed-results")
def get_quiz_detailed_results(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz bulunamadı")
    # Sadece öğretmen veya ilgili öğrenci erişebilir
    if current_user.role != UserRole.TEACHER:
        raise HTTPException(status_code=403, detail="Sadece öğretmenler erişebilir")

    # Katılımcı sayısı
    student_responses = db.query(StudentResponse).filter(StudentResponse.quiz_id == quiz_id).all()
    participant_count = len(student_responses)

    # Öğrenci skorları
    scores = db.query(Score).filter(Score.quiz_id == quiz_id).all()
    if scores:
        average_score = sum(s.score for s in scores) / len(scores)
        max_score = max(s.score for s in scores)
        min_score = min(s.score for s in scores)
    else:
        average_score = max_score = min_score = 0

    # Soru bazında istatistikler
    question_stats = []
    questions = db.query(Question).filter(Question.quiz_id == quiz_id).all()
    for q in questions:
        answers = db.query(StudentAnswer).filter(StudentAnswer.question_id == q.id).all()
        correct_count = sum(1 for a in answers if a.is_correct)
        total = len(answers)
        correct_rate = correct_count / total if total > 0 else 0
        # Şıkların seçilme sayısı
        option_stats = []
        options = db.query(Option).filter(Option.question_id == q.id).all()
        for idx, opt in enumerate(options):
            selected_count = db.query(StudentAnswer).filter(
                StudentAnswer.question_id == q.id,
                StudentAnswer.selected_option_id == opt.id
            ).count()
            option_stats.append({
                "option": chr(65 + idx),  # A, B, C, D ...
                "text": opt.text,
                "selectedCount": selected_count
            })
        question_stats.append({
            "id": q.id,
            "text": q.text,
            "correctRate": correct_rate,
            "optionStats": option_stats
        })

    # Öğrenci detayları
    student_details = []
    for s in scores:
        student_answers = db.query(StudentAnswer).join(StudentResponse).filter(
            StudentResponse.student_id == s.student_id,
            StudentResponse.quiz_id == quiz_id
        ).all()
        answers = []
        for a in student_answers:
            # Soru metni
            # Soru metni (her zaman DB'den çek)
            # Soru metni (her zaman DB'den çek)
            question_obj = db.query(Question).filter(Question.id == a.question_id).first()
            question_text = question_obj.text if question_obj and question_obj.text else "-"

            # Seçilen şık metni (her zaman DB'den çek)
            selected_option_text = "-"
            if a.selected_option_id:
                selected_option = db.query(Option).filter(Option.id == a.selected_option_id).first()
                if selected_option and selected_option.text:
                    selected_option_text = selected_option.text

            # Doğru şık metni (her zaman DB'den çek)
            correct_option_text = "-"
            if a.question_id:
                correct_option = db.query(Option).filter(Option.question_id == a.question_id, Option.is_correct == True).first()
                if correct_option and correct_option.text:
                    correct_option_text = correct_option.text

            # is_correct değeri güvenli kontrol
            is_correct_value = False
            if a.selected_option_id:
                selected_option = db.query(Option).filter(Option.id == a.selected_option_id).first()
                correct_option = db.query(Option).filter(Option.question_id == a.question_id, Option.is_correct == True).first()
                if selected_option and correct_option and selected_option.id == correct_option.id:
                    is_correct_value = True
            elif a.is_correct is not None:
                is_correct_value = bool(a.is_correct)

            answers.append({
                "question_text": str(question_text) if question_text else "-",
                "selected_option": str(selected_option_text) if selected_option_text else "-",
                "correct_option": str(correct_option_text) if correct_option_text else "-",
                "is_correct": is_correct_value
            })
        student_details.append({
            "name": s.student.username if s.student else "-",
            "score": s.score,
            "correct": s.correct,
            "wrong": s.wrong,
            "answers": answers
        })

    # En çok yanlış yapılan sorular
    wrong_stats = [
        {
            "id": q[0],
            "text": q[1],
            "wrongCount": q[2]
        }
        for q in db.query(
            StudentAnswer.question_id,
            Question.text,
            func.count(StudentAnswer.id).label("wrong_count")
        ).join(Question, StudentAnswer.question_id == Question.id)
         .filter(StudentAnswer.is_correct == False, Question.quiz_id == quiz_id)
         .group_by(StudentAnswer.question_id, Question.text)
         .order_by(func.count(StudentAnswer.id).desc()).limit(3)
    ]

    return {
        "quizId": quiz.id,
        "title": quiz.title,
        "participantCount": participant_count,
        "averageScore": average_score,
        "maxScore": max_score,
        "minScore": min_score,
        "questions": question_stats,
        "students": student_details,
        "mostWrongQuestions": wrong_stats
    }
