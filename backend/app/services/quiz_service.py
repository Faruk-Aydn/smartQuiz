from typing import List, Dict, Any
from sqlalchemy.orm import Session

from app.db.models.quiz import Quiz
from app.db.models.question import Question, QuestionType
from app.db.models.option import Option
from app.services.ai_service import get_ai_service

def generate_ai_questions(db: Session, quiz_id: int, topic: str, difficulty: str, num_questions: int = 5) -> List[Question]:
    """
    AI kullanarak quiz soruları oluşturur ve veritabanına kaydeder
    
    Args:
        db: Veritabanı oturumu
        quiz_id: Quiz ID
        topic: Konu başlığı
        difficulty: Zorluk seviyesi
        num_questions: Oluşturulacak soru sayısı
        
    Returns:
        Oluşturulan soru listesi
    """
    # AI servisini al
    ai_service = get_ai_service()
    
    # AI ile soruları oluştur
    ai_questions = ai_service.generate_quiz_questions(topic, difficulty, num_questions)
    
    # Soruları veritabanına kaydet
    db_questions = []
    
    for q_data in ai_questions:
        # Soru oluştur
        question = Question(
            quiz_id=quiz_id,
            text=q_data["question"],
            question_type=QuestionType.MULTIPLE_CHOICE,
            points=10,  # Varsayılan puan
            explanation=q_data.get("explanation", "")
        )
        db.add(question)
        db.flush()  # ID almak için flush
        
        # Seçenekleri oluştur
        for opt_data in q_data["options"]:
            option = Option(
                question_id=question.id,
                text=opt_data["text"],
                is_correct=opt_data["is_correct"]
            )
            db.add(option)
        
        db_questions.append(question)
    
    db.commit()
    return db_questions