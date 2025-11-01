package com.sako.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent

/**
 * Quiz Question Card Component
 * Responsibility: Display question with number indicator
 */
@Composable
fun QuizQuestionCard(
    questionNumber: Int,
    totalQuestions: Int,
    questionText: String,
    points: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Number Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Question Number Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SakoAccent
                ) {
                    Text(
                        text = "Soal $questionNumber/$totalQuestions",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Points Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SakoPrimary
                ) {
                    Text(
                        text = "⭐ $points pts",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Divider
            HorizontalDivider(
                thickness = 2.dp,
                color = SakoAccent.copy(alpha = 0.3f)
            )

            // Question Text
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Start,
                lineHeight = MaterialTheme.typography.titleLarge.lineHeight.times(1.2f)
            )
        }
    }
}