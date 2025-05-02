from fastapi import APIRouter

from app.api.endpoints import quizzes, student, leaderboard
from app.api.api_v1.api import api_router as api_v1_router

api_router = APIRouter()
api_router.include_router(quizzes.router, prefix="/quizzes", tags=["quizzes"])
api_router.include_router(student.router, prefix="/student", tags=["student"])
api_router.include_router(leaderboard.router, prefix="/leaderboard", tags=["leaderboard"])
api_router.include_router(api_v1_router, prefix="/api/v1")