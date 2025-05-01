from sqlalchemy import Column, Integer, ForeignKey, DateTime
from sqlalchemy.orm import relationship
import datetime

from app.db.base_class import Base

class Score(Base):
    id = Column(Integer, primary_key=True, index=True)
    score = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.datetime.utcnow)
    
    # İlişkiler
    student_id = Column(Integer, ForeignKey("user.id"))
    student = relationship("User")
    quiz_id = Column(Integer, ForeignKey("quiz.id"))
    quiz = relationship("Quiz")