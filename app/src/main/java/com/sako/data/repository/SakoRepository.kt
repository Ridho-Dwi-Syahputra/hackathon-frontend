package com.sako.data.repository

import com.google.gson.Gson
import com.sako.data.pref.UserModel
import com.sako.data.pref.UserPreference
import com.sako.data.remote.request.*
import com.sako.data.remote.response.*
import com.sako.data.remote.retrofit.ApiService
import com.sako.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class SakoRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    // ========== Helper Functions ==========

    /**
     * Helper function untuk parse error response dengan aman
     * Menghindari NullPointerException saat parsing gagal
     */
    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse?.message ?: "Terjadi kesalahan pada server"
        } catch (e: Exception) {
            "Terjadi kesalahan pada server"
        }
    }

    // ========== Session Management ==========

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (e: Exception) {
            // Tetap logout dari lokal meskipun API gagal
        }
        userPreference.logout()
    }

    // ========== Authentication ==========

    fun register(fullName: String, email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = RegisterRequest(fullName, email, password)
            val response = apiService.register(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            response.data?.let { authData ->
                val userModel = UserModel(
                    id = authData.user.id,
                    fullName = authData.user.fullName,
                    email = authData.user.email,
                    totalXp = authData.user.totalXp ?: 0,
                    status = authData.user.status,
                    userImageUrl = authData.user.userImageUrl,
                    token = authData.token,
                    isLogin = true
                )
                saveSession(userModel)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Profile ==========

    fun getProfile(): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getProfile()

            // Update session dengan data terbaru
            response.data?.let { profileData ->
                userPreference.updateUserProfile(
                    fullName = profileData.user.fullName,
                    userImageUrl = profileData.user.userImageUrl
                )
                userPreference.updateTotalXp(profileData.user.totalXp)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateProfile(fullName: String): Flow<Resource<UpdateProfileResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = UpdateProfileRequest(fullName)
            val response = apiService.updateProfile(request)

            // Update session
            response.data?.let {
                userPreference.updateUserProfile(it.user.fullName, it.user.userImageUrl)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateProfileImage(imageFile: File): Flow<Resource<UpdateProfileResponse>> = flow {
        emit(Resource.Loading)
        try {
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.updateProfileImage(multipartBody)

            // Update session dengan URL gambar baru
            response.data?.imageUrl?.let { imageUrl ->
                userPreference.updateUserProfile(
                    response.data.user.fullName,
                    imageUrl
                )
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun changePassword(oldPassword: String, newPassword: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = ChangePasswordRequest(oldPassword, newPassword)
            val response = apiService.changePassword(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Quiz Categories & Levels ==========

    fun getCategories(): Flow<Resource<CategoryListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getCategories()
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (ex: Exception) {
                null
            }
            emit(Resource.Error(errorResponse?.message ?: "Terjadi kesalahan pada server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getLevelsByCategory(categoryId: String): Flow<Resource<LevelListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getLevelsByCategory(categoryId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (ex: Exception) {
                null
            }
            emit(Resource.Error(errorResponse?.message ?: "Terjadi kesalahan pada server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Quiz Attempt ========== (UPDATED)

    fun startQuiz(levelId: String): Flow<Resource<QuizStartResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = CheckinQuizRequest(levelId)
            val response = apiService.startQuiz(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (ex: Exception) {
                null
            }
            emit(Resource.Error(errorResponse?.message ?: "Terjadi kesalahan pada server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun submitQuiz(attemptId: String, answers: List<QuizAnswerRequest>): Flow<Resource<QuizSubmitResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = SubmitQuizRequest(attemptId, answers)
            val response = apiService.submitQuiz(request)

            // Update total XP di session
            response.data?.let {
                userPreference.updateTotalXp(it.newTotalXp)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (ex: Exception) {
                null
            }
            emit(Resource.Error(errorResponse?.message ?: "Terjadi kesalahan pada server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getQuizAttempt(attemptId: String): Flow<Resource<QuizStartResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getQuizAttempt(attemptId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Videos ==========

    fun getVideos(search: String? = null, kategori: String? = null, page: Int = 1): Flow<Resource<VideoListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getVideos(search, kategori, page)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getVideoDetail(videoId: String): Flow<Resource<VideoDetailResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getVideoDetail(videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun addFavoriteVideo(videoId: String): Flow<Resource<FavoriteVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.addFavoriteVideo(videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun removeFavoriteVideo(videoId: String): Flow<Resource<FavoriteVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.removeFavoriteVideo(videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getFavoriteVideos(): Flow<Resource<VideoListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getFavoriteVideos()
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Tourist Places (Locations) ==========

    fun getTouristPlaces(search: String? = null): Flow<Resource<TouristPlaceListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getTouristPlaces(search)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
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
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun checkinLocation(qrToken: String, latitude: Double, longitude: Double): Flow<Resource<CheckinLocationResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = CheckinLocationRequest(qrToken, latitude, longitude)
            val response = apiService.checkinLocation(request)

            // Update total XP di session
            response.data?.let {
                userPreference.updateTotalXp(it.newTotalXp)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getVisitedPlaces(): Flow<Resource<TouristPlaceListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getVisitedPlaces()
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Reviews ==========

    fun addReview(touristPlaceId: String, rating: Int, reviewText: String?): Flow<Resource<AddReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = AddReviewRequest(touristPlaceId, rating, reviewText)
            val response = apiService.addReview(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateReview(reviewId: String, touristPlaceId: String, rating: Int, reviewText: String?): Flow<Resource<AddReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = UpdateReviewRequest(touristPlaceId, rating, reviewText)
            val response = apiService.updateReview(reviewId, request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun deleteReview(reviewId: String): Flow<Resource<AddReviewResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.deleteReview(reviewId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Badges ==========

    fun getAllBadges(): Flow<Resource<CategoryListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getAllBadges()
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getUserBadges(): Flow<Resource<CategoryListResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getUserBadges()
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    companion object {
        @Volatile
        private var instance: SakoRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): SakoRepository =
            instance ?: synchronized(this) {
                instance ?: SakoRepository(apiService, userPreference)
            }.also { instance = it }
    }
}
