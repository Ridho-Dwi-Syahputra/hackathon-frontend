package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.*
import com.sako.data.repository.MapRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val mapRepository: MapRepository
) : ViewModel() {

    // State for tourist places list
    private val _touristPlaces = MutableStateFlow<Resource<List<TouristPlaceItem>>>(Resource.Loading)
    val touristPlaces: StateFlow<Resource<List<TouristPlaceItem>>> = _touristPlaces.asStateFlow()

    // State for tourist place detail
    private val _touristPlaceDetail = MutableStateFlow<Resource<TouristPlaceDetail>>(Resource.Loading)
    val touristPlaceDetail: StateFlow<Resource<TouristPlaceDetail>> = _touristPlaceDetail.asStateFlow()

    // State for visited places
    private val _visitedPlaces = MutableStateFlow<Resource<List<VisitedPlaceItem>>>(Resource.Loading)
    val visitedPlaces: StateFlow<Resource<List<VisitedPlaceItem>>> = _visitedPlaces.asStateFlow()

    // State for QR scan result
    private val _scanResult = MutableStateFlow<Resource<ScanQRData>?>(null)
    val scanResult: StateFlow<Resource<ScanQRData>?> = _scanResult.asStateFlow()

    // State for reviews list
    private val _reviewsList = MutableStateFlow<Resource<ReviewsData>>(Resource.Loading)
    val reviewsList: StateFlow<Resource<ReviewsData>> = _reviewsList.asStateFlow()

    // State for add/update review operations
    private val _reviewResult = MutableStateFlow<Resource<ReviewItem>?>(null)
    val reviewResult: StateFlow<Resource<ReviewItem>?> = _reviewResult.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTouristPlaces()
    }

    fun loadTouristPlaces(search: String? = null, page: Int = 1) {
        viewModelScope.launch {
            mapRepository.getTouristPlaces(search, page).collect { resource ->
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
            mapRepository.getTouristPlaceDetail(placeId).collect { resource ->
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

    fun loadVisitedPlaces(page: Int = 1) {
        viewModelScope.launch {
            mapRepository.getVisitedPlaces(page).collect { resource ->
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
        // For now, delegate to scanQRCode since checkinLocation doesn't exist in MapRepository
        scanQRCode(qrToken)
    }

    fun addReview(touristPlaceId: String, rating: Int, reviewText: String?) {
        viewModelScope.launch {
            mapRepository.addReview(touristPlaceId, rating, reviewText).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.review?.let { reviewItem ->
                            _reviewResult.value = Resource.Success(reviewItem)
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
            mapRepository.updateReview(reviewId, touristPlaceId, rating, reviewText).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.review?.let { reviewItem ->
                            _reviewResult.value = Resource.Success(reviewItem)
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
            mapRepository.deleteReview(reviewId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _reviewResult.value = resource.data.data?.review?.let { Resource.Success(it) }
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

    fun loadPlaceReviews(placeId: String, page: Int = 1) {
        viewModelScope.launch {
            mapRepository.getPlaceReviews(placeId, page).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Backend now returns data directly in correct format
                        _reviewsList.value = Resource.Success(resource.data.data)
                    }
                    is Resource.Error -> {
                        _reviewsList.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _reviewsList.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun toggleReviewLike(reviewId: String) {
        viewModelScope.launch {
            mapRepository.toggleReviewLike(reviewId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Refresh reviews to update like status
                        val currentDetailResource = _touristPlaceDetail.value
                        if (currentDetailResource is Resource.Success) {
                            loadPlaceReviews(currentDetailResource.data.id)
                        }
                    }
                    is Resource.Error -> {
                        // Handle error if needed
                    }
                    is Resource.Loading -> {
                        // Handle loading if needed
                    }
                }
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            val currentDetailResource = _touristPlaceDetail.value
            if (currentDetailResource is Resource.Success) {
                mapRepository.deleteReview(reviewId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // Reload both detail and reviews after deletion
                            loadTouristPlaceDetail(currentDetailResource.data.id)
                            loadPlaceReviews(currentDetailResource.data.id)
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
    }

    fun resetScanResult() {
        _scanResult.value = null
    }

    fun resetCheckinResult() {
        _scanResult.value = null
    }

    fun resetReviewResult() {
        _reviewResult.value = null
    }

    fun scanQRCode(qrToken: String) {
        viewModelScope.launch {
            mapRepository.scanQRCode(qrToken).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _scanResult.value = Resource.Success(resource.data.data ?: ScanQRData(
                            scan_success = false,
                            tourist_place = null,
                            visited_at = null
                        ))
                    }
                    is Resource.Error -> {
                        _scanResult.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _scanResult.value = Resource.Loading
                    }
                }
            }
        }
    }
}