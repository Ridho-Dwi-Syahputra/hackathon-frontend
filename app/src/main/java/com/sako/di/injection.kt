package com.sako.di

import android.content.Context
import com.sako.data.pref.UserPreference
import com.sako.data.pref.dataStore
import com.sako.data.remote.retrofit.ApiConfig
import com.sako.data.repository.AuthRepository
import com.sako.data.repository.MapRepository
import com.sako.data.repository.ProfileRepository
import com.sako.data.repository.SakoRepository
import com.sako.utils.BackendConnectionMonitor

object Injection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val connectionMonitor = BackendConnectionMonitor(context)
        return AuthRepository.getInstance(apiService, pref, connectionMonitor)
    }
    
    fun provideAuthRepository(userPreference: UserPreference): AuthRepository {
        val apiService = ApiConfig.getApiService(userPreference)
        // connectionMonitor tidak digunakan saat logout, jadi pass null
        return AuthRepository.getInstance(apiService, userPreference, null)
    }

    fun provideMapRepository(context: Context): MapRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return MapRepository.getInstance(apiService, pref)
    }
    
    fun provideProfileRepository(context: Context): ProfileRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return ProfileRepository(apiService, pref)
    }
    
    fun provideProfileRepository(userPreference: UserPreference): ProfileRepository {
        val apiService = ApiConfig.getApiService(userPreference)
        return ProfileRepository(apiService, userPreference)
    }

    fun provideRepository(context: Context): SakoRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return SakoRepository.getInstance(apiService, pref)
    }
}