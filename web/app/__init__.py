from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager
from config import Config

db = SQLAlchemy()
login_manager = LoginManager()

from app.routes.quiz import quiz as quiz_blueprint
from app.routes.auth import auth as auth_blueprint  # Import the blueprint object

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    db.init_app(app)
    login_manager.init_app(app)
    login_manager.login_view = 'auth.login'  # Update login view to use auth blueprint

    from app.routes import main  # Only import main module
    app.register_blueprint(main)
    app.register_blueprint(quiz_blueprint)
    app.register_blueprint(auth_blueprint, url_prefix='/auth')  # Register the blueprint object

    with app.app_context():
        db.create_all()

    return app
from app.routes.auth import auth as auth_blueprint  # Ensure this import is correct