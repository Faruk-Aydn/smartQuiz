# QuizApp ✨📱🧠

Modern bir full-stack quiz uygulaması: yerel Android istemcisi ve FastAPI backend’i.  
Öğretmen ve öğrenci rolleri, güvenli kimlik doğrulama, profil yönetimi ve Jetpack Compose ile tasarlanmış şık bir arayüz sunar.  

> Bu README’yi ekran görüntüleri ve GIF’lerle geliştirebilirsiniz. Bunları `docs/` klasörüne ekleyip **Ekran Görüntüleri** bölümünde gösterebilirsiniz.

---

## 📚 İçindekiler
- [Genel Bakış](#-genel-bakış)
- [Özellikler](#-özellikler)
- [Mimari](#-mimari)
- [Teknoloji Yığını](#️-teknoloji-yığını)
- [Proje Yapısı](#-proje-yapısı)
- [Başlarken](#-başlarken)
  - [Ön Gereksinimler](#-ön-gereksinimler)
  - [Backend Kurulumu (FastAPI)](#-backend-kurulumu-fastapi)
  - [Android Uygulaması Kurulumu](#-android-uygulaması-kurulumu)
- [Ortam Değişkenleri](#-ortam-değişkenleri)
- [API Genel Bakış](#-api-genel-bakış)
- [Ekran Görüntüleri](#-ekran-görüntüleri)
- [Geliştirme Notları](#-geliştirme-notları)
- [Sorun Giderme](#-sorun-giderme)
- [Lisans](#-lisans)

---

## 🧩 Genel Bakış
QuizApp, öğretmenlerin quiz oluşturup yönetmesini; öğrencilerin ise quizlere katılıp çözmesini ve sonuçlarını incelemesini sağlar.  
Android uygulaması modern ve animasyonlu bir UI/UX sunar, offline dostu çalışır ve güvenli navigasyon desenleri kullanır.  
Backend tarafında güvenilir, token tabanlı API’ler bulunur.

---

## ⭐ Özellikler
- ✅ Öğretmen & Öğrenci rolleri  
- 🔐 Güvenli kimlik doğrulama (Bearer token)  
- 👤 Profil görüntüleme/düzenleme (her iki rol için)  
- 📝 Quiz oluşturma, listeleme, katılma  
- ⏱️ Geri sayım sayacı + süre dolunca otomatik gönderim  
- 📊 Sonuç özeti + soru bazlı inceleme  
- 🗂️ Öğrencinin çözdüğü quiz geçmişi (yeniden eskiye)  
- 🎨 Modern arayüz (gradientler, kartlar, ikonlar)  

---

## 🏗️ Mimari
- 🔹 İstemci ve sunucu arasında net ayrım  
- 🔹 Android uygulaması: MVVM + Repository  
- 🔹 Backend: FastAPI + SQLAlchemy + Pydantic  
- 🔹 Bearer token ile güvence altına alınmış stateless API’ler  

```mermaid
flowchart LR
  A[Android Uygulaması (Jetpack Compose)] -->|HTTPS| B[FastAPI Backend]
  B --> C[(DB: SQLAlchemy)]
  A <-.-> D[Yerel Depolama (SharedPreferences)]
🛠️ Teknoloji Yığını
Android: Kotlin, Jetpack Compose, Material 3, Navigation, ViewModel, Coroutines, Retrofit, SharedPreferences

Backend: Python, FastAPI, SQLAlchemy, Pydantic, Uvicorn

Araçlar: Gradle, pip/venv, Git

🗂️ Proje Yapısı
bash
Kodu kopyala
quizapp/
├─ android/          # Android uygulaması (Jetpack Compose)
│  ├─ app/
│  └─ ...
├─ backend/          # FastAPI uygulaması
│  ├─ app/
│  │  ├─ main.py
│  │  ├─ api/
│  │  ├─ models/
│  │  ├─ schemas/
│  │  └─ services/
│  └─ requirements.txt
└─ README.md         # Buradasınız
🚀 Başlarken
✅ Ön Gereksinimler
Android Studio (Giraffe/Flamingo veya üstü)

JDK 17 (Android Gradle Plugin için önerilen)

Python 3.10+

Git

🧪 Backend Kurulumu (FastAPI)
Sanal ortam oluştur ve etkinleştir:

bash
Kodu kopyala
cd backend
python -m venv .venv
# Windows
.venv\Scripts\activate
# macOS/Linux
source .venv/bin/activate
Bağımlılıkları yükle:

bash
Kodu kopyala
pip install -r requirements.txt
Ortam değişkenlerini ayarla (bkz. Ortam Değişkenleri).

Sunucuyu çalıştır:

bash
Kodu kopyala
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
API dokümanları:

Swagger UI: http://localhost:8000/docs

ReDoc: http://localhost:8000/redoc

📱 Android Uygulaması Kurulumu
android/ klasörünü Android Studio ile aç.

Gradle senkronize et ve projeyi derle.

Backend BASE_URL’ini ayarla (constants veya build config). Emulator için:

http://10.0.2.2:8000

Uygulamayı cihazda veya emulator’da çalıştır.

🔐 Ortam Değişkenleri
Backend (örnekler):

DATABASE_URL: SQLAlchemy bağlantı adresi (örn. sqlite:///./quiz.db)

SECRET_KEY: JWT gizli anahtarı

ACCESS_TOKEN_EXPIRE_MINUTES: Token geçerlilik süresi (örn. 60)

CORS_ORIGINS: İzin verilen origin’ler

Android:

BASE_URL: Backend temel URL’i (örn. http://10.0.2.2:8000)

Token’lar SharedPreferences içinde quiz_app_prefs → access_token olarak saklanır.

🔗 API Genel Bakış
Önemli uç noktalar (tam liste için Swagger’a bakın):

Auth

POST /auth/login

POST /auth/register

Profil

GET /users/me

PUT /users/me

Quizler

GET /quizzes (liste)

POST /quizzes (öğretmen)

GET /quizzes/{id} (detay + sorular)

POST /quizzes/{id}/submit (cevap gönder)

Öğrenci

GET /students/me/solved-quizzes (geçmiş)

GET /students/me/results/{quiz_id} (detaylı sonuç)

Tüm korumalı uç noktalar Authorization: Bearer <token> başlığı gerektirir.

🖼️ Ekran Görüntüleri
docs/ klasörüne ekleyip buraya referans verebilirsiniz.

Öğrenci Ana Sayfa

Quiz Katılma

Quiz Çözme / Sayaç

Sonuçlar

🧠 Geliştirme Notları
UI: Compose Material 3 + gradient arka planlar

Navigasyon: NavController + sistem geri desteği

Token: SharedPreferences → quiz_app_prefs içinde access_token

Bazı Compose API’leri deneysel, @OptIn ile kullanıldı

Min SDK: 24+

Tarih parse işlemleri ISO + epoch desteği ile yapıldı

🧯 Sorun Giderme
Bağlantı (Android → Backend):

Emulator için 10.0.2.2 kullan

Sunucunun bind adresini ve CORS ayarlarını kontrol et

Kimlik Doğrulama:

Token eksik/expired → tekrar giriş yap

SharedPreferences içinde token kontrol et

Compose Deneysel API’ler:

@OptIn(ExperimentalMaterial3Api::class) ekle

Tarih Parse:

ISO ve epoch destekli. Backend farklı format kullanıyorsa StudentSolvedQuizListScreen.kt’i güncelle.

📄 Lisans
Bu proje MIT Lisansı ile lisanslanmıştır.
Detaylar için LICENSE dosyasına bakın.
