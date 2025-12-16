package com.sako.ui.screen.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sako.data.remote.response.VideoCollectionWithFlag
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.VideoListItemWithCollection
import com.sako.viewmodel.VideoViewModel
import com.sako.viewmodel.VideoCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFavoriteScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCollections: () -> Unit,
    viewModel: VideoViewModel,
    collectionViewModel: VideoCollectionViewModel
) {
    val favoriteVideos by viewModel.favoriteVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val collectionsForVideo by collectionViewModel.collectionsForVideo.collectAsState()
    val isLoadingCollections by collectionViewModel.isLoading.collectAsState()
    
    var selectedVideoId by remember { mutableStateOf<String?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    // Load collections when bottom sheet is shown
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet && selectedVideoId != null) {
            collectionViewModel.loadCollectionsForVideo(selectedVideoId!!)
        }
    }

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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToCollections,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Koleksi",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "No favorites",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada video favorit",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tekan ikon hati di halaman detail video\nuntuk menambahkan ke favorit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                            VideoListItemWithCollection(
                                video = video,
                                onClick = { onNavigateToDetail(video.id) },
                                onAddToCollection = {
                                    selectedVideoId = video.id
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                
                // Bottom Sheet untuk Add to Collection
                if (showBottomSheet && selectedVideoId != null) {
                    AddToCollectionBottomSheet(
                        videoId = selectedVideoId!!,
                        collections = collectionsForVideo,
                        isLoading = isLoadingCollections,
                        onDismiss = { 
                            showBottomSheet = false
                            selectedVideoId = null
                        },
                        onAddToCollection = { collectionId: String ->
                            collectionViewModel.addVideoToCollection(collectionId, selectedVideoId!!)
                            // Reload collections untuk update status centang
                            collectionViewModel.loadCollectionsForVideo(selectedVideoId!!)
                        },
                        onRemoveFromCollection = { collectionId: String ->
                            collectionViewModel.removeVideoFromCollection(collectionId, selectedVideoId!!)
                            // Reload collections untuk update status centang
                            collectionViewModel.loadCollectionsForVideo(selectedVideoId!!)
                        },
                        onCreateNewCollection = {
                            showCreateDialog = true
                        }
                    )
                }
                
                // Dialog untuk Create Collection
                if (showCreateDialog) {
                    CreateCollectionDialog(
                        onDismiss = { showCreateDialog = false },
                        onCreate = { name: String, description: String? ->
                            collectionViewModel.createCollection(name, description)
                            showCreateDialog = false
                            // Reload collections after creating
                            if (selectedVideoId != null) {
                                collectionViewModel.loadCollectionsForVideo(selectedVideoId!!)
                            }
                        }
                    )
                }
            }
        }
    }
}