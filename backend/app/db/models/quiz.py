from sqlalchemy import Column, Integer, String, ForeignKey, DateTime, Boolean
from sqlalchemy.orm import relationship
import datetime

from app.db.base_class import Base

class Quiz(Base):
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    description = Column(String, nullable=True)
    subject = Column(String, index=True)
    grade_level = Column(String, index=True)
    created_at = Column(DateTime, default=datetime.datetime.utcnow)
    is_active = Column(Boolean, default=True)
    qr_code = Column(String, nullable=True)  # QR kod URL'si veya değeri
    duration_minutes = Column(Integer, nullable=True)  # Quiz süresi (dakika cinsinden)
    
    # İlişkiler
    teacher_id = Column(Integer, ForeignKey("user.id"))
    teacher = relationship("User", back_populates="quizzes")
    questions = relationship("Question", back_populates="quiz", cascade="all, delete-orphan", lazy="joined")
    student_responses = relationship("StudentResponse", back_populates="quiz")