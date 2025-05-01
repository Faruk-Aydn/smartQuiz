import requests
import json
from typing import List, Dict, Any, Optional
from app.core.config import settings
from app.db.models.question import QuestionType

class GeminiService:
    def __init__(self, api_key: str = settings.AI_API_KEY):
        self.api_key = api_key
        self.base_url = settings.GEMINI_API_URL
        self.model = settings.AI_MODEL_NAME
    
    def generate_quiz_questions(self, topic: str, difficulty: str, num_questions: int = 5) -> List[Dict[str, Any]]:
        """
        Gemini API kullanarak quiz soruları oluşturur
        
        Args:
            topic: Konu başlığı
            difficulty: Zorluk seviyesi (easy, medium, hard)
            num_questions: Oluşturulacak soru sayısı
            
        Returns:
            Soru listesi
        """
        prompt = f"""
        Lütfen aşağıdaki konuda {num_questions} adet {difficulty} zorluk seviyesinde quiz sorusu oluştur:
        
        Konu: {topic}
        
        Her soru için 4 seçenek oluştur ve doğru cevabı belirt.
        Yanıtı aşağıdaki JSON formatında döndür:
        
        [
          {{
            "question": "Soru metni",
            "options": [
              {{ "text": "Seçenek 1", "is_correct": false }},
              {{ "text": "Seçenek 2", "is_correct": true }},
              {{ "text": "Seçenek 3", "is_correct": false }},
              {{ "text": "Seçenek 4", "is_correct": false }}
            ],
            "explanation": "Doğru cevabın açıklaması"
          }}
        ]
        
        Sadece JSON çıktısını döndür, başka açıklama ekleme.
        """
        
        response = self._call_gemini_api(prompt)
        
        # Gemini'den gelen yanıtı işle
        if response:
            try:
                # Gemini bazen JSON'ı metin içinde döndürebilir, bu yüzden JSON kısmını çıkarmamız gerekebilir
                json_str = self._extract_json_from_text(response)
                questions = json.loads(json_str)
                return questions
            except Exception as e:
                print(f"JSON ayrıştırma hatası: {e}")
                return []
        
        return []
    
    def _call_gemini_api(self, prompt: str) -> Optional[str]:
        """
        Gemini API'sine istek gönderir
        
        Args:
            prompt: Gönderilecek metin
            
        Returns:
            API yanıtı
        """
        url = f"{self.base_url}/{self.model}:generateContent?key={self.api_key}"
        
        payload = {
            "contents": [
                {
                    "parts": [
                        {
                            "text": prompt
                        }
                    ]
                }
            ],
            "generationConfig": {
                "temperature": 0.7,
                "topK": 40,
                "topP": 0.95,
                "maxOutputTokens": 2048
            }
        }
        
        headers = {
            "Content-Type": "application/json"
        }
        
        try:
            response = requests.post(url, headers=headers, json=payload)
            response.raise_for_status()
            
            data = response.json()
            
            # Gemini API yanıt yapısını kontrol et
            if "candidates" in data and len(data["candidates"]) > 0:
                if "content" in data["candidates"][0] and "parts" in data["candidates"][0]["content"]:
                    parts = data["candidates"][0]["content"]["parts"]
                    if parts and "text" in parts[0]:
                        return parts[0]["text"]
            
            return None
        except Exception as e:
            print(f"Gemini API hatası: {e}")
            return None
    
    def _extract_json_from_text(self, text: str) -> str:
        """
        Metin içindeki JSON kısmını çıkarır
        
        Args:
            text: JSON içeren metin
            
        Returns:
            JSON string
        """
        # Basit bir yaklaşım: İlk '[' ve son ']' arasındaki metni al
        start_idx = text.find('[')
        end_idx = text.rfind(']') + 1
        
        if start_idx != -1 and end_idx != 0:
            return text[start_idx:end_idx]
        
        # Alternatif olarak, ilk '{' ve son '}' arasındaki metni al
        start_idx = text.find('{')
        end_idx = text.rfind('}') + 1
        
        if start_idx != -1 and end_idx != 0:
            return text[start_idx:end_idx]
        
        # Hiçbir JSON bulunamazsa orijinal metni döndür
        return text

def get_ai_service():
    return GeminiService()