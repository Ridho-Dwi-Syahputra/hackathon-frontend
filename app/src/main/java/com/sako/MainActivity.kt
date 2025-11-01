package com.sako

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sako.ui.components.ButtomNav
import com.sako.ui.navigation.SakoNavGraph
import com.sako.ui.navigation.Screen
import com.sako.ui.navigation.bottomNavRoutes
import com.sako.ui.theme.SakoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SakoTheme {
                SakoApp()
            }
        }
    }
}

/**
 * SAKO App - Main composable dengan Navigation dan Bottom Nav
 */
@Composable
fun SakoApp() {
    val navController = rememberNavController()

    // Check apakah current route harus menampilkan bottom nav
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNav) {
                ButtomNav(navController = navController)
            }
        }
    ) { innerPadding ->
        // SAKO Navigation Graph
        SakoNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = Screen.Splash.route // Mulai dari splash screen
        )
    }
}