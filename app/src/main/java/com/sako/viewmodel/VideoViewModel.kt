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
    val videos: StateFlow<List<VideoItem>> = _videos

    private val _selectedVideo = MutableStateFlow<VideoItem?>(null)
    val selectedVideo: StateFlow<VideoItem?> = _selectedVideo

    private val _favoriteVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val favoriteVideos: StateFlow<List<VideoItem>> = _favoriteVideos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
                println("VideoViewModel - Loading video detail for ID: $videoId")
                
                // First try to find in existing videos for immediate display
                val existingVideo = _videos.value.find { it.id == videoId }
                _selectedVideo.value = existingVideo
                println("VideoViewModel - Found in cache: ${existingVideo != null}")
                
                // Then fetch fresh data from API to get latest favorite status
                repository.getVideoDetail(videoId).collect { resource ->
                    println("VideoViewModel - API response: ${resource.javaClass.simpleName}")
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            val videoDetail = resource.data?.data
                            println("VideoViewModel - Video detail loaded: ${videoDetail?.judul}")
                            _selectedVideo.value = videoDetail
                            
                            // Update the video in the list as well
                            if (videoDetail != null) {
                                val updatedList = _videos.value.map { 
                                    if (it.id == videoId) videoDetail else it 
                                }
                                _videos.value = updatedList
                            }
                        }
                        is com.sako.utils.Resource.Error -> {
                            println("VideoViewModel - Error loading video detail: ${resource.error}")
                            // Keep existing video if API fails
                            if (_selectedVideo.value == null) {
                                _selectedVideo.value = existingVideo
                            }
                        }
                        is com.sako.utils.Resource.Loading -> {
                            println("VideoViewModel - Loading video detail from API...")
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                println("VideoViewModel - Exception loading video detail: ${e.message}")
                e.printStackTrace()
                // Fallback to cached video
                val existingVideo = _videos.value.find { it.id == videoId }
                _selectedVideo.value = existingVideo
            } finally {
                _isLoading.value = false
                println("VideoViewModel - Finished loading video detail")
            }
        }
    }

    fun toggleVideoFavorite(videoId: String) {
        viewModelScope.launch {
            try {
                println("VideoViewModel - 🔄 Toggling favorite for video: $videoId")
                
                // Get current video state
                val currentVideo = _videos.value.find { it.id == videoId }
                    ?: _selectedVideo.value
                
                if (currentVideo == null) {
                    println("VideoViewModel - ❌ Video not found: $videoId")
                    return@launch
                }
                
                val isFavorited = currentVideo.isFavorited == 1
                println("VideoViewModel - 📊 Current favorite state: $isFavorited (will change to ${!isFavorited})")
                
                // CRITICAL: Update UI optimistically FIRST for instant feedback
                val newFavoriteStatus = if (isFavorited) 0 else 1
                println("VideoViewModel - ⚡ Updating UI optimistically to: $newFavoriteStatus")
                
                // Update selected video immediately
                _selectedVideo.value = currentVideo.copy(isFavorited = newFavoriteStatus)
                
                // Update in list immediately
                val currentVideos = _videos.value.toMutableList()
                val videoIndex = currentVideos.indexOfFirst { it.id == videoId }
                if (videoIndex != -1) {
                    currentVideos[videoIndex] = currentVideos[videoIndex].copy(isFavorited = newFavoriteStatus)
                    _videos.value = currentVideos
                }
                println("VideoViewModel - ✅ UI updated optimistically")
                
                // Then call API to sync with backend
                val apiCall = if (isFavorited) {
                    repository.removeFavoriteVideo(videoId)
                } else {
                    repository.addFavoriteVideo(videoId)
                }
                
                apiCall.collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            println("VideoViewModel - ✅ API toggle favorite success")
                            
                            // Reload favorite videos list from API for consistency
                            loadFavoriteVideos()
                        }
                        is com.sako.utils.Resource.Error -> {
                            println("VideoViewModel - ❌ API Error toggling favorite: ${resource.error}")
                            println("VideoViewModel - ⚠️ Possible data inconsistency detected")
                            
                            // CRITICAL FIX: Re-fetch video from server to get correct state
                            println("VideoViewModel - 🔄 Re-fetching video from server for correct state")
                            
                            // Revert to original state immediately
                            _selectedVideo.value = currentVideo
                            val revertVideos = _videos.value.toMutableList()
                            val revertIndex = revertVideos.indexOfFirst { it.id == videoId }
                            if (revertIndex != -1) {
                                revertVideos[revertIndex] = currentVideo
                                _videos.value = revertVideos
                            }
                            
                            // Re-fetch from server to get the ACTUAL favorite status
                            repository.getVideoDetail(videoId).collect { detailResource ->
                                when (detailResource) {
                                    is com.sako.utils.Resource.Success -> {
                                        val serverVideo = detailResource.data?.data
                                        if (serverVideo != null) {
                                            println("VideoViewModel - ✅ Server state fetched: is_favorited=${serverVideo.isFavorited}")
                                            _selectedVideo.value = serverVideo
                                            
                                            val updatedList = _videos.value.map { 
                                                if (it.id == videoId) serverVideo else it 
                                            }
                                            _videos.value = updatedList
                                        }
                                    }
                                    is com.sako.utils.Resource.Error -> {
                                        println("VideoViewModel - ❌ Failed to fetch server state: ${detailResource.error}")
                                    }
                                    else -> { /* Loading */ }
                                }
                            }
                        }
                        is com.sako.utils.Resource.Loading -> {
                            println("VideoViewModel - ⏳ API call in progress...")
                        }
                    }
                }
            } catch (e: Exception) {
                println("VideoViewModel - ❌ Exception toggling favorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}