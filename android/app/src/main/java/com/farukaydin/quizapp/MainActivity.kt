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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.farukaydin.quizapp.ui.quiz.TeacherResultsScreen
import com.farukaydin.quizapp.ui.quiz.TeacherHomeViewModel
import com.farukaydin.quizapp.ui.quiz.TeacherDetailedResultsScreen
import com.farukaydin.quizapp.ui.profile.ProfileScreen
import com.farukaydin.quizapp.ui.profile.ProfileViewModel
import com.farukaydin.quizapp.data.repositories.UserRepository
import com.farukaydin.quizapp.ui.quiz.StudentHomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
            .getString("access_token", null) ?: ""
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
            val savedToken = sharedPrefs.getString("access_token", null)
            val savedRole = sharedPrefs.getString("user_role", null)

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

            // Otomatik yönlendirme
            LaunchedEffect(savedToken, savedRole) {
                if (!savedToken.isNullOrEmpty() && !savedRole.isNullOrEmpty()) {
                    if (savedRole == "student") {
                        navController.navigate("studentHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (savedRole == "teacher") {
                        navController.navigate("teacherHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }

            val startDest = if (!savedToken.isNullOrEmpty() && !savedRole.isNullOrEmpty()) {
                if (savedRole == "student") "studentHome"
                else if (savedRole == "teacher") "teacherHome"
                else "login"
            } else {
                "login"
            }

            NavHost(navController, startDestination = startDest) {
                // Öğrenci quiz çözüp sonucu gördükten sonra ana sayfaya dönebilsin diye SolveQuizScreen için route ekleniyor.
                composable("solveQuiz/{quizId}") { backStackEntry ->
                    val quizId = backStackEntry.arguments?.getString("quizId")?.toIntOrNull()
                    val quizListViewModel: QuizListViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuizListViewModel(application) as T
                            }
                        }
                    )
                    if (quizId != null) {
                        QuizDetailScreenWithViewModel(quizId = quizId, viewModel = quizListViewModel, navController = navController)
                    } else {
                        Text("Quiz ID geçersiz")
                    }
                }
                composable("login") {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(application) as T
                            }
                        }
                    )
                    // Eğer token varsa, login ekranını göstermeden ana ekrana yönlendir
                    val context = LocalContext.current
                    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                    val savedToken = sharedPrefs.getString("access_token", null)
                    val savedRole = sharedPrefs.getString("user_role", null)
                    LaunchedEffect(savedToken, savedRole) {
                        if (!savedToken.isNullOrEmpty() && !savedRole.isNullOrEmpty()) {
                            if (savedRole == "student") {
                                navController.navigate("studentHome") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else if (savedRole == "teacher") {
                                navController.navigate("teacherHome") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                    // Eğer token yoksa login ekranı göster
                    if (savedToken.isNullOrEmpty() || savedRole.isNullOrEmpty()) {
                        LoginScreen(
                            onStudent = { navController.navigate("studentHome") },
                            onTeacher = { navController.navigate("teacherHome") },
                            onRegisterClick = { navController.navigate("register") },
                            viewModel = loginViewModel
                        )
                    }
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
                        onDetailedResults = { quizId -> navController.navigate("teacherDetailedResults/$quizId") },
                        onLogoutClick = {
                            val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                            sharedPrefs.edit().clear().apply()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
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
                        onJoinQuizClick = { navController.navigate("joinQuiz") },
                        onResultsClick = { navController.navigate("studentResults") },
                        onLogoutClick = {
                            val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                            sharedPrefs.edit().clear().apply()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
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
                composable("studentResults") {
                    val context = LocalContext.current
                    val repository = remember { com.farukaydin.quizapp.data.repositories.QuizRepository(com.farukaydin.quizapp.data.api.RetrofitClient.apiService) }
                    val accessToken = context.getSharedPreferences("quiz_app_prefs", android.content.Context.MODE_PRIVATE).getString("access_token", null) ?: ""
                    val viewModel: com.farukaydin.quizapp.ui.StudentResultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.farukaydin.quizapp.ui.StudentResultViewModelFactory(repository)
                    )
                    val solvedQuizzes by viewModel.solvedQuizzes
                    val selectedQuizId by viewModel.selectedQuizId
                    val quizResultDetail by viewModel.quizResultDetail
                    val isLoading by viewModel.isLoading

                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        viewModel.fetchSolvedQuizzes(accessToken)
                    }

                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator()
                    } else if (selectedQuizId == null) {
                        com.farukaydin.quizapp.ui.StudentSolvedQuizListScreen(
                            solvedQuizzes = solvedQuizzes,
                            onQuizSelected = { quiz ->
                                viewModel.selectQuizAndFetchResults(quiz.quiz_id, accessToken)
                            }
                        )
                    } else {
                        com.farukaydin.quizapp.ui.StudentResultScreen(quizResultDetail = quizResultDetail)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentHomeScreen(
    userName: String?,
    onProfileClick: () -> Unit,
    onJoinQuizClick: () -> Unit,
    onResultsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Profil ikonu ve kullanıcı adı
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profil",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userName ?: "Kullanıcı",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onProfileClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Profilim")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onJoinQuizClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Quiz'e Katıl")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onResultsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Sonuçlarım")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Çıkış Yap", color = MaterialTheme.colorScheme.onError)
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
                onHome = {
                    navController.navigate("studentHome") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        else -> {
            Text("Yükleniyor...")
        }
    }
}
