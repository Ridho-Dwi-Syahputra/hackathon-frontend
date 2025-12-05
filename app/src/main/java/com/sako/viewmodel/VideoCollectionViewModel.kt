package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.repository.SakoRepository
import com.sako.data.remote.request.CreateVideoCollectionRequest
import com.sako.data.remote.request.UpdateVideoCollectionRequest
import com.sako.data.remote.response.VideoCollectionItem
import com.sako.data.remote.response.VideoCollectionWithFlag
import com.sako.data.remote.response.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoCollectionViewModel(private val repository: SakoRepository) : ViewModel() {

    private val _collections = MutableStateFlow<List<VideoCollectionItem>>(emptyList())
    val collections: StateFlow<List<VideoCollectionItem>> = _collections.asStateFlow()

    private val _selectedCollection = MutableStateFlow<VideoCollectionItem?>(null)
    val selectedCollection: StateFlow<VideoCollectionItem?> = _selectedCollection.asStateFlow()

    private val _collectionVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val collectionVideos: StateFlow<List<VideoItem>> = _collectionVideos.asStateFlow()

    private val _collectionsForVideo = MutableStateFlow<List<VideoCollectionWithFlag>>(emptyList())
    val collectionsForVideo: StateFlow<List<VideoCollectionWithFlag>> = _collectionsForVideo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getVideoCollections().collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _collections.value = resource.data?.data ?: emptyList()
                            println("VideoCollectionViewModel - Collections loaded: ${_collections.value.size}")
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                            println("VideoCollectionViewModel - Error loading collections: ${resource.error}")
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
                println("VideoCollectionViewModel - Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCollectionDetail(collectionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getVideoCollectionDetail(collectionId).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _selectedCollection.value = resource.data?.data?.collection
                            _collectionVideos.value = resource.data?.data?.videos ?: emptyList()
                            println("VideoCollectionViewModel - Collection detail loaded: ${_collectionVideos.value.size} videos")
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                            println("VideoCollectionViewModel - Error loading detail: ${resource.error}")
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCollection(namaKoleksi: String, deskripsi: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val request = CreateVideoCollectionRequest(
                    namaKoleksi = namaKoleksi,
                    deskripsi = deskripsi
                )
                repository.createVideoCollection(request).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _successMessage.value = resource.data?.message ?: "Koleksi berhasil dibuat"
                            loadCollections() // Reload collections
                            println("VideoCollectionViewModel - Collection created")
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                            println("VideoCollectionViewModel - Error creating collection: ${resource.error}")
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCollection(collectionId: String, namaKoleksi: String?, deskripsi: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val request = UpdateVideoCollectionRequest(
                    namaKoleksi = namaKoleksi,
                    deskripsi = deskripsi
                )
                repository.updateVideoCollection(collectionId, request).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _successMessage.value = resource.data?.message ?: "Koleksi berhasil diupdate"
                            loadCollections()
                            loadCollectionDetail(collectionId)
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                repository.deleteVideoCollection(collectionId).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _successMessage.value = resource.data?.message ?: "Koleksi berhasil dihapus"
                            loadCollections()
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addVideoToCollection(collectionId: String, videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                repository.addVideoToCollection(collectionId, videoId).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _successMessage.value = resource.data?.message ?: "Video berhasil ditambahkan"
                            loadCollections() // Refresh to update jumlah_video
                            println("VideoCollectionViewModel - Video added to collection")
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                            println("VideoCollectionViewModel - Error adding video: ${resource.error}")
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeVideoFromCollection(collectionId: String, videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                repository.removeVideoFromCollection(collectionId, videoId).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _successMessage.value = resource.data?.message ?: "Video berhasil dihapus"
                            // Refresh current collection detail if viewing
                            _selectedCollection.value?.id?.let { loadCollectionDetail(it) }
                            loadCollections()
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCollectionsForVideo(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.getCollectionsForVideo(videoId).collect { resource ->
                    when (resource) {
                        is com.sako.utils.Resource.Success -> {
                            _collectionsForVideo.value = resource.data?.data ?: emptyList()
                            println("VideoCollectionViewModel - Collections for video loaded: ${_collectionsForVideo.value.size}")
                        }
                        is com.sako.utils.Resource.Error -> {
                            _errorMessage.value = resource.error
                        }
                        is com.sako.utils.Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
