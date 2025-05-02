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
import com.farukaydin.quizapp.ui.quiz.CreateQuizScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.farukaydin.quizapp.ui.quiz.QuizDetailScreen
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
                    QuizListScreen(viewModel = quizListViewModel)
                }
                composable("quizDetail/{quizId}") { backStackEntry ->
                    val quizId = backStackEntry.arguments?.getString("quizId")?.toIntOrNull()
                    val quizListViewModel: QuizListViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuizListViewModel(application) as T
                            }
                        }
                    )
                    QuizDetailScreenWithViewModel(quizId = quizId, viewModel = quizListViewModel)
                }
                composable("teacherHome") {
                    TeacherHomeScreen(
                        onCreateQuiz = { navController.navigate("createQuiz") },
                        onQuizList = { navController.navigate("quizList") },
                        onResults = { navController.navigate("results") }
                    )
                }
                composable("createQuiz") {
                    CreateQuizScreen(
                        onBack = { navController.popBackStack() },
                        onQuizCreated = { navController.popBackStack() }
                    )
                }
                composable("results") {
                    HomeScreen()
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
fun TeacherHomeScreen(
    onCreateQuiz: () -> Unit,
    onQuizList: () -> Unit,
    onResults: () -> Unit
) {
    Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hoşgeldiniz, Öğretmen!", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
        Button(onClick = onCreateQuiz) {
            Text("Quiz Oluştur")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(onClick = onQuizList) {
            Text("Quizlerim")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(onClick = onResults) {
            Text("Sonuçlar")
        }
    }
}

@Composable
fun QuizDetailScreenWithViewModel(quizId: Int?, viewModel: QuizListViewModel) {
    if (quizId == null) {
        Text("Quiz ID geçersiz")
        return
    }
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(quizId) {
        viewModel.fetchQuizDetail(quizId)
    }
    
    val quizDetailState = viewModel.quizDetailState.collectAsState().value
    when {
        quizDetailState.isLoading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        quizDetailState.error != null -> {
            Text(text = quizDetailState.error, color = MaterialTheme.colorScheme.error)
        }
        quizDetailState.quiz != null -> {
            QuizDetailScreen(
                quiz = quizDetailState.quiz,
                questions = quizDetailState.questions,
                onAddQuestion = { text, options, correctOption ->
                    coroutineScope.launch {
                        viewModel.addQuestionToQuiz(quizDetailState.quiz.id, text, options, correctOption)
                    }
                }
            )
        }
        else -> {
            Text("Yükleniyor...")
        }
    }
}