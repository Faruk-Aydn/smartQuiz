from fastapi import APIRouter

from app.api.endpoints import auth, quizzes, student, leaderboard

api_router = APIRouter()
api_router.include_router(auth.router, prefix="/auth", tags=["authentication"])
api_router.include_router(quizzes.router, prefix="/quizzes", tags=["quizzes"])
api_router.include_router(student.router, prefix="/student", tags=["student"])
api_router.include_router(leaderboard.router, prefix="/leaderboard", tags=["leaderboard"])