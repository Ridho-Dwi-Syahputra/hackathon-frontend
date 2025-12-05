package com.sako.ui.screen.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.R
import com.sako.data.remote.response.VideoItem
import com.sako.ui.components.BackgroundImage
import com.sako.ui.theme.SakoTheme

/**
 * VideoCollectionDetailScreen - Menampilkan detail koleksi dan video-videonya
 * Layout:
 * - Top bar dengan back button, nama koleksi, delete button
 * - Deskripsi koleksi (jika ada)
 * - List video di koleksi ini (mirip VideoListScreen tapi tanpa search)
 * - Setiap video bisa di-remove dari koleksi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCollectionDetailScreen(
    modifier: Modifier = Modifier,
    collectionName: String,
    collectionDescription: String?,
    videoCount: Int,
    videos: List<VideoItem>,
    isLoading: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToVideoDetail: (String) -> Unit = {},
    onRemoveVideo: (String) -> Unit = {},
    onDeleteCollection: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf<String?>(null) }

    SakoTheme {
        BackgroundImage {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = collectionName,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$videoCount video",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Kembali"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Koleksi",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f)
            ) { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Description
                    if (!collectionDescription.isNullOrEmpty()) {
                        Text(
                            text = collectionDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Loading
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    // Empty
                    else if (videos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.video),
                                    contentDescription = "Empty",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Belum ada video di koleksi ini",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    // Video list
                    else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(videos) { video ->
                                CollectionVideoCard(
                                    video = video,
                                    onClick = { onNavigateToVideoDetail(video.id) },
                                    onRemove = { showRemoveDialog = video.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Collection Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Koleksi?") },
            text = { Text("Koleksi akan dihapus, tapi video tetap ada di favorit.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteCollection()
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Remove Video Dialog
    showRemoveDialog?.let { videoId ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text("Hapus dari Koleksi?") },
            text = { Text("Video akan dihapus dari koleksi ini, tapi tetap ada di favorit.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = null
                        onRemoveVideo(videoId)
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CollectionVideoCard(
    video: VideoItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Thumbnail (40% width)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
            ) {
                if (video.thumbnailUrl != null) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.judul,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.video),
                        contentDescription = video.judul,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Info (50% width)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = video.judul,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.kategori,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Remove button (10% width)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.1f),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
