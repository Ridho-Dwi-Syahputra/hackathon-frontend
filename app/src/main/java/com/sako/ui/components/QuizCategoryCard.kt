package com.sako.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent

/**
 * Quiz Category Card Component
 * Responsibility: Display quiz category with progress and image
 */
@Composable
fun QuizCategoryCard(
    categoryName: String,
    categoryDescription: String,
    imageUrl: String?,
    progressPercentage: Float,
    completedLevels: Int,
    totalLevels: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Image
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(
                        width = 2.dp,
                        color = SakoAccent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Category $categoryName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder icon
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SakoPrimary
                    )
                }
            }

            // Category Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title and Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = categoryDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Progress Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Progress Text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$completedLevels/$totalLevels Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        
                        // Progress Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SakoAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${(progressPercentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = SakoPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Progress Bar
                    SakoProgressBar(
                        progress = progressPercentage,
                        modifier = Modifier.fillMaxWidth(),
                        progressColor = SakoPrimary
                    )
                }
            }
        }
    }
}