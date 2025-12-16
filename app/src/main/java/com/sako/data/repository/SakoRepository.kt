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
 * General Repository for common operations
 * Auth operations moved to AuthRepository
 * Map operations moved to MapRepository
 * This repository handles: Categories, Videos, Badges, Quiz operations
 */
class SakoRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    // ========== Error Handling ==========
    
    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Terjadi kesalahan pada server"
        
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse?.message ?: "Terjadi kesalahan pada server"
        } catch (e: Exception) {
            "Terjadi kesalahan pada server"
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Quiz Operations ==========

    fun startQuiz(levelId: String): Flow<Resource<QuizStartResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = CheckinQuizRequest(levelId)
            val response = apiService.startQuiz(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ========== Video Collections ==========

    fun createVideoCollection(request: CreateVideoCollectionRequest): Flow<Resource<VideoCollectionResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.createVideoCollection(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getVideoCollections(): Flow<Resource<VideoCollectionListResponse>> = flow {
        emit(Resource.Loading)
        try {
            android.util.Log.d("REPO_VIDEO_COLLECTION", "🔄 Fetching video collections...")
            val response = apiService.getVideoCollections()
            android.util.Log.d("REPO_VIDEO_COLLECTION", "✅ Success: ${response.success}, Data size: ${response.data.size}")
            android.util.Log.d("REPO_VIDEO_COLLECTION", "📦 Response data: ${response.data}")
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("REPO_VIDEO_COLLECTION", "❌ HTTP Error ${e.code()}: $errorBody")
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            android.util.Log.e("REPO_VIDEO_COLLECTION", "❌ Exception: ${e.message}", e)
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getVideoCollectionDetail(collectionId: String): Flow<Resource<VideoCollectionDetailResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getVideoCollectionDetail(collectionId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateVideoCollection(collectionId: String, request: UpdateVideoCollectionRequest): Flow<Resource<VideoCollectionResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.updateVideoCollection(collectionId, request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun deleteVideoCollection(collectionId: String): Flow<Resource<CollectionVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.deleteVideoCollection(collectionId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun addVideoToCollection(collectionId: String, videoId: String): Flow<Resource<CollectionVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.addVideoToCollection(collectionId, videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun removeVideoFromCollection(collectionId: String, videoId: String): Flow<Resource<CollectionVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.removeVideoFromCollection(collectionId, videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getCollectionsForVideo(videoId: String): Flow<Resource<VideoCollectionsForVideoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getCollectionsForVideo(videoId)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
