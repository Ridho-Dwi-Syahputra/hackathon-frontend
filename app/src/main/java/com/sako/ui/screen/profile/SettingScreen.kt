package com.sako.ui.screen.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sako.R
import com.sako.ui.theme.*
import com.sako.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    
    // Notification preferences state from ViewModel
    val notificationPreferences = uiState.notificationPreferences
    
    var mapNotificationsEnabled by remember { 
        mutableStateOf(notificationPreferences?.mapNotifications?.reviewAdded ?: true) 
    }
    var videoNotificationsEnabled by remember { 
        mutableStateOf(notificationPreferences?.videoNotifications ?: true) 
    }
    var quizNotificationsEnabled by remember { 
        mutableStateOf(notificationPreferences?.quizNotifications ?: true) 
    }
    
    // Logout dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Load user profile and notification preferences on first composition
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.loadNotificationPreferences()
    }
    
    // Update local state when preferences loaded from backend
    LaunchedEffect(notificationPreferences) {
        notificationPreferences?.let { prefs ->
            mapNotificationsEnabled = prefs.mapNotifications.reviewAdded
            videoNotificationsEnabled = prefs.videoNotifications
            quizNotificationsEnabled = prefs.quizNotifications
        }
    }

    // Scroll state for collapsing effect
    val scrollState = rememberScrollState()
    val headerHeight = 200.dp
    val minHeaderHeight = 80.dp
    val scrollProgress = min(1f, scrollState.value / (headerHeight.value - minHeaderHeight.value))
    
    val animatedHeaderHeight by animateFloatAsState(
        targetValue = headerHeight.value - (scrollProgress * (headerHeight.value - minHeaderHeight.value)),
        label = "headerHeight"
    )
    
    val animatedTitleSize by animateFloatAsState(
        targetValue = 24f - (scrollProgress * 4f),
        label = "titleSize"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f - scrollProgress,
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Collapsing Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedHeaderHeight.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                // Back button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Profile info in header
                uiState.userData?.let { profile ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .alpha(animatedAlpha)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Profile Image - scales with scroll
                        val imageSize by animateFloatAsState(
                            targetValue = 80f - (scrollProgress * 30f),
                            label = "imageSize"
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(imageSize.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            if (!profile.userImageUrl.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(profile.userImageUrl),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile),
                                    contentDescription = "Default Profile",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        if (scrollProgress < 0.7f) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = profile.fullName.uppercase(),
                                fontSize = animatedTitleSize.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = profile.email,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                // Title when collapsed
                if (scrollProgress > 0.5f) {
                    Text(
                        text = "Setting",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 56.dp)
                            .alpha((scrollProgress - 0.5f) * 2f)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // Notification Preferences Section
                SettingSection(
                    title = "Izinkan Notifikasi"
                ) {
                    NotificationToggleItem(
                        iconRes = R.drawable.map,
                        title = "Modul Map",
                        isEnabled = mapNotificationsEnabled,
                        onToggle = { 
                            mapNotificationsEnabled = it
                            // Update backend
                            val updatedPrefs = (notificationPreferences ?: com.sako.data.remote.request.NotificationPreferences()).copy(
                                mapNotifications = com.sako.data.remote.request.MapNotifications(
                                    reviewAdded = it,
                                    placeVisited = it
                                )
                            )
                            viewModel.updateNotificationPreferences(updatedPrefs)
                        }
                    )
                    
                    NotificationToggleItem(
                        iconRes = R.drawable.video,
                        title = "Modul Video",
                        isEnabled = videoNotificationsEnabled,
                        onToggle = { 
                            videoNotificationsEnabled = it
                            // Update backend
                            val updatedPrefs = (notificationPreferences ?: com.sako.data.remote.request.NotificationPreferences()).copy(
                                videoNotifications = it
                            )
                            viewModel.updateNotificationPreferences(updatedPrefs)
                        }
                    )
                    
                    NotificationToggleItem(
                        icon = Icons.Default.Quiz,
                        title = "Modul Kuis",
                        isEnabled = quizNotificationsEnabled,
                        onToggle = { 
                            quizNotificationsEnabled = it
                            // Update backend
                            val updatedPrefs = (notificationPreferences ?: com.sako.data.remote.request.NotificationPreferences()).copy(
                                quizNotifications = it
                            )
                            viewModel.updateNotificationPreferences(updatedPrefs)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Keamanan Section
                SettingSection(
                    title = "Keamanan"
                ) {
                    SettingMenuItem(
                        iconRes = R.drawable.key,
                        title = "Ganti Kata Sandi",
                        onClick = onNavigateToChangePassword
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tentang Aplikasi Section
                SettingSection(
                    title = "Tentang Aplikasi"
                ) {
                    SettingMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        onClick = onNavigateToAbout
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keluar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Konfirmasi Keluar",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Apakah Anda yakin ingin keluar dari aplikasi?",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            scope.launch {
                                viewModel.logout()
                                onLogoutSuccess()
                            }
                        },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Keluar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                iconRes != null -> {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun SettingMenuItem(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                iconRes != null -> {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
