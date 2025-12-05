package com.sako.di

import android.content.Context
import com.sako.data.pref.UserPreference
import com.sako.data.pref.dataStore
import com.sako.data.remote.retrofit.ApiConfig
import com.sako.data.repository.AuthRepository
import com.sako.data.repository.MapRepository
import com.sako.data.repository.SakoRepository
import com.sako.firebase.notifications.map.MapNotificationManager
import com.sako.utils.BackendConnectionMonitor

object Injection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val connectionMonitor = BackendConnectionMonitor(context)
        return AuthRepository.getInstance(apiService, pref, connectionMonitor)
    }

    fun provideMapRepository(context: Context): MapRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return MapRepository.getInstance(apiService, pref)
    }

    fun provideRepository(context: Context): SakoRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return SakoRepository.getInstance(apiService, pref)
    }

    fun provideMapNotificationManager(context: Context): MapNotificationManager {
        val pref = UserPreference.getInstance(context.dataStore)
        val mapRepository = provideMapRepository(context)
        return MapNotificationManager(context, mapRepository, pref)
    }
}