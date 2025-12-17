package com.sako.data.repository

import com.sako.data.pref.UserPreference
import com.sako.data.remote.request.ChangePasswordRequest
import com.sako.data.remote.request.UpdateProfileRequest
import com.sako.data.remote.response.ChangePasswordResponse
import com.sako.data.remote.response.ProfileResponse
import com.sako.data.remote.response.UpdateProfileResponse
import com.sako.data.remote.retrofit.ApiService
import com.sako.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    /**
     * Get user profile with stats and badges
     * Endpoint: GET /api/auth/profile
     */
    fun getProfile(): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getProfile()
            
            if (response.success) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Sesi Anda telah berakhir. Silakan login kembali"
                404 -> "Profil tidak ditemukan"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Update user profile (name and email)
     * Endpoint: PUT /api/auth/profile
     */
    fun updateProfile(
        fullName: String,
        email: String
    ): Flow<Resource<UpdateProfileResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val request = UpdateProfileRequest(
                fullName = fullName,
                email = email
            )
            
            val response = apiService.updateProfile(request)
            
            if (response.success) {
                // Update user preferences if needed
                val currentUser = userPreference.getSession().first()
                userPreference.saveSession(
                    currentUser.copy(
                        fullName = fullName,
                        email = email
                    )
                )
                
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Data yang Anda masukkan tidak valid"
                401 -> "Sesi Anda telah berakhir. Silakan login kembali"
                409 -> "Email sudah digunakan oleh pengguna lain"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Update user profile image
     * Endpoint: PUT /api/auth/profile/image
     */
    fun updateProfileImage(
        imageFile: File
    ): Flow<Resource<UpdateProfileResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val response = apiService.updateProfileImage(body)
            
            if (response.success) {
                // Update user preferences with new image URL
                response.data?.user?.let { updatedUser ->
                    val currentUser = userPreference.getSession().first()
                    userPreference.saveSession(
                        currentUser.copy(
                            userImageUrl = updatedUser.userImageUrl
                        )
                    )
                }
                
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "File gambar tidak valid atau terlalu besar (maksimal 10MB)"
                401 -> "Sesi Anda telah berakhir. Silakan login kembali"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan saat mengunggah gambar"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Change user password
     * Endpoint: PUT /api/auth/password
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<ChangePasswordResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val request = ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            
            val response = apiService.changePassword(request)
            
            if (response.success) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Password baru harus minimal 6 karakter"
                401 -> "Password lama tidak sesuai"
                404 -> "Pengguna tidak ditemukan"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get notification preferences
     * Endpoint: GET /api/auth/notification-preferences
     */
    fun getNotificationPreferences(): Flow<Resource<com.sako.data.remote.response.NotificationPreferencesResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getNotificationPreferences()
            
            if (response.success) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Sesi Anda telah berakhir. Silakan login kembali"
                404 -> "User tidak ditemukan"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Update notification preferences
     * Endpoint: PUT /api/auth/notification-preferences
     */
    fun updateNotificationPreferences(
        preferences: com.sako.data.remote.request.NotificationPreferences
    ): Flow<Resource<com.sako.data.remote.response.NotificationPreferencesResponse>> = flow {
        emit(Resource.Loading)
        
        try {
            val request = com.sako.data.remote.request.UpdateNotificationPreferencesRequest(
                notificationPreferences = preferences
            )
            
            val response = apiService.updateNotificationPreferences(request)
            
            if (response.success) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Data preferensi notifikasi tidak valid"
                401 -> "Sesi Anda telah berakhir. Silakan login kembali"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)

    // ============================================================================
    // BADGE FUNCTIONS
    // ============================================================================

    /**
     * Get all badges dengan status (owned/locked) dan progress
     * Endpoint: GET /api/badges
     */
    fun getAllBadges(): Flow<Resource<com.sako.data.remote.response.BadgeListData>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getAllBadges()
            
            if (response.success) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat badge"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Sesi Anda telah berakhir"
                500 -> "Terjadi kesalahan pada server"
                else -> e.message() ?: "Terjadi kesalahan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get user's earned badges
     * Endpoint: GET /api/badges/user
     */
    fun getUserBadges(): Flow<Resource<List<com.sako.data.remote.response.Badge>>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getUserBadges()
            
            if (response.success) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat badge"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get unviewed badges (untuk popup)
     * Endpoint: GET /api/badges/unviewed
     */
    fun getUnviewedBadges(): Flow<Resource<List<com.sako.data.remote.response.Badge>>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.getUnviewedBadges()
            
            if (response.success) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat badge"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Mark badge as viewed
     * Endpoint: POST /api/badges/:badgeId/view
     */
    fun markBadgeAsViewed(badgeId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.markBadgeAsViewed(badgeId)
            
            if (response.success) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Mark all badges as viewed
     * Endpoint: POST /api/badges/view-all
     */
    fun markAllBadgesAsViewed(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            val response = apiService.markAllBadgesAsViewed()
            
            if (response.success) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)
}
