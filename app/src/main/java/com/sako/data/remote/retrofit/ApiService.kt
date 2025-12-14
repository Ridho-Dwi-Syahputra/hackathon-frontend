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

    @GET("auth/profile")
    suspend fun getProfile(): ProfileResponse

    @GET("auth/auto-login")
    suspend fun autoLogin(): AuthResponse

    // ========== Map Endpoints ==========

    @GET("map/places")
    suspend fun getTouristPlaces(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): TouristPlaceListResponse

    @GET("map/places/{id}")
    suspend fun getTouristPlaceDetail(
        @Path("id") placeId: String
    ): TouristPlaceDetailResponse

    @GET("map/visited")
    suspend fun getVisitedPlaces(
        @Query("page") page: Int = 1
    ): VisitedPlaceListResponse

    @POST("map/scan/qr")
    suspend fun scanQRCode(
        @Body request: ScanQRRequest
    ): ScanQRResponse

    @GET("map/places/{id}/reviews")
    suspend fun getPlaceReviews(
        @Path("id") placeId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ReviewListResponse

    @POST("map/reviews/add")
    suspend fun addReview(
        @Body request: AddReviewRequest
    ): ReviewResponse

    @PUT("map/reviews/{id}/edit")
    suspend fun updateReview(
        @Path("id") reviewId: String,
        @Body request: UpdateReviewRequest
    ): ReviewResponse

    @DELETE("map/reviews/{id}/delete")
    suspend fun deleteReview(
        @Path("id") reviewId: String
    ): ReviewResponse

    @POST("map/reviews/{id}/toggle-like")
    suspend fun toggleReviewLike(
        @Path("id") reviewId: String
    ): ToggleLikeResponse

    // ========== Profile Endpoints ==========

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse

    @Multipart
    @PUT("auth/profile/image")
    suspend fun updateProfileImage(
        @Part image: MultipartBody.Part
    ): UpdateProfileResponse

    @PUT("auth/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): ChangePasswordResponse
    
    @GET("auth/notification-preferences")
    suspend fun getNotificationPreferences(): NotificationPreferencesResponse
    
    @PUT("auth/notification-preferences")
    suspend fun updateNotificationPreferences(
        @Body request: UpdateNotificationPreferencesRequest
    ): NotificationPreferencesResponse

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

    // ========== Video Collection Endpoints ==========

    @POST("video-collections/collections")
    suspend fun createVideoCollection(
        @Body request: CreateVideoCollectionRequest
    ): VideoCollectionResponse

    @GET("video-collections/collections")
    suspend fun getVideoCollections(): VideoCollectionListResponse

    @GET("video-collections/collections/{collectionId}")
    suspend fun getVideoCollectionDetail(
        @Path("collectionId") collectionId: String
    ): VideoCollectionDetailResponse

    @PUT("video-collections/collections/{collectionId}")
    suspend fun updateVideoCollection(
        @Path("collectionId") collectionId: String,
        @Body request: UpdateVideoCollectionRequest
    ): VideoCollectionResponse

    @DELETE("video-collections/collections/{collectionId}")
    suspend fun deleteVideoCollection(
        @Path("collectionId") collectionId: String
    ): CollectionVideoResponse

    @POST("video-collections/collections/{collectionId}/videos/{videoId}")
    suspend fun addVideoToCollection(
        @Path("collectionId") collectionId: String,
        @Path("videoId") videoId: String
    ): CollectionVideoResponse

    @DELETE("video-collections/collections/{collectionId}/videos/{videoId}")
    suspend fun removeVideoFromCollection(
        @Path("collectionId") collectionId: String,
        @Path("videoId") videoId: String
    ): CollectionVideoResponse

    @GET("video-collections/videos/{videoId}/collections")
    suspend fun getCollectionsForVideo(
        @Path("videoId") videoId: String
    ): VideoCollectionsForVideoResponse

    // ========== Home Dashboard Endpoints ==========

    @GET("home/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("home/stats")
    suspend fun getUserStats(): UserStatsResponse

    @GET("home/activities")
    suspend fun getRecentActivities(
        @Query("limit") limit: Int = 5
    ): RecentActivitiesResponse

    @GET("home/popular")
    suspend fun getPopularContent(
        @Query("limit") limit: Int = 10
    ): PopularContentResponse

}