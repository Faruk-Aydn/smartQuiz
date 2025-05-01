from sqlalchemy import Column, Integer, String, ForeignKey, Boolean
from sqlalchemy.orm import relationship

from app.db.base_class import Base

class Option(Base):
    id = Column(Integer, primary_key=True, index=True)
    text = Column(String, nullable=False)
    is_correct = Column(Boolean, default=False)
    
    # İlişkiler
    question_id = Column(Integer, ForeignKey("question.id"))
    question = relationship("Question", back_populates="options")