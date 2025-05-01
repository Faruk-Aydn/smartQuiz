import qrcode
import os
from io import BytesIO
import base64
from app.core.config import settings
from sqlalchemy.orm import Session
from app.db.models.quiz import Quiz

def generate_qr_code(quiz_id: int, base_url: str = settings.FRONTEND_BASE_URL) -> str:
    """
    Quiz için QR kod oluşturma ve kaydetme
    
    Args:
        quiz_id: Quiz ID
        base_url: Mobil uygulamanın quiz'e erişeceği temel URL
        
    Returns:
        QR kod resmi için base64 kodlanmış string
    """
    # QR kod içeriği: mobil uygulamanın anlayacağı bir URL veya deep link
    qr_content = f"{base_url}/quiz/{quiz_id}"
    
    # QR kod oluşturma
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(qr_content)
    qr.make(fit=True)
    
    img = qr.make_image(fill_color="black", back_color="white")
    
    # QR kodu base64'e dönüştürme
    buffered = BytesIO()
    img.save(buffered)
    img_str = base64.b64encode(buffered.getvalue()).decode()
    
    return f"data:image/png;base64,{img_str}"

def update_quiz_qr_code(db: Session, quiz: Quiz) -> Quiz:
    """
    Quiz için QR kod oluşturur ve veritabanında günceller
    """
    qr_code_data = generate_qr_code(quiz.id)
    quiz.qr_code = qr_code_data
    db.add(quiz)
    db.commit()
    db.refresh(quiz)
    return quiz