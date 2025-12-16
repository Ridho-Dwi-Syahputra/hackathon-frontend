package com.sako.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sako.R
import com.sako.data.model.LevelInfo
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.FullscreenImageViewer
import com.sako.ui.components.SakoLoadingScreen
import com.sako.ui.components.SakoSpacing
import com.sako.ui.components.VerticalSpacer
import com.sako.ui.navigation.Screen
import com.sako.ui.theme.*
import com.sako.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showImagePicker by remember { mutableStateOf(false) }
    var showImageOptions by remember { mutableStateOf(false) }
    var showFullscreenImage by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Show fullscreen image viewer
    if (showFullscreenImage && !uiState.userData?.userImageUrl.isNullOrEmpty()) {
        FullscreenImageViewer(
            imageUrl = uiState.userData?.userImageUrl,
            onDismiss = { showFullscreenImage = false }
        )
    }

    // Show image options dialog (untuk saat sudah ada foto)
    if (showImageOptions) {
        ImageOptionsDialog(
            imageUrl = uiState.userData?.userImageUrl,
            onDismiss = { showImageOptions = false },
            onViewPhoto = {
                showImageOptions = false
                showFullscreenImage = true
            },
            onChangePhoto = {
                showImageOptions = false
                showImagePicker = true
            }
        )
    }

    // Show image picker dialog
    if (showImagePicker) {
        ImageSourcePickerDialog(
            onDismiss = { showImagePicker = false },
            onImageSelected = { imageFile ->
                viewModel.updateProfileImage(imageFile)
                showImagePicker = false
            }
        )
    }

    // Show snackbar when update is successful dengan auto-dismiss 4 detik
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess && !uiState.updateMessage.isNullOrEmpty()) {
            snackbarMessage = uiState.updateMessage ?: ""
            showSnackbar = true
            // Auto dismiss setelah 4 detik
            kotlinx.coroutines.delay(4000)
            showSnackbar = false
        }
    }

    // Reload profile data when returning from edit screens
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                onSettingsClick = {
                    navController.navigate(Screen.Setting.route)
                }
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            BackgroundImage(alpha = 0.3f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
            when {
                uiState.isLoading -> {
                    SakoLoadingScreen(
                        message = "Memuat profil...",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadUserProfile() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.userData != null -> {
                    ProfileContent(
                        userData = uiState.userData!!,
                        stats = uiState.stats,
                        levelInfo = uiState.levelInfo,
                        isUploadingImage = uiState.isUploadingImage,
                        localImageFile = uiState.localImageFile,  // Pass local file untuk preview
                        onEditProfileImageClick = {
                            showImagePicker = true
                        },
                        onViewPhotoClick = {
                            showImageOptions = true
                        },
                        onImageLoaded = {
                            // Clear local file setelah URL berhasil dimuat
                            viewModel.clearLocalImageFile()
                        },
                        onEditProfileClick = {
                            navController.navigate(Screen.EditProfile.route)
                        },
                        onBadgesClick = {
                            navController.navigate(Screen.BadgeList.route)
                        },
                        onChangePasswordClick = {
                            navController.navigate(Screen.ChangePassword.route)
                        }
                    )
                }
            }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onSettingsClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Detail Profil",
                color = onPrimaryColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = onPrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        }
    }
}

