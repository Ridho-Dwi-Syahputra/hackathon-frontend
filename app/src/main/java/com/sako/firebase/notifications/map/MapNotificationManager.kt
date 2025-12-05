package com.sako.firebase.notifications.map

import android.content.Context
import android.util.Log
import com.sako.data.pref.UserPreference
import com.sako.data.repository.MapRepository
import com.sako.firebase.FirebaseConfig
import com.sako.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Map Notification Manager
 * Handles notification preferences for map module
 * Sesuai dengan backend mapNotifikasiController.js
 */
class MapNotificationManager(
    private val context: Context,
    private val mapRepository: MapRepository,
    private val userPreference: UserPreference
) {
    
    companion object {
        private const val TAG = "MAP_NOTIFICATION"
        
        // Notification preference keys (sesuai backend)
        const val PREF_REVIEW_ADDED = "review_added"
        const val PREF_PLACE_VISITED = "place_visited"
    }

    /**
     * Get current notification preferences for map module
     */
    suspend fun getMapNotificationPreferences(): MapNotificationPreferences {
        return try {
            val user = userPreference.getSession().first()
            
            // Default preferences if none set (sesuai backend default)
            MapNotificationPreferences(
                reviewAdded = true,  // Default enabled
                placeVisited = true  // Default enabled
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting preferences: ${e.message}")
            MapNotificationPreferences(
                reviewAdded = true,
                placeVisited = true
            )
        }
    }

    /**
     * Update notification preferences for map module
     * Sync dengan backend melalui API
     */
    fun updateMapNotificationPreferences(preferences: MapNotificationPreferences): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            val user = userPreference.getSession().first()
            
            if (!user.isLogin) {
                emit(Resource.Error("User tidak login"))
                return@flow
            }

            // Prepare preferences map for backend
            val preferencesMap = mapOf(
                PREF_REVIEW_ADDED to preferences.reviewAdded,
                PREF_PLACE_VISITED to preferences.placeVisited
            )

            // TODO: Send to backend via API
            // val result = mapRepository.updateNotificationPreferences(preferencesMap)
            
            // For now, save locally
            // userPreference.saveMapNotificationPreferences(preferences)
            
            // Subscribe/unsubscribe from Firebase topics based on preferences
            if (preferences.reviewAdded || preferences.placeVisited) {
                // Keep subscription if any preference is enabled
                Log.d(TAG, "🔔 Keeping map notification subscription")
            } else {
                // Unsubscribe if all disabled
                FirebaseConfig.unsubscribeFromMapNotifications()
                Log.d(TAG, "🔕 Unsubscribed from map notifications")
            }

            emit(Resource.Success(true))
            Log.d(TAG, "✅ Map notification preferences updated: $preferences")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating preferences: ${e.message}")
            emit(Resource.Error(e.message ?: "Gagal update preferences"))
        }
    }

    /**
     * Handle review added notification
     */
    fun logReviewAddedNotification(placeName: String, rating: Int) {
        Log.d(TAG, "⭐ Review notification logged: $placeName ($rating stars)")
        
        // Log untuk debugging integrasi dengan backend
        val message = "Review added notification untuk $placeName dengan rating $rating bintang"
        Log.i(TAG, message)
    }

    /**
     * Handle place visited notification
     */
    fun logPlaceVisitedNotification(placeName: String, visitType: String = "qr_scan") {
        Log.d(TAG, "🏛️ Place visit notification logged: $placeName via $visitType")
        
        // Log untuk debugging integrasi dengan backend
        val message = "Place visited notification untuk $placeName melalui $visitType"
        Log.i(TAG, message)
    }

    /**
     * Check if FCM token is ready and sent to backend
     */
    suspend fun ensureFCMTokenSynced(): Boolean {
        return try {
            val fcmToken = FirebaseConfig.getFCMToken()
            if (fcmToken != null) {
                Log.d(TAG, "🎯 FCM Token ready for backend sync")
                // TODO: Send to backend if not already sent
                true
            } else {
                Log.w(TAG, "⚠️ FCM Token not available")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking FCM token: ${e.message}")
            false
        }
    }
}

/**
 * Data class for map notification preferences
 */
data class MapNotificationPreferences(
    val reviewAdded: Boolean = true,
    val placeVisited: Boolean = true
) {
    override fun toString(): String {
        return "MapNotificationPreferences(reviewAdded=$reviewAdded, placeVisited=$placeVisited)"
    }
}