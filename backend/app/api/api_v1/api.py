from fastapi import APIRouter

from app.api.api_v1.endpoints import auth, quizzes, student
from app.api.api_v1.endpoints import user_routes
from app.api.api_v1.endpoints import questions
from app.api.endpoints import quiz_results, student_results

api_router = APIRouter()
api_router.include_router(auth.router, prefix="/auth", tags=["authentication"])
api_router.include_router(user_routes.router, prefix="/users", tags=["users"])
api_router.include_router(quizzes.router, prefix="/quizzes", tags=["quizzes"])
api_router.include_router(questions.router, prefix="/questions", tags=["questions"])
api_router.include_router(student.router, prefix="/student", tags=["student"])
api_router.include_router(student_results.router, prefix="/student", tags=["student_results"])
api_router.include_router(quiz_results.router, prefix="/quizzes", tags=["quiz_results"])