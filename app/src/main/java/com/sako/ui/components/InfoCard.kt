package com.sako.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.ui.theme.SakoCustomShapes
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Info Card - Card untuk menampilkan item konten (kuis, video, lokasi)
 *
 * @param title Judul card
 * @param description Deskripsi card
 * @param imageUrl URL gambar dari backend
 * @param onClick Callback ketika card diklik
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun InfoCard(
    title: String,
    description: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = SakoCustomShapes.categoryCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(SakoCustomShapes.categoryCard),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Konten Text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * SAKO Vertical Info Card - Card vertikal untuk grid layout
 *
 * @param title Judul card
 * @param description Deskripsi card (optional)
 * @param imageUrl URL gambar dari backend
 * @param onClick Callback ketika card diklik
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun VerticalInfoCard(
    title: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = SakoCustomShapes.videoThumbnail
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gambar
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(SakoCustomShapes.videoThumbnail),
                contentScale = ContentScale.Crop
            )

            // Konten Text
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (description != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    SakoTheme {
        InfoCard(
            title = "Kesenian Minangkabau",
            description = "Pelajari tentang kesenian tradisional Minangkabau",
            imageUrl = "https://example.com/image.jpg",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalInfoCardPreview() {
    SakoTheme {
        VerticalInfoCard(
            title = "Tari Piring",
            description = "Video edukasi tentang Tari Piring",
            imageUrl = "https://example.com/image.jpg",
            onClick = {}
        )
    }
}