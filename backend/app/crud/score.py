from typing import List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import desc, func
from app.db.models.score import Score
from app.schemas.score import ScoreCreate

def get_top_scores(
    db: Session,
    quiz_id: int,
    skip: int = 0,
    limit: int = 100
) -> List[Score]:
    """
    Get top scores for a specific quiz
    """
    return db.query(Score)\
        .filter(Score.quiz_id == quiz_id)\
        .order_by(desc(Score.score))\
        .offset(skip)\
        .limit(limit)\
        .all()

def get_student_rank(
    db: Session,
    student_id: int,
) -> Optional[Score]:
    """
    Get a student's highest score and rank
    """
    return db.query(Score)\
        .filter(Score.student_id == student_id)\
        .order_by(desc(Score.score))\
        .first()

def create_score(
    db: Session,
    score: ScoreCreate,
    student_id: int,
    quiz_id: int
) -> Score:
    """
    Create a new score record
    """
    db_score = Score(
        score=score.total_points,
        student_id=student_id,
        quiz_id=quiz_id,
        correct=score.correct,
        wrong=score.wrong
    )
    db.add(db_score)
    db.commit()
    db.refresh(db_score)
    return db_score

def get_quiz_results(db: Session, quiz_id: int):
    # Score tablosunda quiz_id'ye göre tüm skorları çek, öğrenci adını da ekle
    results = db.query(Score).filter(Score.quiz_id == quiz_id).all()
    output = []
    for r in results:
        output.append({
            "studentName": r.student.username if r.student else "-",
            "correct": r.correct,
            "wrong": r.wrong,
            "score": r.score
        })
    return output

def get_quiz_detailed_results(db: Session, quiz_id: int):
    from app.db.models.student_response import StudentResponse
    from app.db.models.student_answer import StudentAnswer
    from app.db.models.question import Question
    from app.db.models.option import Option
    from app.schemas.quiz_result import StudentQuizResult, StudentAnswerDetail
    import logging
    from collections import defaultdict, Counter

    from sqlalchemy.orm import joinedload
    responses = db.query(StudentResponse).options(
        joinedload(StudentResponse.answers).joinedload(StudentAnswer.question),
        joinedload(StudentResponse.answers).joinedload(StudentAnswer.selected_option)
    ).filter(StudentResponse.quiz_id == quiz_id).all()
    detailed_results = []

    # --- Aggregate analysis ---
    # 1. Soru ve şık bazında seçilme sayısı
    question_option_counter = defaultdict(lambda: Counter())  # {question_id: Counter({option_id: count})}
    question_wrong_counter = Counter()  # {question_id: wrong_count}
    question_total_counter = Counter()  # {question_id: total_count}
    question_texts = dict()  # {question_id: question_text}
    option_texts = dict()    # {option_id: option_text}
    correct_option_ids = dict() # {question_id: correct_option_id}

    for resp in responses:
        try:
            student = getattr(resp, 'student', None)
            answers = []
            correct = 0
            wrong = 0
            for ans in getattr(resp, 'answers', []):
                # Soru ID ve Seçilen Şık ID fallback
                question_id = getattr(ans, 'question_id', None)
                selected_option_id = getattr(ans, 'selected_option_id', None)
                # Soru nesnesi yoksa veritabanından çek
                question = getattr(ans, 'question', None)
                if not question and question_id:
                    question = db.query(Question).filter(Question.id == question_id).first()
                question_text = getattr(question, 'text', None) if question else None
                if not question_text:
                    question_text = f"Soru ID: {question_id}" if question_id else "Bilinmiyor"
                question_texts[question_id] = question_text
                # Doğru şık id’sini bul
                correct_option = db.query(Option).filter(Option.question_id == question_id, Option.is_correct == True).first()
                correct_option_id = correct_option.id if correct_option else None
                is_correct = (selected_option_id == correct_option_id)
                question_total_counter[question_id] += 1
                if not is_correct:
                    question_wrong_counter[question_id] += 1
                if selected_option_id:
                    question_option_counter[question_id][selected_option_id] += 1
                # Seçilen şık metni (her zaman metin olarak)
                selected_option_text = None
                if hasattr(ans, 'selected_option') and ans.selected_option and getattr(ans.selected_option, 'text', None):
                    selected_option_text = ans.selected_option.text
                elif selected_option_id:
                    # Option'ı veritabanından çek ve metni al
                    opt = db.query(Option).filter(Option.id == selected_option_id).first()
                    selected_option_text = opt.text if opt and opt.text else None
                if not selected_option_text:
                    selected_option_text = "Bilinmiyor"
                option_texts[selected_option_id] = selected_option_text
                # Doğru şık metni (her zaman metin olarak)
                correct_option_id = None
                correct_option_text = None
                if question:
                    correct_option = db.query(Option).filter(Option.question_id == question.id, Option.is_correct == True).first()
                    if correct_option:
                        correct_option_id = correct_option.id
                        correct_option_text = correct_option.text if correct_option.text else "Bilinmiyor"
                        correct_option_ids[question_id] = correct_option_id
                        option_texts[correct_option_id] = correct_option_text
                if not correct_option_text:
                    correct_option_text = "Bilinmiyor"
                if is_correct:
                    correct += 1
                else:
                    wrong += 1
                answers.append(StudentAnswerDetail(
                    question_text=question_text,
                    selected_option=selected_option_text,
                    correct_option=correct_option_text,
                    is_correct=is_correct
                ))
            detailed_results.append(StudentQuizResult(
                student_name=getattr(student, 'username', '-') if student else "-",
                correct=correct,
                wrong=wrong,
                score=getattr(resp, 'total_score', 0),
                answers=answers
            ))
        except Exception as e:
            logging.error(f"Quiz detailed result error for response {getattr(resp, 'id', None)}: {e}")
            continue

    # 2. Her soru için analiz
    question_analysis = {}
    for qid, opt_counter in question_option_counter.items():
        total = question_total_counter[qid]
        wrong = question_wrong_counter[qid]
        correct_option_id = correct_option_ids.get(qid, None)
        correct_option_text = option_texts.get(correct_option_id, '') if correct_option_id else ''
        option_stats = []
        for oid, count in opt_counter.items():
            option_stats.append({
                'option_id': oid,
                'option_text': option_texts.get(oid, ''),
                'selected_count': count
            })
        question_analysis[qid] = {
            'question_text': question_texts.get(qid, ''),
            'total_answers': total,
            'wrong_count': wrong,
            'correct_option_text': correct_option_text,
            'options': option_stats,
            'wrong_ratio': (wrong / total) if total > 0 else 0.0
        }

    # 3. En çok yanlış yapılan soru
    if question_wrong_counter:
        most_wrong_qid, most_wrong_count = question_wrong_counter.most_common(1)[0]
        most_wrong_question = {
            'question_id': most_wrong_qid,
            'question_text': question_texts.get(most_wrong_qid, ''),
            'wrong_count': most_wrong_count
        }
    else:
        most_wrong_question = None

    analysis = {
        'question_analysis': question_analysis,
        'most_wrong_question': most_wrong_question
    }

    return {
        'results': detailed_results,
        'analysis': analysis
    }