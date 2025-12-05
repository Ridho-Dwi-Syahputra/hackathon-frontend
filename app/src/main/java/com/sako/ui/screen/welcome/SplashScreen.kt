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

    // Cek session setelah delay
    LaunchedEffect(key1 = true) {
        delay(2000L) // 2 detik
        
        // Cek apakah user sudah login
        if (userSession?.isLogin == true && userSession?.accessToken?.isNotEmpty() == true) {
            onNavigateToHome()
        } else {
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
            // Logo SAKO
            Image(
                painter = painterResource(id = R.drawable.sako),
                contentDescription = "Logo SAKO",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "SAKO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gamifikasi Pelestarian Budaya untuk Mendorong Pariwisata Berkelanjutan.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Indicator
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}