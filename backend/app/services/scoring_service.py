from sqlalchemy.orm import Session
from typing import List, Dict, Any
import datetime

from app.db.models.student_response import StudentResponse
from app.db.models.student_answer import StudentAnswer
from app.db.models.question import Question
from app.db.models.option import Option
from app.db.models.user import User

def calculate_score(db: Session, response_id: int) -> int:
    """
    Öğrenci yanıtlarını değerlendirir ve toplam puanı hesaplar
    
    Args:
        db: Veritabanı oturumu
        response_id: Değerlendirilecek yanıt ID'si
        
    Returns:
        Toplam puan
    """
    # Yanıtı al
    response = db.query(StudentResponse).filter(StudentResponse.id == response_id).first()
    if not response:
        return 0
    
    total_score = 0
    
    # Her bir cevabı kontrol et
    for answer in response.answers:
        question = answer.question
        
        # Çoktan seçmeli veya doğru/yanlış soruları için
        if answer.selected_option_id:
            selected_option = db.query(Option).filter(Option.id == answer.selected_option_id).first()
            if selected_option and selected_option.is_correct:
                answer.is_correct = True
                answer.points_earned = question.points
                total_score += question.points
            else:
                answer.is_correct = False
                answer.points_earned = 0
        
        # Kısa cevaplı sorular için (basit eşleştirme)
        elif answer.text_answer and question.question_type == "short_answer":
            # Doğru cevabı al (basit implementasyon)
            correct_answer = db.query(Option).filter(
                Option.question_id == question.id,
                Option.is_correct == True
            ).first()
            
            if correct_answer and answer.text_answer.lower().strip() == correct_answer.text.lower().strip():
                answer.is_correct = True
                answer.points_earned = question.points
                total_score += question.points
            else:
                answer.is_correct = False
                answer.points_earned = 0
    
    # Yanıtı güncelle
    response.completed_at = datetime.datetime.utcnow()
    response.total_score = total_score
    db.add(response)
    db.commit()
    
    return total_score

def get_leaderboard(db: Session, quiz_id: int, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Quiz için liderlik tablosunu döndürür
    
    Args:
        db: Veritabanı oturumu
        quiz_id: Quiz ID
        limit: Maksimum kayıt sayısı
        
    Returns:
        Liderlik tablosu listesi
    """
    # Quiz'e ait tamamlanmış yanıtları al
    responses = db.query(StudentResponse).filter(
        StudentResponse.quiz_id == quiz_id,
        StudentResponse.completed_at.isnot(None)
    ).order_by(StudentResponse.total_score.desc()).limit(limit).all()
    
    leaderboard = []
    for response in responses:
        student = db.query(User).filter(User.id == response.student_id).first()
        if student:
            leaderboard.append({
                "student_id": student.id,
                "student_name": f"{student.first_name} {student.last_name}",
                "score": response.total_score,
                "completion_time": response.completed_at - response.started_at
            })
    
    return leaderboard