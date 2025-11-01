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

    // ... (kode sebelumnya tetap sama)

    // ========== Quiz Attempt ========== (UPDATED)

    fun startQuiz(levelId: String): Flow<Resource<QuizStartResponse>> = flow {
        emit(Resource.Loading)
        try {
            val request = CheckinQuizRequest(levelId)
            val response = apiService.startQuiz(request)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
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
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ... (kode lainnya tetap sama)

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