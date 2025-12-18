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
import com.sako.data.remote.response.QuizSubmitResponse
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
    
    // State untuk instant feedback
    var showAnswer by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }

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
        println("🔍 QuizAttemptScreen - submitState changed: ${submitState?.javaClass?.simpleName}")
        when (val state = submitState) {
            is Resource.Success -> {
                val response = state.data
                val attemptId = response.data.attemptId
                println("✅ QuizAttemptScreen - Submit success, navigating to result")
                println("🚀 attemptId: $attemptId")
                println("🚀 isPassed: ${response.data.isPassed}")
                println("🚀 scorePoints: ${response.data.scorePoints}")
                onNavigateToResult(attemptId)
            }
            is Resource.Error -> {
                println("❌ QuizAttemptScreen - Submit error: ${state.error}")
            }
            is Resource.Loading -> {
                println("⏳ QuizAttemptScreen - Submit loading...")
            }
            null -> {
                // Initial state
            }
        }
    }

    BackgroundImage {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Top Bar dengan back button transparan
            quizAttemptData?.let { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Keluar",
                            tint = SakoPrimary
                        )
                    }
                    Text(
                        text = data.level.name,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary,
                        fontSize = 20.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

        when (quizState) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                quizAttemptData?.let { attemptData ->
                    val currentQuestion = viewModel.getCurrentQuestion()
                    val totalQuestions = attemptData.questions.size

                    Column(
                        modifier = modifier.fillMaxSize()
                    ) {
                        // Modern Score & Timer Header
                        ModernQuizHeader(
                            totalSeconds = attemptData.durationSeconds,
                            onTimeUp = {
                                viewModel.onTimeUp()
                            },
                            isPaused = isTimerPaused,
                            currentQuestion = currentQuestionIndex + 1,
                            totalQuestions = totalQuestions,
                            correctCount = correctCount,
                            wrongCount = wrongCount,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Quiz Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        )
                        {

                            // Question Card
                            currModernQuestionCard(
                                    questionNumber = currentQuestionIndex + 1,
                                    totalQuestions = totalQuestions,
                                    questionText = question.text,
                                    points = question.pointsCorrect
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Options dengan instant feedback
                                question.options.sortedBy { it.displayOrder }.forEach { option ->
                                    ModernQuizOptionCard(
                                        label = option.label,
                                        optionText = option.text,
                                        isSelected = selectedAnswers[question.id] == option.id,
                                        isCorrect = if (showAnswer) option.isCorrect else null,
                                        isRevealed = showAnswer,
                                        onClick = {
                                            if (!showAnswer) {
                                                viewModel.selectAnswer(question.id, option.id)
                                                showAnswer = true
                                                
                                                // Update counter
                                                if (option.isCorrect) {
                                                    correctCount++
                                                } else {
                                                    wrongCount++
                                                }
                                                
                                                // Auto next setelah 1.5 detik
                                                kotlinx.coroutines.GlobalScope.launch {
                                                    kotlinx.coroutines.delay(1500)
                                                    showAnswer = false
                                                    if (currentQuestionIndex < totalQuestions - 1) {
                                                        viewModel.nextQuestion()
                                                    } else {
                                                        // Soal terakhir, submit otomatis
                                                        showSubmitDialog = true
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }

                        // Submit Button (hanya muncul di soal terakhir)
                        if (currentQuestionIndex == totalQuestions - 1 && showAnswer) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Button(
                                    onClick = { showSubmitDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SakoAccent
                                    )
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "🏁 Selesaikan Quiz",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SakoPrimary
                                        )
                                    }
                                }
                            }
                        }
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
    } // Column
    } // BackgroundImage
    
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
// ============================================================================
// MODERN COMPONENTS
// ============================================================================

@Composable
fun ModernQuizHeader(
    totalSeconds: Int,
    onTimeUp: () -> Unit,
    isPaused: Boolean,
    currentQuestion: Int,
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    val progress = remainingSeconds.toFloat() / totalSeconds.toFloat()
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    
    val timerColor = when {
        progress > 0.5f -> Color(0xFF4CAF50)
        progress > 0.25f -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Row: Timer, Progress, Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer Circle
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            color = timerColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⏱️",
                            fontSize = 24.sp
                        )
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = timerColor
                        )
                    }
                }
                
                // Progress
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentQuestion/$totalQuestions",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary
                    )
                    Text(
                        text = "Soal",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // Score Counter
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 18.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$correctCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color(0xFFEF5350).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✗",
                                fontSize = 18.sp,
                                color = Color(0xFFEF5350),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$wrongCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF5350)
                        )
                    }
                }
            }
            
            // Progress Bar
            LinearProgressIndicator(
                progress = currentQuestion.toFloat() / totalQuestions.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = SakoPrimary,
                trackColor = SakoPrimary.copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
fun ModernQuestionCard(
    questionNumber: Int,
    totalQuestions: Int,
    questionText: String,
    points: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question Text
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50),
                lineHeight = MaterialTheme.typography.titleLarge.lineHeight.times(1.3f)
            )
            
            // Points Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SakoAccent.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$points Poin",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SakoPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ModernQuizOptionCard(
    label: String,
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isRevealed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect == true -> Color(0xFF4CAF50)
            isRevealed && isCorrect == false && isSelected -> Color(0xFFEF5350)
            isSelected && !isRevealed -> SakoPrimary.copy(alpha = 0.1f)
            else -> Color.White
        },
        animationSpec = tween(durationMillis = 300),
        label = "containerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect == true -> Color(0xFF4CAF50)
            isRevealed && isCorrect == false && isSelected -> Color(0xFFEF5350)
            isSelected && !isRevealed -> SakoPrimary
            else -> Color(0xFFE0E0E0)
        },
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )
    
    val textColor = when {
        isRevealed && (isCorrect == true || (isCorrect == false && isSelected)) -> Color.White
        else -> Color(0xFF2C3E50)
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        enabled = !isRevealed
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isRevealed || isSelected) 3.dp else 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = when {
                                isRevealed && isCorrect == true -> Color.White.copy(alpha = 0.3f)
                                isRevealed && isCorrect == false && isSelected -> Color.White.copy(alpha = 0.3f)
                                isSelected && !isRevealed -> SakoPrimary
                                else -> SakoAccent.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isRevealed && (isCorrect == true || (isCorrect == false && isSelected)) -> Color.White
                            isSelected && !isRevealed -> Color.White
                            else -> SakoPrimary
                        }
                    )
                }

                // Option Text
                Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected || isRevealed) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                
                // Icon Feedback
                if (isRevealed) {
                    Text(
                        text = if (isCorrect == true) "✓" else if (isSelected) "✗" else "",
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}