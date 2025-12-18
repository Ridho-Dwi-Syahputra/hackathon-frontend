package com.sako

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sako.data.pref.UserPreference
import com.sako.data.remote.retrofit.ApiConfig
import com.sako.data.repository.SakoRepository
import com.sako.firebase.FirebaseHelper
import com.sako.ui.components.BottomNav
import com.sako.ui.navigation.SakoNavGraph
import com.sako.ui.navigation.Screen
import com.sako.ui.navigation.bottomNavRoutes
import com.sako.ui.theme.SakoTheme
import com.sako.viewmodel.ViewModelFactory

// DataStore instance
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class MainActivity : ComponentActivity() {
    
    // Permission launcher for POST_NOTIFICATIONS
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "✅ Notification permission granted")
        } else {
            android.util.Log.w("MainActivity", "⚠️ Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+
        requestNotificationPermission()
        
        // Handle notification intents when app is opened from notification
        handleNotificationIntent(intent)
        
        setContent {
            android.util.Log.d("MainActivity", "🚀 Setting content - SakoTheme")
            SakoTheme {
                android.util.Log.d("MainActivity", "🚀 Calling SakoApp")
                SakoApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle notification intents when app is already running
        handleNotificationIntent(intent)
    }

    private fun requestNotificationPermission() {
        // Only request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    android.util.Log.d("MainActivity", "✅ Notification permission already granted")
                }
                else -> {
                    android.util.Log.d("MainActivity", "📲 Requesting notification permission")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            android.util.Log.d("MainActivity", "ℹ️ Notification permission not required for Android < 13")
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val notificationType = extras.getString("type")
            val placeId = extras.getString("placeId")
            val placeName = extras.getString("placeName")
            val reviewId = extras.getString("reviewId")
            
            android.util.Log.d("MainActivity", "Notification intent received: type=$notificationType, placeId=$placeId")
            
            // TODO: Navigate to specific screen based on notification type
            // This will be handled by the navigation system in SakoNavGraph
            when (notificationType) {
                "review_added" -> {
                    android.util.Log.d("MainActivity", "Handling review_added notification for place: $placeName")
                    // Navigation will be handled in SakoNavGraph
                }
                "place_visited" -> {
                    android.util.Log.d("MainActivity", "Handling place_visited notification for place: $placeName")
                    // Navigation will be handled in SakoNavGraph
                }
            }
        }
    }
}

/**
 * SAKO App - Main composable dengan Navigation dan Bottom Nav
 * Integrated dengan Firebase Cloud Messaging untuk notifikasi peta
 */
@Composable
fun SakoApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Setup UserPreference dan ViewModelFactory with remember to prevent recomposition overhead
    val userPreference = androidx.compose.runtime.remember {
        UserPreference.getInstance(context.dataStore)
    }
    val viewModelFactory = androidx.compose.runtime.remember {
        ViewModelFactory.getInstance(context)
    }

    // Firebase setup with automatic token handling
    LaunchedEffect(Unit) {
        try {
            // Subscribe to map notifications
            FirebaseHelper.subscribeToTopic("map_notifications")
            android.util.Log.d("SakoApp", "Successfully subscribed to map notifications")
        } catch (e: Exception) {
            android.util.Log.e("SakoApp", "Failed to subscribe to map notifications", e)
        }
    }

    // Check apakah current route harus menampilkan bottom nav
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNav) {
                BottomNav(navController = navController)
            }
        }
    ) { innerPadding ->
        // SAKO Navigation Graph
        SakoNavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            userPreference = userPreference,
            modifier = Modifier.padding(innerPadding)
        )
    }
}