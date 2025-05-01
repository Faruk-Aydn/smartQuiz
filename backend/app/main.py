from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.api import api_router
from app.core.config import settings

app = FastAPI(
    title="Akıllı Quiz API",
    description="Öğretmenler için AI destekli quiz oluşturma ve öğrenciler için quiz çözme API'si",
    version="1.0.0",
)

# CORS ayarları
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Güvenlik için gerçek uygulamada sınırlandırılmalı
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router, prefix=settings.API_V1_STR)

@app.get("/")
def root():
    return {"message": "Akıllı Quiz API'ye Hoş Geldiniz! /docs adresinden API dokümantasyonuna ulaşabilirsiniz."}