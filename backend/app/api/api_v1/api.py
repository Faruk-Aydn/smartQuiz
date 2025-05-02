from fastapi import APIRouter

from app.api.api_v1.endpoints import auth, quizzes
from app.api.api_v1.endpoints import user_routes

api_router = APIRouter()
api_router.include_router(auth.router, prefix="/auth", tags=["authentication"])
api_router.include_router(user_routes.router, prefix="/users", tags=["users"])
api_router.include_router(quizzes.router, prefix="/quizzes", tags=["quizzes"])