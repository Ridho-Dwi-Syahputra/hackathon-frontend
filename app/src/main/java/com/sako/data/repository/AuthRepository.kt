package com.sako.data.repository

import com.google.gson.Gson
import com.sako.data.pref.UserModel
import com.sako.data.pref.UserPreference
import com.sako.data.remote.request.*
import com.sako.data.remote.response.*
import com.sako.data.remote.retrofit.ApiService
import com.sako.firebase.FirebaseHelper
import com.sako.utils.Resource
import com.sako.utils.BackendConnectionMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Repository for Authentication module
 * Handles all auth-related operations: login, register, profile, password management
 */
class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val connectionMonitor: BackendConnectionMonitor?
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
        } catch (_: Exception) {
            // Tetap logout dari lokal meskipun API gagal
        }
        userPreference.logout()
    }

    // ========== Authentication ==========

    fun register(fullName: String, email: String, password: String, fcmToken: String? = null): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            // Get FCM token if not provided
            val actualFcmToken = fcmToken ?: FirebaseHelper.generateFCMToken()
            
            val request = RegisterRequest(fullName, email, password, actualFcmToken)
            val response = apiService.register(request)

            response.data?.let { authData ->
                android.util.Log.d("AUTH_REPO", "🔍 Register response received")
                android.util.Log.d("AUTH_REPO", "  - AccessToken from backend: ${authData.accessToken}")
                android.util.Log.d("AUTH_REPO", "  - DatabaseToken from backend: ${authData.databaseToken}")
                android.util.Log.d("AUTH_REPO", "  - User ID: ${authData.user.id}")
                
                val userModel = UserModel(
                    id = authData.user.id,
                    fullName = authData.user.fullName,
                    email = authData.user.email,
                    totalXp = authData.user.totalXp,
                    status = authData.user.status,
                    userImageUrl = authData.user.userImageUrl,
                    accessToken = authData.accessToken ?: "",
                    databaseToken = authData.databaseToken ?: "",
                    fcmToken = authData.user.fcmToken,
                    isLogin = true
                )
                
                android.util.Log.d("AUTH_REPO", "💾 Saving session after register with accessToken: ${userModel.accessToken.take(20)}...")
                saveSession(userModel)
                android.util.Log.d("AUTH_REPO", "✅ Session saved after register")
                
                // Verify session was saved
                kotlinx.coroutines.delay(100)
                val savedSession = userPreference.getSession().first()
                android.util.Log.d("AUTH_REPO", "🔄 Verification - saved accessToken: ${savedSession.accessToken.take(20)}...")
                android.util.Log.d("AUTH_REPO", "🔄 Verification - saved databaseToken: ${savedSession.databaseToken}")
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun login(email: String, password: String, fcmToken: String? = null): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            // Get FCM token if not provided (optional for now)
            val actualFcmToken = try {
                fcmToken ?: FirebaseHelper.generateFCMToken()
            } catch (e: Exception) {
                null // Firebase might not be properly configured
            }
            
            val request = LoginRequest(email, password, actualFcmToken)
            val response = apiService.login(request)

            response.data?.let { authData ->
                val userModel = UserModel(
                    id = authData.user.id,
                    fullName = authData.user.fullName,
                    email = authData.user.email,
                    totalXp = authData.user.totalXp,
                    status = authData.user.status,
                    userImageUrl = authData.user.userImageUrl,
                    accessToken = authData.accessToken ?: "",
                    databaseToken = authData.databaseToken ?: "",
                    fcmToken = authData.user.fcmToken,
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

    fun autoLogin(): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.autoLogin()

            response.data?.let { authData ->
                // Update session dengan data terbaru dari server
                val userModel = UserModel(
                    id = authData.user.id,
                    fullName = authData.user.fullName,
                    email = authData.user.email,
                    totalXp = authData.user.totalXp,
                    status = authData.user.status,
                    userImageUrl = authData.user.userImageUrl,
                    accessToken = authData.accessToken ?: "",
                    databaseToken = authData.databaseToken ?: "",
                    fcmToken = authData.user.fcmToken,
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

    // ========== Profile Management ==========

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
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun updateProfile(fullName: String): Flow<Resource<UpdateProfileResponse>> = flow {
        emit(Resource.Loading)
        try {
            // Get current user email from session
            val currentUser = userPreference.getSession().first()
            val request = UpdateProfileRequest(fullName, currentUser.email)
            val response = apiService.updateProfile(request)

            // Update session
            response.data?.let { updateData ->
                userPreference.updateUserProfile(updateData.user.fullName, updateData.user.userImageUrl)
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
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
            response.data?.let { updateData ->
                userPreference.updateUserProfile(
                    updateData.user.fullName,
                    updateData.imageUrl ?: updateData.user.userImageUrl
                )
            }

            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(Resource.Error(parseErrorMessage(errorBody)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun changePassword(oldPassword: String, newPassword: String): Flow<Resource<ChangePasswordResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = ChangePasswordRequest(oldPassword, newPassword)
            val response = apiService.changePassword(request)
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
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            connectionMonitor: BackendConnectionMonitor?
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, userPreference, connectionMonitor)
            }.also { instance = it }
    }
}