@Composable
private fun ProfileContent(
    userData: com.sako.data.remote.response.ProfileUserData,
    stats: com.sako.data.remote.response.UserStats?,
    levelInfo: LevelInfo?,
    isUploadingImage: Boolean,
    localImageFile: java.io.File?,  // Local file untuk preview instant
    onEditProfileImageClick: () -> Unit,
    onViewPhotoClick: () -> Unit,
    onImageLoaded: () -> Unit,  // Callback saat URL loaded
    onEditProfileClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Image Section
        ProfileImageSection(
            imageUrl = userData.userImageUrl,
            localImageFile = localImageFile,  // Pass local file
            isUploading = isUploadingImage,
            onImageClick = onEditProfileImageClick,
            onViewPhotoClick = if (!userData.userImageUrl.isNullOrEmpty()) onViewPhotoClick else null,
            onImageLoaded = onImageLoaded  // Pass callback untuk clear local file
        )

        VerticalSpacer(height = SakoSpacing.ExtraLarge)

        // User Info Card
        UserInfoCard(
            fullName = userData.fullName,
            userId = userData.id,
            email = userData.email,
            createdAt = userData.createdAt,
            totalPoints = stats?.totalPoints ?: 0,
            totalXp = userData.totalXp,
            levelInfo = levelInfo
        )

        VerticalSpacer(height = SakoSpacing.ExtraLarge)

        // Stats Summary
        stats?.let {
            StatsSection(
                totalAttempts = it.totalAttempts,
                completedLevels = it.completedLevels,
                visitedPlaces = it.visitedPlaces
            )
        }

        VerticalSpacer(height = SakoSpacing.ExtraLarge)
        
        // Action Buttons
        ActionButtonsSection(
            onEditProfileClick = onEditProfileClick,
            onBadgesClick = onBadgesClick,
            onChangePasswordClick = onChangePasswordClick
        )
        
        // Bottom padding
        VerticalSpacer(height = SakoSpacing.ExtraLarge)
    }
}

