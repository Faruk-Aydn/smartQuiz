package com.farukaydin.quizapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
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
import com.farukaydin.quizapp.ui.quiz.CreateQuizScreen
import com.farukaydin.quizapp.ui.quiz.QuizDetailScreen
import kotlinx.coroutines.launch
import com.farukaydin.quizapp.ui.quiz.JoinQuizScreen
import android.content.Intent
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import androidx.activity.compose.rememberLauncherForActivityResult
import com.farukaydin.quizapp.ui.quiz.SolveQuizScreen
import com.farukaydin.quizapp.data.models.OptionResponse
import com.farukaydin.quizapp.data.api.RetrofitClient
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.farukaydin.quizapp.ui.quiz.TeacherResultsScreen
import com.farukaydin.quizapp.ui.quiz.TeacherHomeViewModel
import com.farukaydin.quizapp.ui.quiz.TeacherDetailedResultsScreen
import com.farukaydin.quizapp.ui.profile.ProfileScreen
import com.farukaydin.quizapp.ui.profile.ProfileViewModel
import com.farukaydin.quizapp.data.repositories.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
            .getString("access_token", null) ?: ""
        setContent {
            val navController = rememberNavController()

            // ZXing QR kod okuma launcher
            val qrScanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
                val scannedText = result.contents
                scannedText?.let {
                    // Ör: akilliquiz://quiz/5
                    val quizId = it.substringAfterLast("/").toIntOrNull()
                    quizId?.let { id ->
                        navController.navigate("quizDetail/$id")
                    }
                }
            }

            NavHost(navController, startDestination = "login") {
                // region Suppress unchecked cast warnings for ViewModelProvider.Factory
                @Suppress("UNCHECKED_CAST")
                composable("login") {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(application) as T
                            }
                        }
                    )
                    LoginScreen(
                        onStudent = { navController.navigate("studentHome") },
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
                            @Suppress("UNCHECKED_CAST")
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
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuizListViewModel(application) as T
                            }
                        }
                    )
                    QuizDetailScreenWithViewModel(
                        quizId = quizId,
                        viewModel = quizListViewModel,
                        navController = navController
                    )
                }
                composable("teacherHome") {
                    val context = LocalContext.current
                    val userRepository = remember { UserRepository(RetrofitClient.apiService, context) }
                    val profileViewModel: ProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ProfileViewModel(userRepository) as T
                        }
                    })
                    val user by profileViewModel.user.collectAsState()
                    LaunchedEffect(Unit) { profileViewModel.loadProfile() }
                    com.farukaydin.quizapp.ui.quiz.TeacherHomeScreen(
                        userName = user?.username ?: "",
                        onProfileClick = { navController.navigate("profile") },
                        onCreateQuiz = { navController.navigate("createQuiz") },
                        onQuizList = { navController.navigate("quizList") },
                        onResults = { navController.navigate("results") },
                        onDetailedResults = { quizId -> navController.navigate("teacherDetailedResults/$quizId") }
                    )
                }
                composable("teacherDetailedResults/{quizId}") { backStackEntry ->
                    val quizId = backStackEntry.arguments?.getString("quizId")?.toIntOrNull()
                    val context = LocalContext.current
                    val quizListViewModel: QuizListViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuizListViewModel(context.applicationContext as Application) as T
                            }
                        }
                    )
                    if (quizId != null) {
                        TeacherDetailedResultsScreen(
    quizId = quizId,
    viewModel = quizListViewModel,
    onBack = { navController.popBackStack() }
)
                    } else {
                        Text("Quiz ID bulunamadı")
                    }
                }
                composable("studentHome") {
                    val context = LocalContext.current
                    val userRepository = remember { UserRepository(RetrofitClient.apiService, context) }
                    val profileViewModel: ProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ProfileViewModel(userRepository) as T
                        }
                    })
                    val user by profileViewModel.user.collectAsState()
                    LaunchedEffect(Unit) { profileViewModel.loadProfile() }
                    StudentHomeScreen(
                        userName = user?.username ?: "",
                        onProfileClick = { navController.navigate("profile") },
                        onJoinQuizClick = { navController.navigate("joinQuiz") }
                    )
                }
                composable("createQuiz") {
                    CreateQuizScreen(
                        onBack = { navController.popBackStack() },
                        onQuizCreated = { navController.popBackStack() }
                    )
                }
                composable("results") {
                    TeacherResultsScreen(
    onQuizClick = { quizId -> navController.navigate("teacherDetailedResults/$quizId") }
)
                }
                composable("joinQuiz") {
                    JoinQuizScreen(
                        onJoinQuiz = { quizId ->
                            navController.navigate("quizDetail/$quizId")
                        },
                        onScanQr = {
                            val options = ScanOptions()
                            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            options.setPrompt("QR kodu okutunuz")
                            qrScanLauncher.launch(options)
                        }
                    )
                }
                composable("profile") {
                    val context = LocalContext.current
                    val userRepository = remember { UserRepository(RetrofitClient.apiService, context) }
                    val profileViewModel: ProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ProfileViewModel(userRepository) as T
                        }
                    })
                    val user by profileViewModel.user.collectAsState()
                    LaunchedEffect(Unit) { profileViewModel.loadProfile() }
                    ProfileScreen(
                        user = user,
                        onSave = { profileViewModel.updateProfile(it) }
                    )
                }
                // endregion
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
fun StudentHomeScreen(
    userName: String,
    onProfileClick: () -> Unit,
    onJoinQuizClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Hoşgeldin $userName",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = onProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text("Profilim", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onJoinQuizClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text("Quiz'e Katıl", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun QuizDetailScreenWithViewModel(quizId: Int?, viewModel: QuizListViewModel, navController: androidx.navigation.NavController) {
    if (quizId == null) {
        Text("Quiz ID geçersiz")
        return
    }
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
            SolveQuizScreen(
                quiz = quizDetailState.quiz,
                questions = quizDetailState.questions,
                apiService = RetrofitClient.apiService,
                onHome = { navController.navigate("joinQuiz") }
            )
        }
        else -> {
            Text("Yükleniyor...")
        }
    }
}