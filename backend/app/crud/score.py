from typing import List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import desc, func
from app.db.models.score import Score
from app.schemas.score import ScoreCreate

def get_top_scores(
    db: Session,
    quiz_id: int,
    skip: int = 0,
    limit: int = 100
) -> List[Score]:
    """
    Get top scores for a specific quiz
    """
    return db.query(Score)\
        .filter(Score.quiz_id == quiz_id)\
        .order_by(desc(Score.score))\
        .offset(skip)\
        .limit(limit)\
        .all()

def get_student_rank(
    db: Session,
    student_id: int,
) -> Optional[Score]:
    """
    Get a student's highest score and rank
    """
    return db.query(Score)\
        .filter(Score.student_id == student_id)\
        .order_by(desc(Score.score))\
        .first()

def create_score(
    db: Session,
    score: ScoreCreate,
    student_id: int,
    quiz_id: int
) -> Score:
    """
    Create a new score record
    """
    db_score = Score(
        score=score.score,
        student_id=student_id,
        quiz_id=quiz_id
    )
    db.add(db_score)
    db.commit()
    db.refresh(db_score)
    return db_score