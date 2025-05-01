from app.core.config import settings
from passlib.context import CryptContext
from datetime import datetime, timedelta
from jose import JWTError, jwt
from typing import Optional  # Bu satırı ekleyin


# Şifreleme için bir bağlam oluşturuyoruz
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """
    Kullanıcının girdiği şifreyi, veritabanında saklanan hash ile karşılaştırır.
    """
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password: str) -> str:
    """
    Şifreyi hashleyerek saklanabilir hale getirir.
    """
    return pwd_context.hash(password)

# JWT ayarları
ALGORITHM = "HS256"

def create_access_token(subject: str, expires_delta: Optional[timedelta] = None) -> str:
    """
    JWT access token oluşturur.
    """
    to_encode = {"sub": str(subject)}
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, settings.SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt