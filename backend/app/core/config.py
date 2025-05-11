import secrets
from typing import Any, Dict, List, Optional, Union

from pydantic import AnyHttpUrl, PostgresDsn, validator
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    API_V1_STR: str = "/api/v1"
    SECRET_KEY: str = secrets.token_urlsafe(32)
    # 60 dakika * 24 saat * 8 gün = 8 gün
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 8
    # JWT algorithm
    ALGORITHM: str = "HS256"
    # BACKEND_CORS_ORIGINS, liste olarak tanımlanır
    BACKEND_CORS_ORIGINS: List[AnyHttpUrl] = []

    @validator("BACKEND_CORS_ORIGINS", pre=True)
    def assemble_cors_origins(cls, v: Union[str, List[str]]) -> Union[List[str], str]:
        if isinstance(v, str) and not v.startswith("["):
            return [i.strip() for i in v.split(",")]
        elif isinstance(v, (list, str)):
            return v
        raise ValueError(v)

    # Supabase Pooler bağlantı bilgileri
    POSTGRES_SERVER: str = "aws-0-us-east-2.pooler.supabase.com"
    POSTGRES_USER: str = "postgres.umvwlcofbcrqpititqxb"
    POSTGRES_PASSWORD: str = "Ef123456789"  # Kendi Supabase şifreni yaz
    POSTGRES_DB: str = "postgres"
    POSTGRES_PORT: int = 6543
    SQLALCHEMY_DATABASE_URI: Optional[str] = None

    @validator("SQLALCHEMY_DATABASE_URI", pre=True)
    def assemble_db_connection(cls, v: Optional[str], values: Dict[str, Any]) -> Any:
        if isinstance(v, str):
            return v
        username = values.get("POSTGRES_USER")
        password = values.get("POSTGRES_PASSWORD")
        host = values.get("POSTGRES_SERVER")
        db = values.get("POSTGRES_DB")
        port = values.get("POSTGRES_PORT", 6543)
        return f"postgresql://{username}:{password}@{host}:{port}/{db}"

    # AI model ayarları
    AI_PROVIDER: str = "gemini"  # "openai" veya "gemini"
    AI_MODEL_NAME: str = "gemini-2.0-flash-001"  # En güncel ve stabil Gemini modeli
    AI_API_KEY: str = "AIzaSyD-9_jzyyChovlKr6nI9lFVH0vyYwTtVj0"  # Gemini API anahtarınız
    GEMINI_API_URL: str = "https://generativelanguage.googleapis.com/v1/models"  # Güncel Gemini API endpointi
    
    # Frontend ayarları
    FRONTEND_BASE_URL: str = "http://localhost:3000"  # Frontend URL'si

    class Config:
        case_sensitive = True


settings = Settings()