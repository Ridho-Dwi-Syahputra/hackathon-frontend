package com.sako.data.remote.retrofit

import com.sako.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    // Konfigurasi Base URL
    // Gunakan salah satu sesuai kebutuhan:
    // - Emulator: http://10.0.2.2:3000/api/
    // - Physical Device (same WiFi): http://[IP_KOMPUTER]:3000/api/ (cek dengan ipconfig)
    // - ngrok (bypass WiFi isolation): https://your-subdomain.ngrok-free.dev/api/
    // - Production: https://your-backend-domain.com/api/
    
    private const val BASE_URL = "https://lowery-marcus-nonreputably.ngrok-free.dev/api/"


    fun getApiService(userPreference: UserPreference): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val token = runBlocking {
                userPreference.getSession().first().token
            }

            val requestHeaders = req.newBuilder()
                .addHeader("Accept", "application/json")
                .apply {
                    if (token.isNotEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }
                .build()

            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}