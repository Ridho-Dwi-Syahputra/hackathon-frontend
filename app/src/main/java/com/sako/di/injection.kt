package com.sako.di

import android.content.Context
import com.sako.data.pref.UserPreference
import com.sako.data.pref.dataStore
import com.sako.data.remote.retrofit.ApiConfig
import com.sako.data.repository.SakoRepository

object Injection {

    fun provideRepository(context: Context): SakoRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return SakoRepository.getInstance(apiService, pref)
    }
}