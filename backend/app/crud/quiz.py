from sqlalchemy.orm import Session, joinedload
from typing import List, Optional
import qrcode
import io
import base64
from datetime import datetime

from app.db.models.quiz import Quiz
from app.schemas.quiz import QuizCreate, QuizUpdate

def create_quiz(db: Session, obj_in: QuizCreate, teacher_id: int) -> Quiz:
    db_obj = Quiz(
        title=obj_in.title,
        description=obj_in.description,
        subject=obj_in.subject,
        grade_level=obj_in.grade_level,
        teacher_id=teacher_id
    )
    db.add(db_obj)
    db.commit()
    db.refresh(db_obj)
    
    # QR kodu oluştur ve kaydet
    qr_code_url = generate_qr_code(db_obj.id)
    db_obj.qr_code = qr_code_url
    db.add(db_obj)
    db.commit()
    db.refresh(db_obj)
    
    return db_obj

def get_quiz(db: Session, quiz_id: int) -> Optional[Quiz]:
    return db.query(Quiz).options(joinedload(Quiz.questions)).filter(Quiz.id == quiz_id).first()

def get_quizzes_by_teacher(db: Session, teacher_id: int) -> List[Quiz]:
    return db.query(Quiz).filter(Quiz.teacher_id == teacher_id).all()

def update_quiz(db: Session, quiz_id: int, obj_in: QuizUpdate) -> Optional[Quiz]:
    db_obj = get_quiz(db, quiz_id)
    if not db_obj:
        return None
    
    update_data = obj_in.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(db_obj, field, value)
    
    db.add(db_obj)
    db.commit()
    db.refresh(db_obj)
    return db_obj

def generate_qr_code(quiz_id: int) -> str:
    """Quiz ID'si için QR kod oluşturur ve base64 formatında döndürür"""
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    
    # Mobil uygulamanın açacağı URL veya deep link
    data = f"akilliquiz://quiz/{quiz_id}"
    qr.add_data(data)
    qr.make(fit=True)
    
    img = qr.make_image(fill_color="black", back_color="white")
    
    # Resmi base64'e çevir
    buffered = io.BytesIO()
    img.save(buffered)
    img_str = base64.b64encode(buffered.getvalue()).decode()
    
    return f"data:image/png;base64,{img_str}"

def get_quizzes(db: Session, teacher_id: Optional[int] = None, skip: int = 0, limit: int = 100):
    query = db.query(Quiz)
    if teacher_id is not None:
        query = query.filter(Quiz.teacher_id == teacher_id)
    return query.offset(skip).limit(limit).all()