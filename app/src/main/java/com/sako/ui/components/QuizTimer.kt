package com.sako.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent
import kotlinx.coroutines.delay

/**
 * Quiz Timer Component
 * Responsibility: Display countdown timer with warning states
 */
@Composable
fun QuizTimer(
    totalSeconds: Int,
    onTimeUp: () -> Unit,
    modifier: Modifier = Modifier,
    isPaused: Boolean = false
) {
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    
    // Timer color based on remaining time
    val timerColor by animateColorAsState(
        targetValue = when {
            remainingSeconds <= 10 -> Color(0xFFEF5350) // Red
            remainingSeconds <= 30 -> Color(0xFFFFA726) // Orange
            else -> SakoPrimary
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerColor"
    )

    // Blinking animation for last 10 seconds
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (remainingSeconds <= 10) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Timer countdown logic
    LaunchedEffect(isPaused, remainingSeconds) {
        if (!isPaused && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
            if (remainingSeconds == 0) {
                onTimeUp()
            }
        }
    }

    // Format time as MM:SS
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = timerColor.copy(alpha = 0.15f * alpha),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Timer",
                tint = timerColor,
                modifier = Modifier.size(28.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = timerColor
                )

                if (remainingSeconds <= 30) {
                    Text(
                        text = when {
                            remainingSeconds <= 10 -> "Waktu Hampir Habis!"
                            else -> "Tersisa $remainingSeconds detik"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = timerColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Progress indicator
    LinearProgressIndicator(
        progress = { remainingSeconds.toFloat() / totalSeconds.toFloat() },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = timerColor,
        trackColor = Color.Gray.copy(alpha = 0.2f)
    )
}