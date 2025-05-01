from sqlalchemy.orm import Session
from typing import List
import random

from app.db.models.quiz import Quiz
from app.db.models.question import Question, QuestionType
from app.db.models.option import Option

def generate_questions(db: Session, quiz: Quiz, count: int = 5) -> List[Question]:
    """
    Yapay zeka ile quiz için sorular oluşturur.
    Bu örnek fonksiyon, gerçek bir AI entegrasyonu olmadan basit sorular üretir.
    Gerçek uygulamada, OpenAI API veya başka bir AI servisi kullanılabilir.
    """
    questions = []
    
    # Örnek soru tipleri ve şablonları
    question_templates = [
        "{}  konusunda en önemli kavram nedir?",
        "{} ile ilgili aşağıdakilerden hangisi doğrudur?",
        "{} konusunda hangi ifade yanlıştır?",
        "{} hakkında ne söylenebilir?",
        "{} ile ilgili temel prensip nedir?"
    ]
    
    for i in range(count):
        # Rastgele bir soru tipi seç
        q_type = random.choice([QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE])
        
        # Soru metnini oluştur
        template = random.choice(question_templates)
        question_text = template.format(quiz.subject)
        
        # Yeni soru oluştur
        question = Question(
            text=question_text,
            question_type=q_type,
            points=random.randint(1, 5),
            quiz_id=quiz.id
        )
        
        db.add(question)
        db.flush()  # ID ataması için
        
        # Seçenekleri oluştur
        if q_type == QuestionType.MULTIPLE_CHOICE:
            # Doğru cevap
            correct_option = Option(
                text=f"Doğru cevap {i+1}",
                is_correct=True,
                question_id=question.id
            )
            db.add(correct_option)
            
            # Yanlış cevaplar
            for j in range(3):
                wrong_option = Option(
                    text=f"Yanlış cevap {i+1}-{j+1}",
                    is_correct=False,
                    question_id=question.id
                )
                db.add(wrong_option)
        
        elif q_type == QuestionType.TRUE_FALSE:
            # Rastgele doğru/yanlış belirle
            is_true = random.choice([True, False])
            
            true_option = Option(
                text="Doğru",
                is_correct=is_true,
                question_id=question.id
            )
            db.add(true_option)
            
            false_option = Option(
                text="Yanlış",
                is_correct=not is_true,
                question_id=question.id
            )
            db.add(false_option)
        
        questions.append(question)
    
    db.commit()
    
    # Seçenekleriyle birlikte soruları yeniden yükle
    for q in questions:
        db.refresh(q)
    
    return questions