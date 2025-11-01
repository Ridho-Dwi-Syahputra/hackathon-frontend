package com.sako.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent

/**
 * Quiz Level Card Component
 * Responsibility: Display level with lock state, progress, and badge
 */
@Composable
fun QuizLevelCard(
    levelNumber: Int,
    levelName: String,
    levelDescription: String,
    status: LevelStatus,
    progressPercentage: Float,
    bestScore: Int?,
    baseXp: Int,
    timeLimit: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = status == LevelStatus.LOCKED
    val containerColor = when (status) {
        LevelStatus.LOCKED -> Color(0xFFE0E0E0)
        LevelStatus.UNSTARTED -> Color(0xFFF4F4F4)
        LevelStatus.IN_PROGRESS -> Color(0xFFFFF8E1)
        LevelStatus.COMPLETED -> Color(0xFFE8F5E9)
    }
    
    Card(
        onClick = { if (!isLocked) onClick() },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLocked) 0.dp else 2.dp,
            pressedElevation = if (isLocked) 0.dp else 4.dp
        ),
        enabled = !isLocked
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Number Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLocked) Color.Gray else SakoAccent
                    )
                    .border(
                        width = 3.dp,
                        color = if (isLocked) Color.DarkGray else SakoPrimary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(40.dp),
                        tint = Color.DarkGray
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = SakoPrimary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = levelNumber.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SakoPrimary
                        )
                    }
                }
            }

            // Level Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = levelName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) Color.Gray else SakoPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                Text(
                    text = levelDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isLocked) Color.Gray else Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Limit
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "⏱ ${timeLimit / 60}m",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isLocked) Color.Gray else Color.DarkGray
                        )
                    }

                    // XP Reward
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "⭐ ${baseXp} XP",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isLocked) Color.Gray else SakoPrimary
                        )
                    }
                }

                // Progress or Best Score
                if (!isLocked && status != LevelStatus.UNSTARTED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (status == LevelStatus.COMPLETED && bestScore != null) {
                            Text(
                                text = "Best: ${(progressPercentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SakoPrimary
                            )
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (status) {
                                LevelStatus.IN_PROGRESS -> Color(0xFFFFA726)
                                LevelStatus.COMPLETED -> Color(0xFF66BB6A)
                                else -> Color.Transparent
                            }
                        ) {
                            Text(
                                text = when (status) {
                                    LevelStatus.IN_PROGRESS -> "In Progress"
                                    LevelStatus.COMPLETED -> "✓ Completed"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}