package com.sako.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent

/**
 * Quiz Option Card Component
 * Responsibility: Display answer option (A/B/C/D) with selection state
 */
@Composable
fun QuizOptionCard(
    label: String,
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isRevealed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect == true -> Color(0xFF66BB6A).copy(alpha = 0.2f)
            isRevealed && isCorrect == false && isSelected -> Color(0xFFEF5350).copy(alpha = 0.2f)
            isSelected -> SakoAccent.copy(alpha = 0.3f)
            else -> Color(0xFFF4F4F4)
        },
        animationSpec = tween(durationMillis = 300),
        label = "containerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect == true -> Color(0xFF66BB6A)
            isRevealed && isCorrect == false && isSelected -> Color(0xFFEF5350)
            isSelected -> SakoPrimary
            else -> SakoAccent
        },
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(28.8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        enabled = enabled && !isRevealed
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected || (isRevealed && isCorrect == true)) 3.dp else 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option Label (A/B/C/D)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isRevealed && isCorrect == true -> Color(0xFF66BB6A)
                        isRevealed && isCorrect == false && isSelected -> Color(0xFFEF5350)
                        isSelected -> SakoPrimary
                        else -> SakoAccent
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = if (isSelected || (isRevealed && (isCorrect == true || (isCorrect == false && isSelected)))) {
                                Color.White
                            } else {
                                SakoPrimary
                            }
                        )
                    }
                }

                // Option Text
                Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )

                // Checkmark or X icon when revealed
                if (isRevealed) {
                    when {
                        isCorrect == true -> {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF66BB6A)
                            )
                        }
                        isCorrect == false && isSelected -> {
                            Text(
                                text = "✗",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF5350)
                            )
                        }
                    }
                }
            }
        }
    }
}