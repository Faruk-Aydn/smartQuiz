from flask import render_template, redirect, url_for, flash, request
from flask_login import login_required
from app.routes import main
from app.models.quiz import Quiz
from app import db
from flask import Blueprint

quiz = Blueprint('quiz', __name__)

@quiz.route('/quizzes')
def quizzes():
    quizzes = Quiz.query.all()
    return render_template('quiz/quizzes.html', quizzes=quizzes)

@quiz.route('/quiz/<int:quiz_id>')
@login_required
def take_quiz(quiz_id):
    quiz = Quiz.query.get_or_404(quiz_id)
    return render_template('quiz/take_quiz.html', quiz=quiz)

@quiz.route('/create-quiz', methods=['GET', 'POST'])
@login_required
def create_quiz():
    if request.method == 'POST':
        title = request.form.get('title')
        description = request.form.get('description')
        
        quiz = Quiz(title=title, description=description)
        db.session.add(quiz)
        db.session.commit()
        
        flash('Quiz başarıyla oluşturuldu!')
        return redirect(url_for('main.index'))
        
    return render_template('quiz/create_quiz.html')

@quiz.route('/quiz/<int:quiz_id>/results')
@login_required
def quiz_results(quiz_id):
    quiz = Quiz.query.get_or_404(quiz_id)
    return render_template('quiz/quiz_results.html', quiz=quiz)