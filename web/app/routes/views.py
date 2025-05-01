from flask import render_template
from app.routes import main

@main.route('/')
def index():
    return render_template('index.html')

# Remove this duplicate route
# @main.route('/')
# def index():
#     return 'Quiz Uygulamasına Hoş Geldiniz!'