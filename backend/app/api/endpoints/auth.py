from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api import deps
from app.core.security import create_access_token
from app.db.models.user import User
from app.schemas.user import UserCreate, UserResponse
from app.crud.user import create_user, get_user_by_email

router = APIRouter()

@router.post("/register", response_model=UserResponse)
def register_user(user_in: UserCreate, db: Session = Depends(deps.get_db)):
    user = get_user_by_email(db, email=user_in.email)
    if user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Bu e-posta adresi zaten kullanılıyor."
        )
    user = create_user(db, obj_in=user_in)
    return user