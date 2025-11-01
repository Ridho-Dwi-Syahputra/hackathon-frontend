package com.sako.ui.screen.kuis

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

    // Start quiz saat pertama kali
    LaunchedEffect(levelId) {
        viewModel.startQuiz(levelId)
    }

    // Handle submit success -> navigate to result
    LaunchedEffect(submitState) {
        if (submitState is Resource.Success) {
            val attemptId = (submitState as Resource.Success).data.data.attemptId
            onNavigateToResult(attemptId)
            viewModel.clearSubmitState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    quizAttemptData?.let { data ->
                        Text(
                            text = data.level.name,
                            fontWeight = FontWeight.Bold,
                            color = SakoPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
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
                )
            )
        },
        containerColor = Color(0xFFF4F4F4)
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
                        // Timer
                        QuizTimer(
                            totalSeconds = attemptData.durationSeconds,
                            onTimeUp = {
                                viewModel.onTimeUp()
                            },
                            isPaused = isTimerPaused,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Quiz Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Progress Indicator
                            QuizProgressIndicator(
                                currentIndex = currentQuestionIndex,
                                totalQuestions = totalQuestions,
                                answeredCount = viewModel.getAnsweredCount()
                            )

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

@Composable
private fun QuizProgressIndicator(
    currentIndex: Int,
    totalQuestions: Int,
    answeredCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pertanyaan ${currentIndex + 1}/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SakoPrimary
                )
                Text(
                    text = "$answeredCount dijawab",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            LinearProgressIndicator(
                progress = (currentIndex + 1).toFloat() / totalQuestions.toFloat(),
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp),
                color = SakoPrimary,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun QuizBottomNav(
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Previous Button
            OutlinedButton(
                onClick = onPrevious,
                enabled = canGoBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SakoPrimary
                )
            ) {
                Text("← Sebelumnya")
            }

            // Next or Submit Button
            if (canGoNext) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakoPrimary
                    )
                ) {
                    Text("Selanjutnya →")
                }
            } else {
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakoAccent,
                        contentColor = SakoPrimary
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SakoPrimary
                        )
                    } else {
                        Text("✓ Selesai", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}