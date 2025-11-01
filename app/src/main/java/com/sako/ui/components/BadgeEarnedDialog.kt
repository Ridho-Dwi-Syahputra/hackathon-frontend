package com.sako.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.sako.ui.theme.SakoAccent
import com.sako.ui.theme.SakoPrimary

/**
 * Badge Earned Dialog Component
 * Responsibility: Display celebration dialog when user earns a badge
 */
@Composable
fun BadgeEarnedDialog(
    badgeName: String,
    badgeDescription: String,
    badgeImageUrl: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Celebration Emoji
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.scale(1.5f)
                )

                // Title
                Text(
                    text = "Badge Baru!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = SakoPrimary,
                    textAlign = TextAlign.Center
                )

                // Badge Image with Glow Effect
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Glow background
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .scale(scale)
                            .background(
                                SakoAccent.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )

                    // Badge Image
                    if (!badgeImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = badgeImageUrl,
                            contentDescription = "Badge $badgeName",
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale * 0.9f),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Default badge icon
                        Surface(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale * 0.9f),
                            shape = CircleShape,
                            color = SakoAccent
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "🏆",
                                    style = MaterialTheme.typography.displayLarge
                                )
                            }
                        }
                    }
                }

                // Badge Name
                Text(
                    text = badgeName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SakoPrimary,
                    textAlign = TextAlign.Center
                )

                // Badge Description
                Text(
                    text = badgeDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.3f)
                )

                // Divider
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                // Celebrate Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakoPrimary
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = "🎊 Lanjutkan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}