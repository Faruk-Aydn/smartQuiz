from sqlalchemy import Column, Integer, String, Enum, Boolean
from sqlalchemy.orm import relationship
import enum

from app.db.base_class import Base

class UserRole(str, enum.Enum):
    ADMIN = "admin"
    TEACHER = "teacher"
    STUDENT = "student"

class User(Base):
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True, nullable=False)
    username = Column(String, unique=True, index=True, nullable=False)
    full_name = Column(String, nullable=True)
    first_name = Column(String, nullable=True)
    last_name = Column(String, nullable=True)
    hashed_password = Column(String, nullable=False)
    is_active = Column(Boolean, default=True)
    role = Column(Enum(UserRole), default=UserRole.STUDENT)
    
    # İlişkiler
    quizzes = relationship("Quiz", back_populates="teacher")
    student_responses = relationship("StudentResponse", back_populates="student")
    scores = relationship("Score", back_populates="student")