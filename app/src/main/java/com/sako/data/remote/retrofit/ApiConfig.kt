package com.sako.data.remote.retrofit

import com.sako.data.pref.UserPreference
import com.sako.utils.LoggingInterceptor
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
    // - Emulator: http://10.0.2.2:5000/api/
    // - Physical Device (same WiFi): http://[IP_KOMPUTER]:5000/api/ (cek dengan ipconfig)
    // - ngrok (bypass WiFi isolation): https://your-subdomain.ngrok-free.app/api/
    // - Production: https://your-backend-domain.com/api/
    
    // ✅ Using ngrok for real device testing
    // IMPORTANT: Must end with /api/ (with trailing slash)
    private const val BASE_URL = "https://beryl-irradiant-leone.ngrok-free.dev/api/"


    fun getApiService(userPreference: UserPreference): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
        
        // Custom logging interceptor untuk debugging yang lebih detail
        val customLoggingInterceptor = LoggingInterceptor()

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            
            // Skip auth HANYA untuk endpoint login/register/auto-login (tidak butuh token)
            val url = req.url.toString()
            val isPublicAuthEndpoint = url.contains("/auth/login") || 
                                       url.contains("/auth/register") || 
                                       url.contains("/auth/auto-login")
            
            if (isPublicAuthEndpoint) {
                android.util.Log.d("AUTH_INTERCEPTOR", "⏭️ Skipping auth for public endpoint: ${req.url}")
                return@Interceptor chain.proceed(req.newBuilder().addHeader("Accept", "application/json").build())
            }
            
            // Simple retry mechanism for DataStore sync
            var userSession = runBlocking {
                userPreference.getSession().first()
            }
            
            // If token empty but logged in, retry once with short delay
            if (userSession.isLogin && userSession.accessToken.isEmpty() && userSession.databaseToken.isEmpty()) {
                runBlocking {
                    Thread.sleep(150) // Short delay for DataStore sync
                    userSession = userPreference.getSession().first()
                }
            }

            // Debug yang lebih detail untuk troubleshooting
            android.util.Log.d("AUTH_INTERCEPTOR", "🔐 Session Details for ${req.url}:")
            android.util.Log.d("AUTH_INTERCEPTOR", "   - Login Status: ${userSession.isLogin}")
            android.util.Log.d("AUTH_INTERCEPTOR", "   - AccessToken: '${userSession.accessToken}' (length: ${userSession.accessToken.length})")
            android.util.Log.d("AUTH_INTERCEPTOR", "   - DatabaseToken: '${userSession.databaseToken}' (length: ${userSession.databaseToken.length})")
            android.util.Log.d("AUTH_INTERCEPTOR", "   - FCM Token: ${if (userSession.fcmToken.isNullOrEmpty()) "None" else "Present"}")

            val requestHeaders = req.newBuilder()
                .addHeader("Accept", "application/json")
                .apply {
                    // Prioritas: gunakan access_token, fallback ke database_token jika ada
                    when {
                        userSession.accessToken.isNotEmpty() && userSession.accessToken.isNotBlank() -> {
                            addHeader("Authorization", "Bearer ${userSession.accessToken}")
                            android.util.Log.d("AUTH_INTERCEPTOR", "📤 Using access token for ${req.url}")
                        }
                        userSession.databaseToken.isNotEmpty() && userSession.databaseToken.isNotBlank() -> {
                            addHeader("Authorization", "Bearer ${userSession.databaseToken}")
                            android.util.Log.d("AUTH_INTERCEPTOR", "📤 Using database token for ${req.url}")
                        }
                        else -> {
                            android.util.Log.w("AUTH_INTERCEPTOR", "⚠️ No valid token available for ${req.url}")
                            android.util.Log.w("AUTH_INTERCEPTOR", "   - AccessToken empty: ${userSession.accessToken.isEmpty()}")
                            android.util.Log.w("AUTH_INTERCEPTOR", "   - DatabaseToken empty: ${userSession.databaseToken.isEmpty()}")
                            android.util.Log.w("AUTH_INTERCEPTOR", "   - Login status: ${userSession.isLogin}")
                            android.util.Log.w("AUTH_INTERCEPTOR", "   - Thread: ${Thread.currentThread().name}")
                        }
                    }
                }
                .build()

            chain.proceed(requestHeaders)
        }

        // Authenticator untuk auto-refresh token saat 401
        val tokenAuthenticator = okhttp3.Authenticator { _, response ->
            // Hindari infinite loop - jika sudah 3x gagal, stop
            if (response.request.header("Token-Retry-Count")?.toIntOrNull() ?: 0 >= 3) {
                android.util.Log.e("AUTH_AUTHENTICATOR", "❌ Max retry reached, giving up")
                return@Authenticator null
            }

            // Skip HANYA untuk public auth endpoints (login/register/auto-login)
            val url = response.request.url.toString()
            val isPublicAuthEndpoint = url.contains("/auth/login") || 
                                       url.contains("/auth/register") || 
                                       url.contains("/auth/auto-login")
            
            if (isPublicAuthEndpoint) {
                android.util.Log.d("AUTH_AUTHENTICATOR", "⏭️ Skipping refresh for public endpoint")
                return@Authenticator null
            }

            android.util.Log.d("AUTH_AUTHENTICATOR", "🔄 Got 401, attempting token refresh...")

            // Get current session
            val userSession = runBlocking {
                userPreference.getSession().first()
            }

            // Jika tidak ada database token, tidak bisa refresh
            if (userSession.databaseToken.isEmpty() || userSession.databaseToken.isBlank()) {
                android.util.Log.e("AUTH_AUTHENTICATOR", "❌ No database token available for refresh")
                return@Authenticator null
            }

            try {
                // Call auto-login endpoint dengan database token
                android.util.Log.d("AUTH_AUTHENTICATOR", "📡 Calling auto-login endpoint...")
                
                val autoLoginClient = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()
                
                val autoLoginRequest = okhttp3.Request.Builder()
                    .url("${BASE_URL}auth/auto-login")
                    .header("Authorization", "Bearer ${userSession.databaseToken}")
                    .header("Accept", "application/json")
                    .get()
                    .build()

                val autoLoginResponse = autoLoginClient.newCall(autoLoginRequest).execute()

                if (autoLoginResponse.isSuccessful) {
                    val responseBody = autoLoginResponse.body?.string()
                    android.util.Log.d("AUTH_AUTHENTICATOR", "✅ Auto-login response: ${responseBody?.take(200)}")

                    // Parse response untuk mendapatkan access_token baru
                    val gson = com.google.gson.Gson()
                    val authResponse = gson.fromJson(responseBody, com.sako.data.remote.response.AuthResponse::class.java)

                    if (authResponse.success && authResponse.data != null) {
                        val newAccessToken = authResponse.data.accessToken ?: ""
                        
                        if (newAccessToken.isNotEmpty()) {
                            android.util.Log.d("AUTH_AUTHENTICATOR", "🔑 Got new access token: ${newAccessToken.take(20)}...")
                            
                            // Update session dengan token baru
                            runBlocking {
                                userPreference.saveSession(
                                    userSession.copy(accessToken = newAccessToken)
                                )
                            }

                            android.util.Log.d("AUTH_AUTHENTICATOR", "💾 Session updated with new token")

                            // Increment retry count
                            val retryCount = (response.request.header("Token-Retry-Count")?.toIntOrNull() ?: 0) + 1

                            // Retry original request dengan token baru
                            return@Authenticator response.request.newBuilder()
                                .header("Authorization", "Bearer $newAccessToken")
                                .header("Token-Retry-Count", retryCount.toString())
                                .build()
                        } else {
                            android.util.Log.e("AUTH_AUTHENTICATOR", "❌ Auto-login returned empty token")
                        }
                    } else {
                        android.util.Log.e("AUTH_AUTHENTICATOR", "❌ Auto-login failed: ${authResponse.message}")
                    }
                } else {
                    android.util.Log.e("AUTH_AUTHENTICATOR", "❌ Auto-login HTTP ${autoLoginResponse.code}: ${autoLoginResponse.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AUTH_AUTHENTICATOR", "❌ Auto-login exception: ${e.message}", e)
            }

            // Jika sampai sini, refresh gagal - return null supaya tidak retry
            null
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(customLoggingInterceptor) // Custom logging untuk debugging integrasi
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator) // Tambahkan authenticator untuk auto-refresh
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