package com.sako.ui.screen.kuis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
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
 * Quiz Result Screen
 * Menampilkan hasil quiz dan badge yang didapat
 */
@Composable
fun QuizResultScreen(
    attemptId: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCategoryList: () -> Unit,
    viewModel: QuizAttemptViewModel,
    modifier: Modifier = Modifier
) {
    val submitState by viewModel.submitState.collectAsState()
    var showBadgeDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Get quiz result
    val quizResult = viewModel.getQuizResult()

    // Show badges earned dialog
    LaunchedEffect(quizResult) {
        quizResult?.badgesEarned?.firstOrNull()?.let { badge ->
            showBadgeDialog = Pair(badge.name, badge.description ?: "")
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF4F4F4)
    ) {
        when {
            submitState is Resource.Loading -> {
                LoadingScreen()
            }
            quizResult != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Result Icon & Status
                    val isPassed = quizResult.isPassed
                    Text(
                        text = if (isPassed) "🎉" else "😢",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = if (isPassed) "Selamat!" else "Belum Berhasil",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isPassed) {
                            "Kamu berhasil menyelesaikan quiz ini!"
                        } else {
                            "Jangan menyerah, coba lagi!"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    // Score Card
                    ScoreCard(
                        scorePoints = quizResult.scorePoints,
                        percentCorrect = quizResult.percentCorrect,
                        isPassed = isPassed
                    )

                    // Statistics Card
                    StatisticsCard(
                        correctCount = quizResult.correctCount,
                        wrongCount = quizResult.wrongCount,
                        unansweredCount = quizResult.unansweredCount
                    )

                    // Rewards Card
                    RewardsCard(
                        xpEarned = quizResult.xpEarned,
                        pointsEarned = quizResult.pointsEarned,
                        badgesCount = quizResult.badgesEarned?.size ?: 0
                    )

                    // Action Buttons
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCategoryList,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SakoPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            Text(
                                text = "📚 Pilih Level Lain",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToHome,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SakoPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            Text(
                                text = "🏠 Kembali ke Home",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            else -> {
                ErrorScreen(
                    message = "Gagal memuat hasil quiz",
                    onRetry = { /* No retry available */ }
                )
            }
        }
    }

    // Badge Earned Dialog
    showBadgeDialog?.let { (name, description) ->
        BadgeEarnedDialog(
            badgeName = name,
            badgeDescription = description,
            badgeImageUrl = null,
            onDismiss = {
                showBadgeDialog = null
            }
        )
    }
}

@Composable
private fun ScoreCard(
    scorePoints: Int,
    percentCorrect: Double,
    isPassed: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPassed) SakoPrimary else Color(0xFFEF5350)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Skor Kamu",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.9f)
            )

            Text(
                text = "$scorePoints",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "${percentCorrect.toInt()}% Benar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Divider(
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = if (isPassed) "Lulus" else "Belum Lulus",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    correctCount: Int,
    wrongCount: Int,
    unansweredCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Statistik Jawaban",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SakoPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "✓",
                    label = "Benar",
                    value = correctCount.toString(),
                    color = Color(0xFF66BB6A)
                )
                StatItem(
                    icon = "✗",
                    label = "Salah",
                    value = wrongCount.toString(),
                    color = Color(0xFFEF5350)
                )
                StatItem(
                    icon = "⊝",
                    label = "Kosong",
                    value = unansweredCount.toString(),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun RewardsCard(
    xpEarned: Int,
    pointsEarned: Int,
    badgesCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SakoAccent.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎁 Reward yang Didapat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SakoPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardItem(
                    icon = "⭐",
                    label = "XP",
                    value = "+$xpEarned"
                )
                RewardItem(
                    icon = "💎",
                    label = "Points",
                    value = "+$pointsEarned"
                )
                if (badgesCount > 0) {
                    RewardItem(
                        icon = "🏆",
                        label = "Badge",
                        value = "+$badgesCount"
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardItem(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SakoPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}