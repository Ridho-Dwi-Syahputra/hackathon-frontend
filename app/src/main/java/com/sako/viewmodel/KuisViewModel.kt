package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.DummyData
import com.sako.data.remote.response.CategoryItem
import com.sako.data.remote.response.CategoryListResponse
import com.sako.data.remote.response.LevelItem
import com.sako.data.remote.response.LevelListResponse
import com.sako.data.repository.SakoRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KuisViewModel(
    private val repository: SakoRepository
) : ViewModel() {

    // ========== Category List State ==========

    private val _categoriesState = MutableStateFlow<Resource<CategoryListResponse>>(Resource.Loading)
    val categoriesState: StateFlow<Resource<CategoryListResponse>> = _categoriesState.asStateFlow()

    // ========== Level List State ==========

    private val _levelsState = MutableStateFlow<Resource<LevelListResponse>>(Resource.Loading)
    val levelsState: StateFlow<Resource<LevelListResponse>> = _levelsState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<CategoryItem?>(null)
    val selectedCategory: StateFlow<CategoryItem?> = _selectedCategory.asStateFlow()

    // ========== Functions ==========

    /**
     * Fetch all quiz categories with progress
     * Fallback ke dummy data jika API gagal
     */
    fun getCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { resource ->
                // Jika error (termasuk connection timeout), gunakan dummy data
                if (resource is Resource.Error) {
                    _categoriesState.value = Resource.Success(DummyData.getDummyCategories())
                } else {
                    _categoriesState.value = resource
                }
            }
        }
    }

    /**
     * Fetch levels for a specific category
     * Fallback ke dummy data jika API gagal
     */
    fun getLevelsByCategory(categoryId: String) {
        viewModelScope.launch {
            _levelsState.value = Resource.Loading
            repository.getLevelsByCategory(categoryId).collect { resource ->
                // Jika error (termasuk connection timeout), gunakan dummy data
                if (resource is Resource.Error) {
                    val dummyData = DummyData.getDummyLevels(categoryId)
                    _levelsState.value = Resource.Success(dummyData)
                    _selectedCategory.value = dummyData.data.category
                } else {
                    _levelsState.value = resource
                    
                    // Update selected category if success
                    if (resource is Resource.Success) {
                        _selectedCategory.value = resource.data.data.category
                    }
                }
            }
        }
    }

    /**
     * Set selected category (untuk UI state)
     */
    fun selectCategory(category: CategoryItem) {
        _selectedCategory.value = category
    }

    /**
     * Clear selected category and levels
     */
    fun clearLevels() {
        _levelsState.value = Resource.Loading
        _selectedCategory.value = null
    }

    /**
     * Refresh categories (untuk pull-to-refresh)
     */
    fun refreshCategories() {
        getCategories()
    }

    /**
     * Refresh levels (untuk pull-to-refresh)
     */
    fun refreshLevels(categoryId: String) {
        getLevelsByCategory(categoryId)
    }

    // ========== Helper Functions ==========

    /**
     * Get category by ID from current state
     */
    fun getCategoryById(categoryId: String): CategoryItem? {
        val state = _categoriesState.value
        return if (state is Resource.Success) {
            state.data.data.find { it.id == categoryId }
        } else {
            null
        }
    }

    /**
     * Get level by ID from current state
     */
    fun getLevelById(levelId: String): LevelItem? {
        val state = _levelsState.value
        return if (state is Resource.Success) {
            state.data.data.levels.find { it.id == levelId }
        } else {
            null
        }
    }
}