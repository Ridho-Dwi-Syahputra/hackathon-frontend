package com.sako.data.repository

import com.google.gson.Gson
import com.sako.data.pref.UserPreference
import com.sako.data.remote.request.*
import com.sako.data.remote.response.*
import com.sako.data.remote.retrofit.ApiService
import com.sako.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

/**
 * Repository for Map module
 * Handles all map-related operations: tourist places, QR scanning, reviews, visited places
 */
class MapRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    // ========== Error Handling ==========
    
    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Terjadi kesalahan pada server"
        
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse?.message ?: "Terjadi kesalahan pada server"
        } catch (_: Exception) {
            "Terjadi kesalahan pada server"
        }
    }

    // ========== Tourist Places ==========

    fun getTouristPlaces(search: String? = null, page: Int = 1): Flow<Resource<TouristPlaceListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getTouristPlaces(search, page)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getTouristPlaceDetail(placeId: String): Flow<Resource<TouristPlaceDetailResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getTouristPlaceDetail(placeId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getVisitedPlaces(page: Int = 1): Flow<Resource<VisitedPlaceListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getVisitedPlaces(page)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== QR Code Scanning ==========

    fun scanQRCode(qrCodeValue: String): Flow<Resource<ScanQRResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = ScanQRRequest(qrCodeValue)
            val response = apiService.scanQRCode(request)
            
            // CATATAN: Scan QR hanya mencatat kunjungan, tidak ada XP reward
            // Sesuai dokumentasi: "❌ TIDAK memberikan XP (hanya mencatat kunjungan)"
            
            // Handle notification if scan was successful
            if (response.success && response.data?.scan_success == true) {
                val placeName = response.data.tourist_place?.name ?: "Unknown Place"
                android.util.Log.d("MAP_REPOSITORY", "✅ Place visit successful: $placeName")
            }
            
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Reviews ==========

    fun getPlaceReviews(placeId: String, page: Int = 1): Flow<Resource<ReviewListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getPlaceReviews(placeId, page)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun addReview(touristPlaceId: String, rating: Int, reviewText: String?): Flow<Resource<ReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = AddReviewRequest(touristPlaceId, rating, reviewText)
            val response = apiService.addReview(request)
            
            // Handle notification if review was successful
            if (response.success) {
                val placeName = response.data?.touristPlace?.name ?: "Unknown Place"
                android.util.Log.d("MAP_REPOSITORY", "✅ Review added successfully: $placeName, rating: $rating")
            }
            
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateReview(reviewId: String, touristPlaceId: String, rating: Int, reviewText: String?): Flow<Resource<ReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = UpdateReviewRequest(touristPlaceId, rating, reviewText)
            val response = apiService.updateReview(reviewId, request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun deleteReview(reviewId: String): Flow<Resource<ReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.deleteReview(reviewId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun toggleReviewLike(reviewId: String): Flow<Resource<ToggleLikeResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.toggleReviewLike(reviewId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    companion object {
        @Volatile
        private var instance: MapRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): MapRepository =
            instance ?: synchronized(this) {
                instance ?: MapRepository(apiService, userPreference)
            }.also { instance = it }
    }
}