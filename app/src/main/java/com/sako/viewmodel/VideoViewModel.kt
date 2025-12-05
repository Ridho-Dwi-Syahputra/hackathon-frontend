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
        println("VideoViewModel - INIT CALLED")
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("VideoViewModel - Starting to load videos from API")
                // Fetch videos from API
                repository.getVideos().collect { resource ->
                    println("VideoViewModel - Resource received: ${resource.javaClass.simpleName}")
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            val videoList = resource.data?.data?.videos ?: emptyList()
                            println("VideoViewModel - Success! Videos count: ${videoList.size}")
                            videoList.forEach { v ->
                                println("VideoViewModel - Video: ${v.judul}, Kategori: ${v.kategori}")
                            }
                            _videos.value = videoList
                            // Update favorite videos setelah videos di-load
                            _favoriteVideos.value = videoList.filter { it.isFavorited == 1 }
                        }
                        is com.sako.utils.Resource.Error -> {
                            // TODO: Handle error properly - show to user
                            println("VideoViewModel - Error loading videos: ${resource.error}")
                            _videos.value = emptyList()
                        }
                        is com.sako.utils.Resource.Loading -> {
                            println("VideoViewModel - Loading...")
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                println("VideoViewModel - Exception: ${e.message}")
                e.printStackTrace()
                _videos.value = emptyList()
            } finally {
                _isLoading.value = false
                println("VideoViewModel - Finished loading. Final count: ${_videos.value.size}")
            }
        }
    }

    fun loadFavoriteVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("VideoViewModel - Loading favorite videos from API")
                repository.getFavoriteVideos().collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            val favoriteList = resource.data?.data?.videos ?: emptyList()
                            println("VideoViewModel - Favorite videos count: ${favoriteList.size}")
                            _favoriteVideos.value = favoriteList
                        }
                        is com.sako.utils.Resource.Error -> {
                            println("VideoViewModel - Error loading favorites: ${resource.error}")
                            // Fallback: filter dari videos list
                            _favoriteVideos.value = _videos.value.filter { it.isFavorited == 1 }
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                println("VideoViewModel - Error loading favorites: ${e.message}")
                // Fallback: filter dari videos list
                _favoriteVideos.value = _videos.value.filter { it.isFavorited == 1 }
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
                _selectedVideo.value = existingVideo
            } catch (e: Exception) {
                println("Error loading video detail: ${e.message}")
                _selectedVideo.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleVideoFavorite(videoId: String) {
        viewModelScope.launch {
            try {
                println("VideoViewModel - Toggling favorite for video: $videoId")
                
                // Get current video state
                val currentVideo = _videos.value.find { it.id == videoId }
                    ?: _selectedVideo.value
                
                if (currentVideo == null) {
                    println("VideoViewModel - Video not found: $videoId")
                    return@launch
                }
                
                val isFavorited = currentVideo.isFavorited == 1
                println("VideoViewModel - Current favorite state: $isFavorited")
                
                // Call API to toggle favorite
                val apiCall = if (isFavorited) {
                    repository.removeFavoriteVideo(videoId)
                } else {
                    repository.addFavoriteVideo(videoId)
                }
                
                apiCall.collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            println("VideoViewModel - Toggle favorite success")
                            
                            // Update local state
                            val currentVideos = _videos.value.toMutableList()
                            val videoIndex = currentVideos.indexOfFirst { it.id == videoId }

                            if (videoIndex != -1) {
                                val video = currentVideos[videoIndex]
                                val updatedVideo = video.copy(isFavorited = if (isFavorited) 0 else 1)
                                currentVideos[videoIndex] = updatedVideo
                                _videos.value = currentVideos

                                // Update selected video if it's the same
                                if (_selectedVideo.value?.id == videoId) {
                                    _selectedVideo.value = updatedVideo
                                }
                            }
                            
                            // Reload favorite videos from API
                            loadFavoriteVideos()
                        }
                        is com.sako.utils.Resource.Error -> {
                            println("VideoViewModel - Error toggling favorite: ${resource.error}")
                        }
                        is com.sako.utils.Resource.Loading -> {
                            println("VideoViewModel - Toggling favorite...")
                        }
                    }
                }
            } catch (e: Exception) {
                println("VideoViewModel - Error toggling favorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}