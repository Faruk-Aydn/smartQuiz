package com.farukaydin.quizapp.ui.about

import androidx.compose.runtime.Composable

// Delegates to the styled AboutScreen in ui.settings to avoid duplication
@Composable
fun AboutScreen(onBack: () -> Unit) {
    com.farukaydin.quizapp.ui.settings.AboutScreen(onBack = onBack)
}
