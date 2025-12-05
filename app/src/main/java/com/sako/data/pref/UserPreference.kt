package com.sako.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.id
            preferences[FULL_NAME_KEY] = user.fullName
            preferences[EMAIL_KEY] = user.email
            preferences[TOTAL_XP_KEY] = user.totalXp
            preferences[STATUS_KEY] = user.status
            preferences[USER_IMAGE_URL_KEY] = user.userImageUrl ?: ""
            preferences[ACCESS_TOKEN_KEY] = user.accessToken
            preferences[DATABASE_TOKEN_KEY] = user.databaseToken
            preferences[FCM_TOKEN_KEY] = user.fcmToken ?: ""
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                id = preferences[ID_KEY] ?: "",
                fullName = preferences[FULL_NAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                totalXp = preferences[TOTAL_XP_KEY] ?: 0,
                status = preferences[STATUS_KEY] ?: "active",
                userImageUrl = preferences[USER_IMAGE_URL_KEY]?.takeIf { it.isNotEmpty() },
                accessToken = preferences[ACCESS_TOKEN_KEY] ?: "",
                databaseToken = preferences[DATABASE_TOKEN_KEY] ?: "",
                fcmToken = preferences[FCM_TOKEN_KEY]?.takeIf { it.isNotEmpty() },
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun updateUserProfile(fullName: String, userImageUrl: String?) {
        dataStore.edit { preferences ->
            preferences[FULL_NAME_KEY] = fullName
            if (userImageUrl != null) {
                preferences[USER_IMAGE_URL_KEY] = userImageUrl
            }
        }
    }

    suspend fun updateTotalXp(totalXp: Int) {
        dataStore.edit { preferences ->
            preferences[TOTAL_XP_KEY] = totalXp
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID_KEY = stringPreferencesKey("id")
        private val FULL_NAME_KEY = stringPreferencesKey("full_name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOTAL_XP_KEY = intPreferencesKey("total_xp")
        private val STATUS_KEY = stringPreferencesKey("status")
        private val USER_IMAGE_URL_KEY = stringPreferencesKey("user_image_url")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val DATABASE_TOKEN_KEY = stringPreferencesKey("database_token")
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("is_login")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}