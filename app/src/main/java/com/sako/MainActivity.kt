package com.sako

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sako.data.pref.UserPreference
import com.sako.data.remote.retrofit.ApiConfig
import com.sako.data.repository.SakoRepository
import com.sako.firebase.FirebaseConfig
import com.sako.firebase.FirebaseDebugUtils
import com.sako.ui.components.BottomNav
import com.sako.ui.navigation.SakoNavGraph
import com.sako.ui.navigation.Screen
import com.sako.ui.navigation.bottomNavRoutes
import com.sako.ui.theme.SakoTheme
import com.sako.viewmodel.ViewModelFactory

// DataStore instance
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firebase Configuration
        initializeFirebase()
        
        // Handle notification intents when app is opened from notification
        handleNotificationIntent(intent)
        
        setContent {
            SakoTheme {
                SakoApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle notification intents when app is already running
        handleNotificationIntent(intent)
    }

    private fun initializeFirebase() {
        try {
            FirebaseConfig.initialize(this)
            FirebaseDebugUtils.logInfo("Firebase initialized successfully in MainActivity")
        } catch (e: Exception) {
            FirebaseDebugUtils.logError("Failed to initialize Firebase in MainActivity", e)
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val navigationTarget = extras.getString("navigation_target")
            val videoId = extras.getString("video_id")
            val videoTitle = extras.getString("video_title")
            
            FirebaseDebugUtils.logInfo("Notification intent received: target=$navigationTarget")
            
            // Hanya handle video notifications
            when (navigationTarget) {
                "VideoFavoriteScreen" -> {
                    FirebaseDebugUtils.logInfo("Handling video_favorited notification: $videoTitle")
                    pendingNavigationTarget = navigationTarget
                    pendingVideoId = videoId
                }
            }
        }
    }
    
    companion object {
        var pendingNavigationTarget: String? = null
        var pendingVideoId: String? = null
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

    // Setup UserPreference dan ViewModelFactory
    val userPreference = UserPreference.getInstance(context.dataStore)
    val viewModelFactory = ViewModelFactory(context)

    // Firebase setup with automatic token handling
    LaunchedEffect(Unit) {
        try {
            // Subscribe to map notifications
            FirebaseConfig.subscribeToMapNotifications()
            FirebaseDebugUtils.logInfo("Successfully subscribed to map notifications")
        } catch (e: Exception) {
            FirebaseDebugUtils.logError("Failed to subscribe to map notifications", e)
        }
    }

    // Handle pending video notification navigation
    LaunchedEffect(navController) {
        MainActivity.pendingNavigationTarget?.let { target ->
            try {
                // Tunggu sebentar untuk memastikan app sudah ready
                kotlinx.coroutines.delay(1000)
                
                // Cek apakah user sudah login
                userPreference.getSession().collect { session ->
                    if (session.isLogin) {
                        // User sudah login, bisa navigate
                        when (target) {
                            "VideoFavoriteScreen" -> {
                                // Navigate langsung tanpa popUpTo
                                navController.navigate(Screen.VideoFavorite.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                FirebaseDebugUtils.logInfo("Navigated to VideoFavoriteScreen from notification")
                            }
                        }
                    } else {
                        // User belum login, tidak navigate
                        FirebaseDebugUtils.logInfo("User not logged in, skipping notification navigation")
                    }
                    
                    // Clear pending navigation
                    MainActivity.pendingNavigationTarget = null
                    MainActivity.pendingVideoId = null
                }
            } catch (e: Exception) {
                FirebaseDebugUtils.logError("Navigation failed from notification", e)
                // Clear pending navigation on error
                MainActivity.pendingNavigationTarget = null
                MainActivity.pendingVideoId = null
            }
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