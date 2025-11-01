package com.sako.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.ui.theme.BadgeBronze
import com.sako.ui.theme.BadgeGold
import com.sako.ui.theme.BadgeSilver
import com.sako.ui.theme.SakoCustomShapes
import com.sako.ui.theme.SakoTheme

/**
 * Level Status Enum - Status progres level
 */
enum class LevelStatus {
    LOCKED,      // Level terkunci (prasyarat belum terpenuhi)
    UNSTARTED,   // Level tersedia tapi belum dimulai
    IN_PROGRESS, // Level sedang dikerjakan
    COMPLETED    // Level telah selesai
}

/**
 * SAKO Level Item - Item level untuk grid view
 *
 * @param levelNumber Nomor level
 * @param status Status level (locked/unstarted/in_progress/completed)
 * @param progress Progress level (0.0 - 1.0)
 * @param onClick Callback ketika level diklik
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoLevelItem(
    levelNumber: Int,
    status: LevelStatus,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = status == LevelStatus.LOCKED
    val isCompleted = status == LevelStatus.COMPLETED

    Box(
        modifier = modifier
            .size(80.dp)
            .clip(SakoCustomShapes.levelItem)
            .background(
                if (isLocked) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.primaryContainer
            )
            .border(
                width = 2.dp,
                color = when (status) {
                    LevelStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    LevelStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                    else -> Color.Transparent
                },
                shape = SakoCustomShapes.levelItem
            )
            .clickable(enabled = !isLocked) { onClick() }
            .alpha(if (isLocked) 0.5f else 1f),
        contentAlignment = Alignment.Center
    ) {
        // Circular progress di background
        if (status == LevelStatus.IN_PROGRESS || status == LevelStatus.COMPLETED) {
            SakoCircularProgress(
                progress = progress,
                size = 70.dp,
                strokeWidth = 4.dp
            )
        }

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLocked -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                else -> {
                    Text(
                        text = levelNumber.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Badge Type Enum - Tipe badge berdasarkan pencapaian
 */
enum class BadgeType {
    GOLD,
    SILVER,
    BRONZE,
    DEFAULT
}

/**
 * SAKO Badge Item - Item badge untuk achievement
 *
 * @param title Nama badge
 * @param description Deskripsi badge
 * @param imageUrl URL gambar badge dari backend
 * @param isEarned Apakah badge sudah didapat
 * @param badgeType Tipe badge (gold/silver/bronze)
 * @param onClick Callback ketika badge diklik
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoBadgeItem(
    title: String,
    description: String,
    imageUrl: String,
    isEarned: Boolean,
    badgeType: BadgeType = BadgeType.DEFAULT,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (badgeType) {
        BadgeType.GOLD -> BadgeGold
        BadgeType.SILVER -> BadgeSilver
        BadgeType.BRONZE -> BadgeBronze
        BadgeType.DEFAULT -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEarned) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = SakoCustomShapes.badge,
        border = if (isEarned) BorderStroke(2.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoSpacing.Medium)
                .alpha(if (isEarned) 1f else 0.5f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge Image
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(SakoCustomShapes.badge),
                contentScale = ContentScale.Crop
            )

            HorizontalSpacer(width = SakoSpacing.Medium)

            // Badge Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isEarned) FontWeight.Bold else FontWeight.Normal,
                    color = if (isEarned) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                VerticalSpacer(height = SakoSpacing.ExtraSmall)

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isEarned) {
                    VerticalSpacer(height = SakoSpacing.ExtraSmall)

                    Text(
                        text = "✓ Berhasil Didapat",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * SAKO Quiz Score Card - Kartu untuk menampilkan hasil quiz
 *
 * @param correctCount Jumlah jawaban benar
 * @param wrongCount Jumlah jawaban salah
 * @param unansweredCount Jumlah soal tidak dijawab
 * @param totalQuestions Total soal
 * @param score Skor yang didapat
 * @param xpGained XP yang diperoleh
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoQuizScoreCard(
    correctCount: Int,
    wrongCount: Int,
    unansweredCount: Int,
    totalQuestions: Int,
    score: Int,
    xpGained: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = SakoCustomShapes.quizCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoSpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Percentage
            val percentage = (correctCount.toFloat() / totalQuestions * 100).toInt()

            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            VerticalSpacer(height = SakoSpacing.Small)

            Text(
                text = "Skor: $score poin",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            VerticalSpacer(height = SakoSpacing.Large)

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuizStatItem(
                    label = "Benar",
                    value = correctCount.toString(),
                    color = MaterialTheme.colorScheme.primary
                )

                QuizStatItem(
                    label = "Salah",
                    value = wrongCount.toString(),
                    color = MaterialTheme.colorScheme.error
                )

                QuizStatItem(
                    label = "Kosong",
                    value = unansweredCount.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            VerticalSpacer(height = SakoSpacing.Large)

            // XP Gained
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+$xpGained XP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun QuizStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoLevelItemPreview() {
    SakoTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SakoLevelItem(
                levelNumber = 1,
                status = LevelStatus.COMPLETED,
                progress = 1f,
                onClick = {}
            )

            SakoLevelItem(
                levelNumber = 2,
                status = LevelStatus.IN_PROGRESS,
                progress = 0.6f,
                onClick = {}
            )

            SakoLevelItem(
                levelNumber = 3,
                status = LevelStatus.LOCKED,
                progress = 0f,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoBadgeItemPreview() {
    SakoTheme {
        SakoBadgeItem(
            title = "Pemula Minangkabau",
            description = "Selesaikan 10 kuis pertama",
            imageUrl = "https://example.com/badge.png",
            isEarned = true,
            badgeType = BadgeType.GOLD,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoQuizScoreCardPreview() {
    SakoTheme {
        SakoQuizScoreCard(
            correctCount = 8,
            wrongCount = 2,
            unansweredCount = 0,
            totalQuestions = 10,
            score = 80,
            xpGained = 50
        )
    }
}