@Composable
private fun ProfileImageSection(
    imageUrl: String?,
    localImageFile: java.io.File?,  // Local file untuk preview instant
    isUploading: Boolean = false,
    onImageClick: () -> Unit,
    onViewPhotoClick: (() -> Unit)? = null,
    onImageLoaded: (() -> Unit)? = null  // Callback saat URL berhasil dimuat
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    
    // Tentukan model untuk AsyncImage: prioritas local file > URL
    val imageModel = remember(localImageFile, imageUrl) {
        when {
            localImageFile != null -> localImageFile  // Preview dari local file
            !imageUrl.isNullOrEmpty() -> imageUrl     // URL dari Cloudinary
            else -> null
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .border(5.dp, onPrimaryColor, CircleShape)
                .clickable { 
                    if (!isUploading) {
                        // Jika ada foto dan onViewPhotoClick tersedia, panggil itu
                        // Jika tidak, panggil onImageClick untuk upload/edit
                        if (imageModel != null && onViewPhotoClick != null) {
                            onViewPhotoClick()
                        } else {
                            onImageClick()
                        }
                    }
                }
                .background(surfaceColor, CircleShape)
        ) {
            if (imageModel == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(80.dp)
                    )
                }
            } else {
                // AsyncImage dengan ImageRequest untuk force refresh cache
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onSuccess = { 
                        // Callback saat image dari URL berhasil dimuat
                        // Ini trigger untuk clear localImageFile
                        if (imageModel is String && localImageFile != null) {
                            onImageLoaded?.invoke()
                        }
                    }
                )
            }
            
            // Loading overlay saat upload
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = onPrimaryColor,
                        strokeWidth = 4.dp
                    )
                }
            }
            
            // Edit icon overlay with shadow effect
            if (!isUploading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(48.dp)
                        .background(onPrimaryColor, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = onPrimaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfoCard(
    fullName: String,
    userId: String,
    email: String,
    createdAt: String,
    totalPoints: Int,
    totalXp: Int,
    levelInfo: LevelInfo?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SakoSpacing.Medium),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoSpacing.ExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Full Name with larger, bolder text
            Text(
                text = fullName.uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )

            VerticalSpacer(height = SakoSpacing.ExtraSmall)

            // Email below name
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )

            VerticalSpacer(height = SakoSpacing.Medium)

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = SakoSpacing.Large),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            VerticalSpacer(height = SakoSpacing.Medium)

            // Total Points with gradient background
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryRed.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SakoSpacing.Medium, horizontal = SakoSpacing.Large),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Points",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(SakoSpacing.Small))
                    Text(
                        text = "Total Point: ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = totalPoints.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryRed
                    )
                }
            }

            VerticalSpacer(height = SakoSpacing.Large)

            // Level Info with enhanced styling
            levelInfo?.let { level ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Level badge
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = PrimaryRed,
                        modifier = Modifier.padding(bottom = SakoSpacing.Small)
                    ) {
                        Text(
                            text = "Level ${level.currentLevel}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            modifier = Modifier.padding(
                                horizontal = SakoSpacing.Medium,
                                vertical = SakoSpacing.Small
                            )
                        )
                    }
                    
                    Text(
                        text = level.levelName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed
                    )

                    VerticalSpacer(height = SakoSpacing.Large)

                    // Progress Bar with enhanced design
                    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                        XPProgressBar(
                            currentXp = level.currentLevelXp,
                            targetXp = level.nextLevelXp,
                            progress = level.progressPercent
                        )

                        VerticalSpacer(height = SakoSpacing.Small)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${level.currentLevelXp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "${level.nextLevelXp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            } ?: run {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = PrimaryRed
                ) {
                    Text(
                        text = "Level 1 - Newbie",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.padding(
                            horizontal = SakoSpacing.Large,
                            vertical = SakoSpacing.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun XPProgressBar(
    currentXp: Int,
    targetXp: Int,
    progress: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(SakoCustomShapes.progressBar)
                .background(Color(0xFFE8E8E8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(SakoCustomShapes.progressBar)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryRed,
                                PrimaryRed.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            // Progress percentage text overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (progress > 0.5f) White else Color(0xFF424242)
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    totalAttempts: Int,
    completedLevels: Int,
    visitedPlaces: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SakoSpacing.Medium),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoSpacing.Large),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.CheckCircle,
                iconColor = Color(0xFF4CAF50),
                value = totalAttempts.toString(),
                label = "Kuis",
                backgroundColor = Color(0xFFE8F5E9)
            )
            VerticalDivider(
                modifier = Modifier.height(70.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            StatItem(
                icon = Icons.Default.Done,
                iconColor = Color(0xFF2196F3),
                value = completedLevels.toString(),
                label = "Level",
                backgroundColor = Color(0xFFE3F2FD)
            )
            VerticalDivider(
                modifier = Modifier.height(70.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            StatItem(
                icon = Icons.Default.Place,
                iconColor = PrimaryRed,
                value = visitedPlaces.toString(),
                label = "Tempat",
                backgroundColor = Color(0xFFFFEBEE)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        // Icon in colored circle
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
        
        VerticalSpacer(height = SakoSpacing.Small)
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onEditProfileClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SakoSpacing.Medium),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoSpacing.Medium)
        ) {
            ActionButton(
                icon = Icons.Default.Edit,
                label = "Edit Profile",
                onClick = onEditProfileClick,
                iconColor = Color(0xFF2196F3),
                backgroundColor = Color(0xFFE3F2FD)
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = SakoSpacing.Small),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            ActionButton(
                icon = Icons.Default.Star,
                label = "Lihat Badge",
                onClick = onBadgesClick,
                iconColor = Color(0xFFFFC107),
                backgroundColor = Color(0xFFFFF8E1)
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = SakoSpacing.Small),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            ActionButton(
                icon = Icons.Default.Lock,
                label = "Ubah Password",
                onClick = onChangePasswordClick,
                iconColor = PrimaryRed,
                backgroundColor = Color(0xFFFFEBEE)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconColor: Color,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = SakoSpacing.Medium, horizontal = SakoSpacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(SakoSpacing.Medium))
        
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Dialog untuk opsi foto profil yang sudah ada
 */
@Composable
private fun ImageOptionsDialog(
    imageUrl: String?,
    onDismiss: () -> Unit,
    onViewPhoto: () -> Unit,
    onChangePhoto: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Foto Profil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(SakoSpacing.Large))

                // View Photo Option
                Button(
                    onClick = onViewPhoto,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lihat Foto")
                }

                Spacer(modifier = Modifier.height(SakoSpacing.Medium))

                // Change Photo Option
                Button(
                    onClick = onChangePhoto,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ganti Foto")
                }

                Spacer(modifier = Modifier.height(SakoSpacing.Medium))

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal")
                }
            }
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(SakoSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            alpha = 0.6f
        )
        VerticalSpacer(height = SakoSpacing.Medium)
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        VerticalSpacer(height = SakoSpacing.Large)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Coba Lagi")
        }
    }
}

// Helper Functions
private fun formatRegistrationDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd.MM.yyyy / HH:mm:ss", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}