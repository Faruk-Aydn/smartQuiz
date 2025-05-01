package com.farukaydin.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.farukaydin.quizapp.ui.auth.LoginScreen
import com.farukaydin.quizapp.ui.auth.RegisterScreen
import com.farukaydin.quizapp.ui.quiz.QuizListScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farukaydin.quizapp.ui.auth.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "login") {
                composable("login") {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(application) as T
                            }
                        }
                    )
                    LoginScreen(
                        onStudent = { navController.navigate("quizList") },
                        onTeacher = { navController.navigate("teacherHome") },
                        onRegisterClick = { navController.navigate("register") },
                        viewModel = loginViewModel
                    )
                }
                composable("register") {
                    RegisterScreen(onRegisterSuccess = { navController.popBackStack("login", inclusive = false) })
                }
                composable("quizList") {
                    val quizListViewModel: QuizListViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuizListViewModel(application) as T
                            }
                        }
                    )
                    QuizListScreen(
                        onQuizClick = { quizId -> navController.navigate("quizDetail/$quizId") },
                        viewModel = quizListViewModel
                    )
                }
                composable("quizDetail/{quizId}") { backStackEntry ->
                    val quizId = backStackEntry.arguments?.getString("quizId")
                    QuizDetailScreen(quizId = quizId)
                }
                composable("teacherHome") {
                    TeacherHomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text("Hoşgeldin!")
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun QuizDetailScreen(quizId: String?) {
    Text("Quiz Detay Ekranı - Quiz ID: $quizId")
}

@Composable
fun TeacherHomeScreen() {
    Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hoşgeldiniz, Öğretmen!", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
        Button(onClick = { /* Quiz oluşturma ekranına git */ }) {
            Text("Quiz Oluştur")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(onClick = { /* Quizlerim ekranına git */ }) {
            Text("Quizlerim")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(onClick = { /* Sonuçlar ekranına git */ }) {
            Text("Sonuçlar")
        }
    }
}