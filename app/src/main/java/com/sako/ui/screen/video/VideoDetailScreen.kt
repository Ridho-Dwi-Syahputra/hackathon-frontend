package com.sako.ui.screen.video

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.sako.data.remote.response.VideoItem
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.VideoListItem
import com.sako.ui.theme.VideoFavoriteActive
import com.sako.ui.theme.VideoFavoriteInactive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: String,
    onNavigateBack: () -> Unit = {},
    onToggleFavorite: (String) -> Unit = {},
    onNavigateToVideo: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    video: VideoItem? = null,
    videos: State<List<VideoItem>> = remember { mutableStateOf(emptyList()) }
) {
    BackgroundImage {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        // Favorite button
                        IconButton(
                            onClick = {
                                video?.id?.let { onToggleFavorite(it) }
                            }
                        ) {
                            Icon(
                                imageVector = if (video?.isFavorited == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (video?.isFavorited == true) "Remove from favorites" else "Add to favorites",
                                tint = if (video?.isFavorited == true) VideoFavoriteActive else VideoFavoriteInactive
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (video == null) {
                    // Loading state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Memuat video...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // YouTube Player Section
                            val lifecycleOwner = LocalLifecycleOwner.current
                            
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f/9f),
                                factory = { context ->
                                    YouTubePlayerView(context).apply {
                                        layoutParams = FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                        
                                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                                val videoId = video.youtubeUrl?.let { url ->
                                                    url.substringAfterLast("/").takeIf { it.isNotBlank() }
                                                        ?: url.substringAfterLast("v=").takeIf { it.isNotBlank() }
                                                } ?: return
                                                
                                                youTubePlayer.loadVideo(videoId, 0f)
                                            }
                                        })
                                    }
                                }
                            )
                            
                            DisposableEffect(lifecycleOwner) {
                                onDispose {
                                    // Clean up YouTube player resources
                                }
                            }

                            // Video Info Section
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = video.judul,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = video.kategori,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (!video.deskripsi.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = video.deskripsi,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Justify
                                    )
                                }
                            }

                            // Divider dengan padding vertical
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Related Videos Header
                            Text(
                                text = "Video Lainnya",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Related Videos List
                        items(
                            items = videos.value.filter { it.id != video.id },
                            key = { it.id }
                        ) { relatedVideo ->
                            VideoListItem(
                                video = relatedVideo,
                                onClick = { onNavigateToVideo(relatedVideo.id) }
                            )
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun sampleVideo() = VideoItem(
    id = "v1",
    judul = "Pengenalan SAKO: Sejarah dan Tujuan",
    kategori = "Pengenalan Sako",
    youtubeUrl = "https://youtu.be/dummy1",
    thumbnailUrl = null,
    deskripsi = """
        SAKO (Suaka Alam Konservasi) merupakan area konservasi yang memiliki peran penting dalam pelestarian keanekaragaman hayati Indonesia. 
        
        Video ini memberikan pengenalan mendasar tentang sejarah pembentukan SAKO, tujuan konservasi, dan perannya dalam menjaga keseimbangan ekosistem lokal.
    """.trimIndent(),
    isActive = true,
    isFavorited = false,
    createdAt = "2025-01-01")