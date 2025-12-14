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
        // Delegate to scanQRCode with location coordinates
        scanQRCode(qrToken, latitude, longitude)
    }

    fun addReview(touristPlaceId: String, rating: Int, reviewText: String?) {
        viewModelScope.launch {
            mapRepository.addReview(touristPlaceId, rating, reviewText).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.review?.let { reviewItem ->
                            _reviewResult.value = Resource.Success(reviewItem)
                            // Reload reviews and detail to show new review
                            loadPlaceReviews(touristPlaceId)
                            loadTouristPlaceDetail(touristPlaceId)
                            
                            // Reset review result after brief delay to allow UI to show success
                            kotlinx.coroutines.delay(1500)
                            _reviewResult.value = null
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
                            // Reload reviews and detail to show updated review
                            loadPlaceReviews(touristPlaceId)
                            loadTouristPlaceDetail(touristPlaceId)
                            
                            // Reset review result after brief delay
                            kotlinx.coroutines.delay(1500)
                            _reviewResult.value = null
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
            try {
                mapRepository.toggleReviewLike(reviewId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // Update local state without reloading
                            val currentReviewsResource = _reviewsList.value
                            if (currentReviewsResource is Resource.Success) {
                                val likeData = resource.data.data
                                if (likeData != null) {
                                    val updatedData = currentReviewsResource.data.copy(
                                        userReview = currentReviewsResource.data.userReview?.let { userReview ->
                                            if (userReview.id == reviewId) {
                                                // Create new instance with updated values
                                                ReviewItem(
                                                    id = userReview.id,
                                                    userId = userReview.userId,
                                                    touristPlaceId = userReview.touristPlaceId,
                                                    rating = userReview.rating,
                                                    reviewText = userReview.reviewText,
                                                    totalLikes = likeData.totalLikes,
                                                    createdAt = userReview.createdAt,
                                                    updatedAt = userReview.updatedAt,
                                                    userName = userReview.userName,
                                                    userImageUrl = userReview.userImageUrl,
                                                    isLikedByMe = likeData.isLikedByMe
                                                )
                                            } else userReview
                                        },
                                        otherReviews = currentReviewsResource.data.otherReviews.map { review ->
                                            if (review.id == reviewId) {
                                                // Create new instance with updated values
                                                ReviewItem(
                                                    id = review.id,
                                                    userId = review.userId,
                                                    touristPlaceId = review.touristPlaceId,
                                                    rating = review.rating,
                                                    reviewText = review.reviewText,
                                                    totalLikes = likeData.totalLikes,
                                                    createdAt = review.createdAt,
                                                    updatedAt = review.updatedAt,
                                                    userName = review.userName,
                                                    userImageUrl = review.userImageUrl,
                                                    isLikedByMe = likeData.isLikedByMe
                                                )
                                            } else review
                                        }
                                    )
                                    _reviewsList.value = Resource.Success(updatedData)
                                }
                            }
                        }
                        is Resource.Error -> {
                            // Handle error silently or show snackbar
                        }
                        is Resource.Loading -> {
                            // No need to show loading for like toggle
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch any unexpected errors
                android.util.Log.e("MapViewModel", "Error toggling like: ${e.message}", e)
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            val currentDetailResource = _touristPlaceDetail.value
            if (currentDetailResource is Resource.Success) {
                val touristPlaceId = currentDetailResource.data.id
                mapRepository.deleteReview(reviewId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // Update local state to remove deleted review
                            val currentReviewsResource = _reviewsList.value
                            if (currentReviewsResource is Resource.Success) {
                                val updatedData = currentReviewsResource.data.copy(
                                    userReview = if (currentReviewsResource.data.userReview?.id == reviewId) {
                                        null
                                    } else currentReviewsResource.data.userReview,
                                    otherReviews = currentReviewsResource.data.otherReviews.filter { 
                                        it.id != reviewId 
                                    }
                                )
                                _reviewsList.value = Resource.Success(updatedData)
                            }
                            
                            // Reload place detail to update average rating
                            loadTouristPlaceDetail(touristPlaceId)
                            
                            // Clear review result state
                            _reviewResult.value = null
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

    fun scanQRCode(qrToken: String, userLatitude: Double, userLongitude: Double) {
        viewModelScope.launch {
            mapRepository.scanQRCode(qrToken, userLatitude, userLongitude).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _scanResult.value = Resource.Success(resource.data.data ?: ScanQRData(
                            scanSuccess = false,
                            touristPlace = null,
                            visitedAt = null,
                            locationValidation = null,
                            rewardInfo = null
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