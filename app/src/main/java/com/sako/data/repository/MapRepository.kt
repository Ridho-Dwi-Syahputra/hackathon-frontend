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
    
    private fun parseErrorMessage(errorBody: String?, statusCode: Int? = null): String {
        if (errorBody.isNullOrEmpty()) return "Terjadi kesalahan pada server"
        
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val message = errorResponse?.message ?: "Terjadi kesalahan pada server"
            
            // Log for debugging
            android.util.Log.d("MAP_REPOSITORY", "Parsed error message: $message")
            android.util.Log.d("MAP_REPOSITORY", "Status code: $statusCode")
            
            // Handle specific error codes for better user experience
            when (statusCode) {
                409 -> message // ALREADY_VISITED - show backend message directly
                404 -> "QR Code tidak valid atau tempat wisata tidak ditemukan"
                400 -> message // BAD_REQUEST - show validation message (including LOCATION_TOO_FAR)
                403 -> "Akses ditolak. Anda tidak memiliki izin."
                500 -> "Terjadi kesalahan pada server. Silakan coba lagi."
                else -> message
            }
        } catch (e: Exception) {
            android.util.Log.e("MAP_REPOSITORY", "Error parsing error message: ${e.message}")
            when (statusCode) {
                409 -> "Anda sudah pernah mengunjungi tempat ini"
                404 -> "QR Code tidak valid"
                400 -> "Gagal memproses permintaan"
                else -> "Terjadi kesalahan pada server"
            }
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun searchPlaces(query: String): Flow<Resource<TouristPlaceListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.searchTouristPlaces(query)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== QR Code Scanning ==========

    fun scanQRCode(
        qrCodeValue: String,
        userLatitude: Double,
        userLongitude: Double
    ): Flow<Resource<ScanQRResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = ScanQRRequest(
                qrCodeValue = qrCodeValue,
                userLatitude = userLatitude,
                userLongitude = userLongitude
            )
            val response = apiService.scanQRCode(request)
            
            // CATATAN: Scan QR sekarang memberikan XP reward (50 XP) jika berhasil
            // Validasi lokasi dilakukan di backend dengan radius 200m
            
            // Check if response indicates success or failure
            if (response.success && response.data?.scanSuccess == true) {
                val placeName = response.data.touristPlace?.name ?: "Unknown Place"
                val xpEarned = response.data.rewardInfo?.xpEarned ?: 0
                android.util.Log.d("MAP_REPOSITORY", "✅ Place visit successful: $placeName (+$xpEarned XP)")
                emit(Resource.Success(response))
            } else {
                // Backend returned success=false (shouldn't happen with proper backend, but handle it)
                val errorMessage = response.message ?: "Gagal scan QR"
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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
            emit(Resource.Error(parseErrorMessage(errorBody, e.code())))
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