from sqlalchemy import Column, Integer, String, ForeignKey, Text, Boolean
from sqlalchemy.orm import relationship

from app.db.base_class import Base

class StudentAnswer(Base):
    id = Column(Integer, primary_key=True, index=True)
    selected_option_id = Column(Integer, ForeignKey("option.id"), nullable=True)
    text_answer = Column(Text, nullable=True)  # Kısa cevap soruları için
    is_correct = Column(Boolean, default=False)
    points_earned = Column(Integer, default=0)
    
    # İlişkiler
    response_id = Column(Integer, ForeignKey("studentresponse.id"))
    response = relationship("StudentResponse", back_populates="answers")
    question_id = Column(Integer, ForeignKey("question.id"))
    question = relationship("Question", back_populates="student_answers")