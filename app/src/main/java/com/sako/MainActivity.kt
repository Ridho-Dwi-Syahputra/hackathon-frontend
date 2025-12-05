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
            val notificationType = extras.getString("type")
            val placeId = extras.getString("placeId")
            val placeName = extras.getString("placeName")
            val reviewId = extras.getString("reviewId")
            
            FirebaseDebugUtils.logInfo("Notification intent received: type=$notificationType, placeId=$placeId")
            
            // TODO: Navigate to specific screen based on notification type
            // This will be handled by the navigation system in SakoNavGraph
            when (notificationType) {
                "review_added" -> {
                    FirebaseDebugUtils.logInfo("Handling review_added notification for place: $placeName")
                    // Navigation will be handled in SakoNavGraph
                }
                "place_visited" -> {
                    FirebaseDebugUtils.logInfo("Handling place_visited notification for place: $placeName")
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