package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.DashboardData
import com.sako.data.remote.response.PopularContentData
import com.sako.data.remote.response.RecentQuizAttempt
import com.sako.data.remote.response.UserStatsData
import com.sako.data.repository.HomeRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    // ========== UI State ==========
    
    private val _dashboardState = MutableStateFlow<Resource<DashboardData>>(Resource.Loading)
    val dashboardState: StateFlow<Resource<DashboardData>> = _dashboardState.asStateFlow()

    private val _statsState = MutableStateFlow<Resource<UserStatsData>>(Resource.Loading)
    val statsState: StateFlow<Resource<UserStatsData>> = _statsState.asStateFlow()

    private val _activitiesState = MutableStateFlow<Resource<List<RecentQuizAttempt>>>(Resource.Loading)
    val activitiesState: StateFlow<Resource<List<RecentQuizAttempt>>> = _activitiesState.asStateFlow()

    private val _popularState = MutableStateFlow<Resource<PopularContentData>>(Resource.Loading)
    val popularState: StateFlow<Resource<PopularContentData>> = _popularState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ========== Initialization ==========
    
    init {
        loadDashboard()
    }

    // ========== Dashboard Operations ==========

    /**
     * Load complete dashboard data (recommended for initial load)
     * Fetches user stats, recent activities, popular content, and achievements in one call
     */
    fun loadDashboard() {
        viewModelScope.launch {
            homeRepository.getDashboard().collect { result ->
                _dashboardState.value = result
            }
        }
    }

    /**
     * Refresh dashboard with pull-to-refresh
     */
    fun refreshDashboard() {
        viewModelScope.launch {
            _isRefreshing.value = true
            homeRepository.getDashboard().collect { result ->
                _dashboardState.value = result
                if (result !is Resource.Loading) {
                    _isRefreshing.value = false
                }
            }
        }
    }

    /**
     * Load only user statistics
     * Use when only stats section needs updating
     */
    fun loadUserStats() {
        viewModelScope.launch {
            homeRepository.getUserStats().collect { result ->
                _statsState.value = result
            }
        }
    }

    /**
     * Load recent quiz activities
     * @param limit Maximum number of activities to fetch
     */
    fun loadRecentActivities(limit: Int = 5) {
        viewModelScope.launch {
            homeRepository.getRecentActivities(limit).collect { result ->
                _activitiesState.value = result
            }
        }
    }

    /**
     * Load popular content (videos and places)
     * @param limit Maximum number of items per category
     */
    fun loadPopularContent(limit: Int = 10) {
        viewModelScope.launch {
            homeRepository.getPopularContent(limit).collect { result ->
                _popularState.value = result
            }
        }
    }

    /**
     * Clear error states
     */
    fun clearError() {
        if (_dashboardState.value is Resource.Error) {
            _dashboardState.value = Resource.Loading
        }
        if (_statsState.value is Resource.Error) {
            _statsState.value = Resource.Loading
        }
        if (_activitiesState.value is Resource.Error) {
            _activitiesState.value = Resource.Loading
        }
        if (_popularState.value is Resource.Error) {
            _popularState.value = Resource.Loading
        }
    }

    /**
     * Retry loading dashboard after error
     */
    fun retry() {
        clearError()
        loadDashboard()
    }
}