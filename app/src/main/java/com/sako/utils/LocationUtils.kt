package com.sako.utils

import android.location.Location

object LocationUtils {

    /**
     * Calculate distance between two points in meters using Haversine formula
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }

    /**
     * Check if user is within acceptable distance for check-in
     */
    fun isWithinCheckInRange(
        userLat: Double,
        userLon: Double,
        placeLat: Double,
        placeLon: Double,
        maxDistance: Int = Constants.MAX_CHECKIN_DISTANCE
    ): Boolean {
        val distance = calculateDistance(userLat, userLon, placeLat, placeLon)
        return distance <= maxDistance
    }

    /**
     * Format distance for display
     */
    fun formatDistance(distanceInMeters: Double): String {
        return when {
            distanceInMeters < 1000 -> "${distanceInMeters.toInt()} m"
            else -> String.format("%.1f km", distanceInMeters / 1000)
        }
    }
}