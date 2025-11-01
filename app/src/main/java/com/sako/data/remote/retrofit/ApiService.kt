package com.sako.data.remote.retrofit

import com.sako.data.remote.request.*
import com.sako.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ========== Authentication Endpoints ==========

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("auth/logout")
    suspend fun logout(): AuthResponse

    // ========== Profile Endpoints ==========

    @GET("users/profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("users/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse

    @Multipart
    @PUT("users/profile/image")
    suspend fun updateProfileImage(
        @Part image: MultipartBody.Part
    ): UpdateProfileResponse

    @PUT("users/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): AuthResponse

    // ========== Quiz Category Endpoints ==========

    @GET("categories")
    suspend fun getCategories(): CategoryListResponse

    @GET("categories/{categoryId}/levels")
    suspend fun getLevelsByCategory(
        @Path("categoryId") categoryId: String
    ): LevelListResponse

    // ========== Quiz Endpoints ==========

    @POST("quiz/start")
    suspend fun startQuiz(
        @Body request: CheckinQuizRequest
    ): QuizStartResponse

    @POST("quiz/submit")
    suspend fun submitQuiz(
        @Body request: SubmitQuizRequest
    ): QuizSubmitResponse

    @GET("quiz/attempts/{attemptId}")
    suspend fun getQuizAttempt(
        @Path("attemptId") attemptId: String
    ): QuizStartResponse

    // ========== Badge Endpoints ==========

    @GET("badges")
    suspend fun getAllBadges(): CategoryListResponse

    @GET("users/badges")
    suspend fun getUserBadges(): CategoryListResponse

    // ========== Video Endpoints ==========

    @GET("videos")
    suspend fun getVideos(
        @Query("search") search: String? = null,
        @Query("kategori") kategori: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): VideoListResponse

    @GET("videos/{videoId}")
    suspend fun getVideoDetail(
        @Path("videoId") videoId: String
    ): VideoDetailResponse

    @POST("videos/{videoId}/favorite")
    suspend fun addFavoriteVideo(
        @Path("videoId") videoId: String
    ): FavoriteVideoResponse

    @DELETE("videos/{videoId}/favorite")
    suspend fun removeFavoriteVideo(
        @Path("videoId") videoId: String
    ): FavoriteVideoResponse

    @GET("videos/favorites")
    suspend fun getFavoriteVideos(): VideoListResponse

    // ========== Tourist Place (Map) Endpoints ==========

    @GET("locations")
    suspend fun getTouristPlaces(
        @Query("search") search: String? = null
    ): TouristPlaceListResponse

    @GET("locations/{placeId}")
    suspend fun getTouristPlaceDetail(
        @Path("placeId") placeId: String
    ): TouristPlaceDetailResponse

    @POST("locations/checkin")
    suspend fun checkinLocation(
        @Body request: CheckinLocationRequest
    ): CheckinLocationResponse

    @GET("locations/visited")
    suspend fun getVisitedPlaces(): TouristPlaceListResponse

    // ========== Review Endpoints ==========

    @POST("reviews")
    suspend fun addReview(
        @Body request: AddReviewRequest
    ): AddReviewResponse

    @PUT("reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReviewRequest
    ): AddReviewResponse

    @DELETE("reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: String
    ): AddReviewResponse
}