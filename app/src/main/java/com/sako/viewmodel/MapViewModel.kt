package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.CheckinData
import com.sako.data.remote.response.ReviewItem
import com.sako.data.remote.response.TouristPlaceDetail
import com.sako.data.remote.response.TouristPlaceItem
import com.sako.data.repository.SakoRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: SakoRepository
) : ViewModel() {

    // State for tourist places list
    private val _touristPlaces = MutableStateFlow<Resource<List<TouristPlaceItem>>>(Resource.Loading)
    val touristPlaces: StateFlow<Resource<List<TouristPlaceItem>>> = _touristPlaces.asStateFlow()

    // State for tourist place detail
    private val _touristPlaceDetail = MutableStateFlow<Resource<TouristPlaceDetail>>(Resource.Loading)
    val touristPlaceDetail: StateFlow<Resource<TouristPlaceDetail>> = _touristPlaceDetail.asStateFlow()

    // State for visited places
    private val _visitedPlaces = MutableStateFlow<Resource<List<TouristPlaceItem>>>(Resource.Loading)
    val visitedPlaces: StateFlow<Resource<List<TouristPlaceItem>>> = _visitedPlaces.asStateFlow()

    // State for checkin
    private val _checkinResult = MutableStateFlow<Resource<CheckinData>?>(null)
    val checkinResult: StateFlow<Resource<CheckinData>?> = _checkinResult.asStateFlow()

    // State for review operations
    private val _reviewResult = MutableStateFlow<Resource<ReviewItem>?>(null)
    val reviewResult: StateFlow<Resource<ReviewItem>?> = _reviewResult.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTouristPlaces()
    }

    fun loadTouristPlaces(search: String? = null) {
        viewModelScope.launch {
            repository.getTouristPlaces(search).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _touristPlaces.value = Resource.Success(resource.data.data)
                    }
                    is Resource.Error -> {
                        _touristPlaces.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _touristPlaces.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun loadTouristPlaceDetail(placeId: String) {
        viewModelScope.launch {
            repository.getTouristPlaceDetail(placeId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _touristPlaceDetail.value = Resource.Success(resource.data.data)
                    }
                    is Resource.Error -> {
                        _touristPlaceDetail.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _touristPlaceDetail.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun loadVisitedPlaces() {
        viewModelScope.launch {
            repository.getVisitedPlaces().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _visitedPlaces.value = Resource.Success(resource.data.data)
                    }
                    is Resource.Error -> {
                        _visitedPlaces.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _visitedPlaces.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun checkinLocation(qrToken: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.checkinLocation(qrToken, latitude, longitude).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.let { checkinData ->
                            _checkinResult.value = Resource.Success(checkinData)
                            // Reload places after successful checkin
                            loadTouristPlaces()
                        } ?: run {
                            _checkinResult.value = Resource.Error("Data checkin tidak valid")
                        }
                    }
                    is Resource.Error -> {
                        _checkinResult.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _checkinResult.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun addReview(touristPlaceId: String, rating: Int, reviewText: String?) {
        viewModelScope.launch {
            repository.addReview(touristPlaceId, rating, reviewText).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.let { reviewData ->
                            _reviewResult.value = Resource.Success(reviewData)
                            // Reload detail to show new review
                            loadTouristPlaceDetail(touristPlaceId)
                        } ?: run {
                            _reviewResult.value = Resource.Error("Data ulasan tidak valid")
                        }
                    }
                    is Resource.Error -> {
                        _reviewResult.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _reviewResult.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun updateReview(reviewId: String, touristPlaceId: String, rating: Int, reviewText: String?) {
        viewModelScope.launch {
            repository.updateReview(reviewId, touristPlaceId, rating, reviewText).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.let { reviewData ->
                            _reviewResult.value = Resource.Success(reviewData)
                            // Reload detail to show updated review
                            loadTouristPlaceDetail(touristPlaceId)
                        } ?: run {
                            _reviewResult.value = Resource.Error("Data ulasan tidak valid")
                        }
                    }
                    is Resource.Error -> {
                        _reviewResult.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _reviewResult.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun deleteReview(reviewId: String, touristPlaceId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _reviewResult.value = resource.data.data?.let { Resource.Success(it) }
                        // Reload detail to remove deleted review
                        loadTouristPlaceDetail(touristPlaceId)
                    }
                    is Resource.Error -> {
                        _reviewResult.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _reviewResult.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadTouristPlaces(query.ifEmpty { null })
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        loadTouristPlaces()
    }

    fun resetCheckinResult() {
        _checkinResult.value = null
    }

    fun resetReviewResult() {
        _reviewResult.value = null
    }
}