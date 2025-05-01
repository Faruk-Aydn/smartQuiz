from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any

from app.api import deps
from app.db.models.user import User
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any

from app.api import deps
from app.db.models.user import User
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    # Quiz sıralaması getirme işlemleri
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any

from app.api import deps
from app.db.models.user import User
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    # Quiz sıralaması getirme işlemleri
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Henüz puanınız bulunmamaktadır"
        )
    return rank_info

@router.get("/quiz/{quiz_id}", response_model=List[ScoreResponse])
def get_quiz_leaderboard(
    quiz_id: int,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100
):
    """
    Belirli bir quiz için sıralamayı görüntüleme
    """
    leaderboard = get_top_scores(db, quiz_id=quiz_id, skip=skip, limit=limit)
    return leaderboard
from app.schemas.score import ScoreResponse
from app.crud.score import get_top_scores, get_student_rank

router = APIRouter()

@router.get("/", response_model=List[ScoreResponse])
def get_leaderboard(
    limit: int = 10,
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    scores = get_top_scores(db, limit=limit)
    return scores

@router.get("/my-rank", response_model=ScoreResponse)
def get_my_rank(
    db: Session = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
):
    rank_info = get_student_rank(db, student_id=current_user.id)
    if not rank_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,)