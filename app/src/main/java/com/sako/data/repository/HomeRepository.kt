package com.sako.data.repository

import com.google.gson.Gson
import com.sako.data.remote.response.*
import com.sako.data.remote.retrofit.ApiService
import com.sako.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

/**
 * Repository for Home Dashboard module
 * Handles all home dashboard operations: stats, activities, popular content
 */
class HomeRepository private constructor(
    private val apiService: ApiService
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

    // ========== Dashboard Data ==========

    /**
     * Get complete dashboard data (stats, activities, popular content, achievements)
     * Used by HomeScreen to display comprehensive overview
     */
    fun getDashboard(): Flow<Resource<DashboardData>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getDashboard()
            
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(Resource.Error("Sesi telah berakhir. Silakan login kembali."))
                403 -> emit(Resource.Error("Anda tidak memiliki akses untuk melihat data ini."))
                404 -> emit(Resource.Error("Data tidak ditemukan."))
                500 -> emit(Resource.Error("Server sedang mengalami gangguan. Coba lagi nanti."))
                else -> {
                    val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Get user statistics only (XP, level, quiz/video/map stats)
     * Used when only stats are needed without other dashboard data
     */
    fun getUserStats(): Flow<Resource<UserStatsData>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getUserStats()
            
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(Resource.Error("Sesi telah berakhir. Silakan login kembali."))
                403 -> emit(Resource.Error("Anda tidak memiliki akses untuk melihat data ini."))
                404 -> emit(Resource.Error("Data pengguna tidak ditemukan."))
                500 -> emit(Resource.Error("Server sedang mengalami gangguan. Coba lagi nanti."))
                else -> {
                    val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Get recent quiz activities
     * @param limit Maximum number of activities to fetch (default: 5)
     */
    fun getRecentActivities(limit: Int = 5): Flow<Resource<List<RecentQuizAttempt>>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getRecentActivities(limit)
            
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(Resource.Error("Sesi telah berakhir. Silakan login kembali."))
                403 -> emit(Resource.Error("Anda tidak memiliki akses untuk melihat data ini."))
                404 -> emit(Resource.Error("Tidak ada aktivitas yang ditemukan."))
                500 -> emit(Resource.Error("Server sedang mengalami gangguan. Coba lagi nanti."))
                else -> {
                    val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Get popular content (videos and places)
     * @param limit Maximum number of items per category (default: 10)
     */
    fun getPopularContent(limit: Int = 10): Flow<Resource<PopularContentData>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getPopularContent(limit)
            
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(Resource.Error("Sesi telah berakhir. Silakan login kembali."))
                403 -> emit(Resource.Error("Anda tidak memiliki akses untuk melihat data ini."))
                404 -> emit(Resource.Error("Konten populer tidak ditemukan."))
                500 -> emit(Resource.Error("Server sedang mengalami gangguan. Coba lagi nanti."))
                else -> {
                    val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null

        fun getInstance(
            apiService: ApiService
        ): HomeRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository(apiService).also { INSTANCE = it }
            }

        fun clearInstance() {
            INSTANCE = null
        }
    }
}
