package com.sako.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.data.remote.response.VideoItem

@Composable
fun VideoListItem(
    video: VideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            // Thumbnail dengan play icon overlay
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = video.thumbnailUrl ?: "https://img.youtube.com/vi/${extractVideoId(video.youtubeUrl)}/maxresdefault.jpg",
                    contentDescription = "Video thumbnail",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Play icon overlay dengan background semi-transparent
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Play",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Video Info dengan spacing yang lebih baik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = video.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Category badge
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = video.kategori,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun extractVideoId(url: String?): String {
    if (url.isNullOrBlank()) return ""
    return try {
        when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("watch?v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("/embed/") -> url.substringAfter("/embed/").substringBefore("?")
            else -> url.substringAfterLast("/").substringBefore("?")
        }
    } catch (e: Exception) {
        ""
    }
}