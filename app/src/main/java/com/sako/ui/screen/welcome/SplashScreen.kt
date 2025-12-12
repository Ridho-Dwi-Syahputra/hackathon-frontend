package com.sako.ui.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.R
import com.sako.data.pref.UserPreference
import com.sako.ui.theme.SakoPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    userPreference: UserPreference
) {
    val userSession by userPreference.getSession().collectAsState(initial = null)

    // Log untuk debugging
    LaunchedEffect(key1 = true) {
        android.util.Log.d("SplashScreen", "🔄 SplashScreen dimulai")
        delay(2000L) // 2 detik
        
        android.util.Log.d("SplashScreen", "🔍 Checking session: isLogin=${userSession?.isLogin}, token=${userSession?.accessToken}")
        
        // Cek apakah user sudah login
        if (userSession?.isLogin == true && userSession?.accessToken?.isNotEmpty() == true) {
            android.util.Log.d("SplashScreen", "🏠 Navigating to Home")
            onNavigateToHome()
        } else {
            android.util.Log.d("SplashScreen", "📝 Navigating to Register")
            onNavigateToRegister()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SakoPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // App Name tanpa image dulu untuk debugging
            Text(
                text = "SAKO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Loading...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Indicator
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}