package com.sako.ui.screen.kuis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.ui.components.*
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent
import com.sako.utils.Resource
import com.sako.viewmodel.QuizAttemptViewModel

/**
 * Quiz Attempt Screen
 * Halaman untuk mengerjakan quiz dengan timer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAttemptScreen(
    levelId: String,
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizAttemptViewModel,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val isTimerPaused by viewModel.isTimerPaused.collectAsState()
    val quizAttemptData by viewModel.quizAttemptData.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Handle back press -> show exit dialog
    BackHandler(enabled = true) {
        showExitDialog = true
    }

    // Start quiz saat pertama kali
    LaunchedEffect(levelId) {
        viewModel.startQuiz(levelId)
    }

    // Handle submit success -> navigate to result
    LaunchedEffect(submitState) {
        if (submitState is Resource.Success) {
            val attemptId = (submitState as Resource.Success).data.data.attemptId
            println("🚀 QuizAttemptScreen - Navigating to result with attemptId: $attemptId")
            println("🚀 QuizAttemptScreen - Result data: ${(submitState as Resource.Success).data.data}")
            onNavigateToResult(attemptId)
            // Jangan clear submitState di sini agar data masih available di ResultScreen
        }
    }

    BackgroundImage {
        Scaffold(
            topBar = {
                TopAppBar(
                title = {
                    quizAttemptData?.let { data ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .offset(y = (-2).dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = data.level.name,
                                fontWeight = FontWeight.Bold,
                                color = SakoPrimary,
                                fontSize = 20.sp,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Keluar",
                            tint = SakoPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.height(56.dp)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        when (quizState) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                quizAttemptData?.let { attemptData ->
                    val currentQuestion = viewModel.getCurrentQuestion()
                    val totalQuestions = attemptData.questions.size

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Enhanced Timer Card
                        EnhancedTimerCard(
                            totalSeconds = attemptData.durationSeconds,
                            onTimeUp = {
                                viewModel.onTimeUp()
                            },
                            isPaused = isTimerPaused,
                            currentQuestion = currentQuestionIndex + 1,
                            totalQuestions = totalQuestions,
                            answeredCount = viewModel.getAnsweredCount(),
                            modifier = Modifier.padding(16.dp)
                        )

                        // Quiz Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        )
                        {

                            // Question Card
                            currentQuestion?.let { question ->
                                QuizQuestionCard(
                                    questionNumber = currentQuestionIndex + 1,
                                    totalQuestions = totalQuestions,
                                    questionText = question.text,
                                    points = question.pointsCorrect
                                )

                                // Options
                                question.options.sortedBy { it.displayOrder }.forEach { option ->
                                    QuizOptionCard(
                                        label = option.label,
                                        optionText = option.text,
                                        isSelected = selectedAnswers[question.id] == option.id,
                                        isCorrect = null,
                                        isRevealed = false,
                                        onClick = {
                                            viewModel.selectAnswer(question.id, option.id)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Bottom Navigation
                        QuizBottomNav(
                            currentIndex = currentQuestionIndex,
                            totalQuestions = totalQuestions,
                            canGoBack = currentQuestionIndex > 0,
                            canGoNext = currentQuestionIndex < totalQuestions - 1,
                            onPrevious = { viewModel.previousQuestion() },
                            onNext = { viewModel.nextQuestion() },
                            onSubmit = { showSubmitDialog = true },
                            isSubmitting = submitState is Resource.Loading
                        )
                    }
                }
            }
            is Resource.Error -> {
                val errorMessage = (quizState as Resource.Error).error
                ErrorScreen(
                    message = errorMessage,
                    onRetry = { viewModel.startQuiz(levelId) }
                )
            }
        }
    }
    
        // Submit Confirmation Dialog
        if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            icon = {
                Text(text = "📝", style = MaterialTheme.typography.displaySmall)
            },
            title = {
                Text(
                    text = "Selesaikan Quiz?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Kamu telah menjawab ${viewModel.getAnsweredCount()} dari ${quizAttemptData?.questions?.size ?: 0} pertanyaan.")
                    if (viewModel.getUnansweredCount() > 0) {
                        Text(
                            text = "\n${viewModel.getUnansweredCount()} pertanyaan belum dijawab.",
                            color = Color(0xFFEF5350),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakoPrimary
                    )
                ) {
                    Text("Ya, Selesaikan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = {
                Text(text = "⚠️", style = MaterialTheme.typography.displaySmall)
            },
            title = {
                Text(
                    text = "Keluar dari Quiz?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Progress kamu tidak akan tersimpan jika keluar sekarang.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
        }
    }
}

@Composable
fun EnhancedTimerCard(
    totalSeconds: Int,
    onTimeUp: () -> Unit,
    isPaused: Boolean,
    currentQuestion: Int,
    totalQuestions: Int,
    answeredCount: Int,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    val progress = remainingSeconds.toFloat() / totalSeconds.toFloat()
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    
    // Color based on time remaining
    val timerColor = when {
        progress > 0.5f -> Color(0xFF4CAF50) // Green
        progress > 0.25f -> Color(0xFFFFA726) // Orange
        else -> Color(0xFFEF5350) // Red
    }

    LaunchedEffect(key1 = isPaused) {
        if (!isPaused && remainingSeconds > 0) {
            while (remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                if (!isPaused) {
                    remainingSeconds--
                }
            }
            if (remainingSeconds == 0) {
                onTimeUp()
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Timer and Progress Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = timerColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏱️",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    
                    Column {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = timerColor
                        )
                        Text(
                            text = "Waktu Tersisa",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Question Progress
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$currentQuestion/$totalQuestions",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary
                    )
                    Text(
                        text = "Pertanyaan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Linear Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = timerColor,
                    trackColor = timerColor.copy(alpha = 0.2f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$answeredCount Terjawab",
                        fontSize = 11.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${totalQuestions - answeredCount} Tersisa",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun QuizBottomNav(
    currentIndex: Int,
    totalQuestions: Int,
    canGoBack: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress dots indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalQuestions) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(
                                width = if (index == currentIndex) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .background(
                                color = when {
                                    index == currentIndex -> SakoPrimary
                                    index < currentIndex -> SakoPrimary.copy(alpha = 0.5f)
                                    else -> Color.LightGray
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Previous Button
                if (canGoBack) {
                    OutlinedButton(
                        onClick = onPrevious,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SakoPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "←",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sebelumnya",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Next or Submit Button
                Button(
                    onClick = if (canGoNext) onNext else onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(if (canGoBack) 1f else 1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canGoNext) SakoPrimary else SakoAccent,
                        contentColor = if (canGoNext) Color.White else SakoPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isSubmitting) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SakoPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengirim...")
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (canGoNext) "Selanjutnya" else "Selesaikan Kuis",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (canGoNext) "→" else "✓",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}