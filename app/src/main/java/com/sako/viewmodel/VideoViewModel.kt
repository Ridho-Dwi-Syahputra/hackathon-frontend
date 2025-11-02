package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.repository.SakoRepository
import com.sako.data.remote.response.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(private val repository: SakoRepository) : ViewModel() {
    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()

    private val _selectedVideo = MutableStateFlow<VideoItem?>(null)
    val selectedVideo: StateFlow<VideoItem?> = _selectedVideo.asStateFlow()

    private val _favoriteVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val favoriteVideos: StateFlow<List<VideoItem>> = _favoriteVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadVideos()
        loadFavoriteVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Temporary sample data until repository is implemented
                _videos.value = listOf(
                    VideoItem(
                        id = "1",
                        judul = "App SAKO: Mengenal Budaya Minang",
                        kategori = "Kesenian",
                        youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                        thumbnailUrl = null,
                        deskripsi = "Perkenalan aplikasi SAKO yang bertujuan mengenalkan budaya Minangkabau, adat, dan tradisi lokal.",
                        isActive = true,
                        isFavorited = false,
                        createdAt = "2025-01-01"
                    ),
                    VideoItem(
                        id = "2",
                        judul = "Kebudayaan Minang: Tari, Musik, dan Tradisi",
                        kategori = "Kesenian",
                        youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                        thumbnailUrl = null,
                        deskripsi = "Mengenal berbagai aspek kebudayaan Minangkabau: tarian tradisional, musik, dan nilai-nilai masyarakat.",
                        isActive = true,
                        isFavorited = false,
                        createdAt = "2025-01-02"
                    ),
                    VideoItem(
                        id = "3",
                        judul = "Wisata Alam Danau Maninjau",
                        kategori = "Wisata",
                        youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                        thumbnailUrl = null,
                        deskripsi = "Mengenal lokasi wisata di Danau Maninjau, Sumatera Barat.",
                        isActive = true,
                        isFavorited = false,
                        createdAt = "2025-01-02"
                    )
                )
                // TODO: Implement actual API call when ready
                // _videos.value = repository.getVideos()
            } catch (e: Exception) {
                // TODO: Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFavoriteVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // For now, filter from videos list
                _favoriteVideos.value = _videos.value.filter { it.isFavorited }
            } catch (e: Exception) {
                // TODO: Handle error properly
                println("Error loading favorites: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedVideo(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First try to find in existing videos
                val existingVideo = _videos.value.find { it.id == videoId }
                if (existingVideo != null) {
                    _selectedVideo.value = existingVideo
                } else {
                    // If not found, we could fetch it from repository
                    // TODO: Implement when repository is ready
                    // _selectedVideo.value = repository.getVideoById(videoId)

                    // For now, create a sample video if not found
                    _selectedVideo.value = VideoItem(
                        id = videoId,
                        judul = "App SAKO: Mengenal Budaya Minang",
                        kategori = "Kesenian",
                        youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                        thumbnailUrl = null,
                        deskripsi = """
                            App SAKO ini bertujuan untuk mengenalkan kebudayaan Minangkabau, termasuk adat,
                            tradisi, kesenian, dan nilai-nilai yang diwariskan secara turun-temurun.

                            Konten ini adalah data sample sementara sampai integrasi API backend siap.
                        """.trimIndent(),
                        isActive = true,
                        isFavorited = false,
                        createdAt = "2025-01-01"
                    )
                }
            } catch (e: Exception) {
                // TODO: Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleVideoFavorite(videoId: String) {
        viewModelScope.launch {
            try {
                // Update in all videos list
                val currentVideos = _videos.value.toMutableList()
                val videoIndex = currentVideos.indexOfFirst { it.id == videoId }

                if (videoIndex != -1) {
                    val video = currentVideos[videoIndex]
                    val updatedVideo = video.copy(isFavorited = !video.isFavorited)
                    currentVideos[videoIndex] = updatedVideo
                    _videos.value = currentVideos

                    // Update selected video if it's the same
                    if (_selectedVideo.value?.id == videoId) {
                        _selectedVideo.value = updatedVideo
                    }

                    // Update favorite videos list immediately
                    _favoriteVideos.value = currentVideos.filter { it.isFavorited }

                    // TODO: Update in repository when ready
                    // repository.toggleVideoFavorite(videoId)
                    saveFavoriteVideos()
                }
            } catch (e: Exception) {
                // TODO: Handle error properly
                println("Error toggling favorite: ${e.message}")
            }
        }
    }

    private fun saveFavoriteVideos() {
        viewModelScope.launch {
            try {
                // TODO: Save to local storage using DataStore or SharedPreferences
                // For now, we'll just keep it in memory
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}