from sqlalchemy import Column, Integer, String, ForeignKey, Text, Boolean
from sqlalchemy.orm import relationship

from app.db.base_class import Base

class Answer(Base):
    id = Column(Integer, primary_key=True, index=True)
    question_id = Column(Integer, ForeignKey("question.id"))
    text = Column(Text, nullable=False)
    is_correct = Column(Boolean, default=False)
    
    # İlişkiler
    question = relationship("Question", back_populates="answers")