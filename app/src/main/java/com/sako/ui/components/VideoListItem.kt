package com.sako.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            // Thumbnail
            AsyncImage(
                model = video.thumbnailUrl ?: "https://img.youtube.com/vi/${extractVideoId(video.youtubeUrl)}/0.jpg",
                contentDescription = "Video thumbnail",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            // Video Info
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = video.judul,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = video.kategori,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun extractVideoId(url: String?): String {
    if (url == null) return ""
    return url.substringAfterLast("/").takeIf { it.isNotBlank() }
        ?: url.substringAfterLast("v=").takeIf { it.isNotBlank() }
        ?: ""
}