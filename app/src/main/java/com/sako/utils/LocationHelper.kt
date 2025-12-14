package com.sako.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Helper class for location operations
 * Provides utilities for getting current location with proper error handling
 */
object LocationHelper {

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get current location using FusedLocationProviderClient
     * @return Location object with latitude and longitude
     * @throws LocationException if location cannot be obtained
     */
    suspend fun getCurrentLocation(context: Context): Location {
        if (!hasLocationPermission(context)) {
            throw LocationException("Location permission not granted")
        }

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { continuation ->
            try {
                // First try to get last known location (faster)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(location)
                        } else {
                            // If last location is null, get current location
                            getCurrentLocationFresh(fusedLocationClient, continuation)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // If last location fails, try to get current location
                        getCurrentLocationFresh(fusedLocationClient, continuation)
                    }
            } catch (e: SecurityException) {
                continuation.resumeWithException(
                    LocationException("Security exception: ${e.message}")
                )
            }

            continuation.invokeOnCancellation {
                // Clean up if coroutine is cancelled
            }
        }
    }

    /**
     * Get fresh current location (not cached)
     */
    @Suppress("MissingPermission")
    private fun getCurrentLocationFresh(
        fusedLocationClient: FusedLocationProviderClient,
        continuation: kotlinx.coroutines.CancellableContinuation<Location>
    ) {
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(
                        LocationException("Unable to get current location")
                    )
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(
                    LocationException("Failed to get location: ${exception.message}")
                )
            }
        } catch (e: SecurityException) {
            continuation.resumeWithException(
                LocationException("Security exception: ${e.message}")
            )
        }
    }

    /**
     * Format coordinates for display
     */
    fun formatCoordinates(latitude: Double, longitude: Double): String {
        return String.format("%.6f, %.6f", latitude, longitude)
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * @return distance in meters
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * Format distance for display
     */
    fun formatDistance(distanceInMeters: Double): String {
        return when {
            distanceInMeters < 1000 -> String.format("%.0f m", distanceInMeters)
            else -> String.format("%.2f km", distanceInMeters / 1000)
        }
    }
}

/**
 * Custom exception for location-related errors
 */
class LocationException(message: String) : Exception(message)

/**
 * Data class to hold user location
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun fromLocation(location: Location): UserLocation {
            return UserLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    fun toFormattedString(): String {
        return LocationHelper.formatCoordinates(latitude, longitude)
    }
}
