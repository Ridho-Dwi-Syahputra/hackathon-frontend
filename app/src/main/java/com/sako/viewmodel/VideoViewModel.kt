package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import com.sako.data.repository.SakoRepository
import com.sako.data.remote.response.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoViewModel(private val repository: SakoRepository) : ViewModel() {
    // Temporarily use empty list until we implement the repository methods
    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()
}