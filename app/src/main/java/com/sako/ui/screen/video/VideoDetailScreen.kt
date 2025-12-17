package com.sako.ui.screen.video

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    // Debug: Log video state changes
    androidx.compose.runtime.LaunchedEffect(video?.isFavorited) {
        println("VideoDetailScreen - 🎬 Video state changed:")
        println("  - Video ID: ${video?.id}")
        println("  - Title: ${video?.judul}")
        println("  - Favorite Status: ${video?.isFavorited} (1=favorited, 0=not)")
        println("  - Icon will be: ${if (video?.isFavorited == 1) "Filled Heart ❤️" else "Empty Heart 🤍"}")
    }
    
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
                        val isFavorited = video?.isFavorited == 1
                        IconButton(
                            onClick = {
                                println("VideoDetailScreen - 👆 Favorite button clicked!")
                                println("VideoDetailScreen - Video ID: ${video?.id}")
                                println("VideoDetailScreen - Current favorite state: ${video?.isFavorited}")
                                video?.id?.let { id ->
                                    println("VideoDetailScreen - 🚀 Calling onToggleFavorite with ID: $id")
                                    onToggleFavorite(id)
                                }
                            },
                            enabled = video != null
                        ) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorited) VideoFavoriteActive else VideoFavoriteInactive
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
                            // YouTube Player dengan WebView (Custom Implementation)
                            val lifecycleOwner = LocalLifecycleOwner.current
                            val context = LocalContext.current
                            
                            // Extract video ID
                            val youtubeVideoId = remember(video.youtubeUrl) {
                                println("VideoDetailScreen - YouTube URL: ${video.youtubeUrl}")
                                val videoId = extractYouTubeVideoId(video.youtubeUrl)
                                println("VideoDetailScreen - Extracted Video ID: $videoId")
                                videoId
                            }
                            
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Video Player Area
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f/9f)
                                ) {
                                    if (youtubeVideoId != null) {
                                        AndroidView(
                                            modifier = Modifier.fillMaxSize(),
                                            factory = { context ->
                                                WebView(context).apply {
                                                    layoutParams = FrameLayout.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                    
                                                    // WebView settings 
                                                    settings.apply {
                                                        javaScriptEnabled = true
                                                        domStorageEnabled = true
                                                        mediaPlaybackRequiresUserGesture = false
                                                    }
                                                    
                                                    // WebChromeClient untuk fullscreen
                                                    webChromeClient = WebChromeClient()
                                                    
                                                    // PENTING: WebViewClient mencegah redirect ke YouTube app
                                                    webViewClient = object : WebViewClient() {
                                                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                                            return false // Jangan redirect, tetap di WebView
                                                        }
                                                    }
                                                    
                                                    // Iframe string untuk embed video di WebView
                                                    val video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$youtubeVideoId\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
                                                    
                                                    println("VideoDetailScreen - Loading video: $youtubeVideoId")
                                                    
                                                    loadData(video, "text/html", "utf-8")
                                                }
                                            },
                                            update = { webView ->
                                                // Optional
                                            }
                                        )
                                    } else {
                                        // Error message jika video ID tidak valid
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "URL video tidak valid",
                                                style = MaterialTheme.typography.bodyLarge,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    
                                    // Lifecycle management untuk WebView
                                    DisposableEffect(lifecycleOwner) {
                                        val observer = LifecycleEventObserver { _, event ->
                                            // Handle lifecycle events if needed
                                        }
                                        lifecycleOwner.lifecycle.addObserver(observer)
                                        
                                        onDispose {
                                            lifecycleOwner.lifecycle.removeObserver(observer)
                                        }
                                    }
                                }
                                
                                // Tombol "Tetap Tonton" selalu ditampilkan di bawah video
                                if (youtubeVideoId != null) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.youtubeUrl))
                                            intent.setPackage("com.google.android.youtube")
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                intent.setPackage(null)
                                                context.startActivity(intent)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.padding(4.dp))
                                        Text("Tetap Tonton di YouTube")
                                    }
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
                            
                            // Debug info
                            androidx.compose.runtime.LaunchedEffect(videos.value) {
                                println("VideoDetailScreen - 🎬 VIDEOS DEBUG:")
                                println("  - Total videos: ${videos.value.size}")
                                println("  - Current video ID: ${video.id}")
                                println("  - All video IDs: ${videos.value.map { it.id }}")
                                val filtered = videos.value.filter { it.id != video.id }
                                println("  - Filtered (excluding current): ${filtered.size}")
                                filtered.forEach {
                                    println("    • ${it.judul} (${it.id})")
                                }
                            }
                        }

                        // Related Videos List
                        val relatedVideos = videos.value.filter { it.id != video.id }
                        
                        // Show message if no related videos
                        if (relatedVideos.isEmpty()) {
                            item {
                                Text(
                                    text = "Belum ada video lainnya",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                        
                        items(
                            items = relatedVideos,
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

/**
 * Extract YouTube video ID from various URL formats:
 * - https://www.youtube.com/watch?v=VIDEO_ID
 * - https://youtu.be/VIDEO_ID
 * - https://youtu.be/VIDEO_ID?si=TRACKING
 * - https://www.youtube.com/embed/VIDEO_ID
 */
private fun extractYouTubeVideoId(url: String?): String? {
    if (url.isNullOrBlank()) return null
    
    return try {
        when {
            // Format: youtu.be/VIDEO_ID or youtu.be/VIDEO_ID?params
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/")
                    .substringBefore("?") // Remove query parameters
                    .substringBefore("&")
                    .takeIf { it.isNotBlank() }
            }
            // Format: youtube.com/watch?v=VIDEO_ID
            url.contains("watch?v=") -> {
                url.substringAfter("v=")
                    .substringBefore("&")
                    .takeIf { it.isNotBlank() }
            }
            // Format: youtube.com/embed/VIDEO_ID
            url.contains("/embed/") -> {
                url.substringAfter("/embed/")
                    .substringBefore("?")
                    .takeIf { it.isNotBlank() }
            }
            // Fallback: assume last path segment is video ID
            else -> {
                url.substringAfterLast("/")
                    .substringBefore("?")
                    .takeIf { it.isNotBlank() }
            }
        }
    } catch (e: Exception) {
        null
    }
}

private fun sampleVideo() = VideoItem(
    id = "v1",
    judul = "App SAKO: Mengenal Budaya Minang",
    kategori = "Kesenian",
    youtubeUrl = "https://youtu.be/dummy1",
    thumbnailUrl = null,
    deskripsi = """
        App SAKO bertujuan untuk mengenalkan kebudayaan Minangkabau: adat, tradisi, kesenian,
        serta nilai-nilai lokal yang menjadi bagian dari identitas masyarakat Minang.

        Konten ini adalah contoh (sample) yang ditampilkan saat data real belum tersedia dari backend.
    """.trimIndent(),
    isActive = 1,
    isFavorited = 0,
    createdAt = "2025-01-01")