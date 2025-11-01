package com.sako.ui.screen.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.VideoListItem
import com.sako.viewmodel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFavoriteScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: VideoViewModel
) {
    val favoriteVideos by viewModel.favoriteVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    BackgroundImage {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Video Favorit") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (favoriteVideos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Belum ada video favorit",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tekan ikon hati di halaman detail video untuk menambahkan ke favorit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = favoriteVideos,
                            key = { it.id }
                        ) { video ->
                            VideoListItem(
                                video = video,
                                onClick = { onNavigateToDetail(video.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}