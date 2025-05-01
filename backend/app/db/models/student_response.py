from sqlalchemy import Column, Integer, ForeignKey, DateTime
from sqlalchemy.orm import relationship
import datetime

from app.db.base_class import Base

class StudentResponse(Base):
    id = Column(Integer, primary_key=True, index=True)
    started_at = Column(DateTime, default=datetime.datetime.utcnow)
    completed_at = Column(DateTime, nullable=True)
    total_score = Column(Integer, default=0)
    
    # İlişkiler
    student_id = Column(Integer, ForeignKey("user.id"))
    student = relationship("User", back_populates="student_responses")
    quiz_id = Column(Integer, ForeignKey("quiz.id"))
    quiz = relationship("Quiz", back_populates="student_responses")
    answers = relationship("StudentAnswer", back_populates="response", cascade="all, delete-orphan")