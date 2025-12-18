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
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }  // Dialog pilihan upload/view
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-dismiss snackbar setelah 4 detik
    LaunchedEffect(uiState.updateSuccess, uiState.updateMessage) {
        if (uiState.updateSuccess && !uiState.updateMessage.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(
                message = uiState.updateMessage!!,
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(4000)  // Auto dismiss setelah 4 detik
            viewModel.clearUpdateMessage()
        }
    }

    // Image picker dialog
    if (showImagePicker) {
        ImageSourcePickerDialog(
            onDismiss = { showImagePicker = false },
            onImageSelected = { file ->
                viewModel.updateProfileImage(file)
                showImagePicker = false
            }
        )
    }

    // Photo options dialog (Upload atau Lihat)
    if (showPhotoOptionsDialog) {
        PhotoOptionsDialog(
            hasPhoto = !uiState.userData?.userImageUrl.isNullOrEmpty(),
            onDismiss = { showPhotoOptionsDialog = false },
            onUploadClick = {
                showPhotoOptionsDialog = false
                showImagePicker = true
            },
            onViewClick = {
                showPhotoOptionsDialog = false
                showImageOptions = true
            }
        )
    }

    // Fullscreen image viewer
    if (showImageOptions) {
        FullscreenImageViewer(
            imageUrl = uiState.userData?.userImageUrl,
            onDismiss = { showImageOptions = false }
        )
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
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
                        onProfileImageClick = {
                            // Tampilkan dialog pilihan upload/view
                            showPhotoOptionsDialog = true
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
        shadowElevation = SakoDimensions.elevationMedium,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SakoPrimary,
                            SakoPrimary.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SakoDimensions.paddingNormal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profil Saya",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(SakoDimensions.iconMedium)
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
    onProfileImageClick: () -> Unit,  // Single callback untuk klik foto
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
            localImageFile = localImageFile,
            isUploading = isUploadingImage,
            onImageClick = onProfileImageClick,  // Selalu panggil dialog pilihan
            onImageLoaded = onImageLoaded
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
    localImageFile: java.io.File?,
    isUploading: Boolean = false,
    onImageClick: () -> Unit,
    onImageLoaded: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    // Tentukan model untuk AsyncImage: prioritas local file > URL
    val imageModel = remember(localImageFile, imageUrl) {
        when {
            localImageFile != null -> localImageFile
            !imageUrl.isNullOrEmpty() -> imageUrl
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
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),  // Border subtle
                    shape = CircleShape
                )
                .clickable { 
                    if (!isUploading) {
                        onImageClick()  // Selalu panggil callback untuk dialog pilihan
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
            
            // Loading indicator saat upload
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
        
        // Edit icon overlay - DIPINDAHKAN KE LUAR AGAR TIDAK TER-CLIP
        if (!isUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 56.dp, y = 56.dp)  // Posisi di pojok kanan bawah (160dp/2 - 48dp/2 = 56dp)
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(2.dp, primaryColor, CircleShape)
                    .clickable { 
                        if (!isUploading) {
                            onImageClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
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
            .padding(horizontal = SakoDimensions.paddingLarge),
        shape = SakoCustomShapes.userLevelCard,
        colors = CardDefaults.cardColors(
            containerColor = SakoAccent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Full Name with larger, bolder text
            Text(
                text = fullName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryRedDark,
                textAlign = TextAlign.Center
            )

            VerticalSpacer(height = SakoDimensions.paddingExtraSmall)

            // Email below name
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryRedDark.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            VerticalSpacer(height = SakoDimensions.spacingNormal)

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = SakoDimensions.paddingLarge),
                color = PrimaryRedDark.copy(alpha = 0.2f),
                thickness = SakoDimensions.dividerThickness
            )

            VerticalSpacer(height = SakoDimensions.spacingNormal)

            // Total Points with semi-transparent background
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = SakoCustomShapes.statCard,
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SakoDimensions.paddingMedium, horizontal = SakoDimensions.paddingLarge),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Points",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(SakoDimensions.paddingSmall))
                    Text(
                        text = "Total Poin: ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryRedDark
                    )
                    Text(
                        text = totalPoints.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryRedDark
                    )
                }
            }

            VerticalSpacer(height = SakoDimensions.spacingLarge)

            // Level Info with enhanced styling
            levelInfo?.let { level ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Level badge
                    Surface(
                        shape = SakoCustomShapes.filterChip,
                        color = SakoPrimary,
                        modifier = Modifier.padding(bottom = SakoDimensions.paddingSmall)
                    ) {
                        Text(
                            text = "Level ${level.currentLevel}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(
                                horizontal = SakoDimensions.paddingNormal,
                                vertical = SakoDimensions.paddingSmall
                            )
                        )
                    }
                    
                    Text(
                        text = level.levelName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRedDark
                    )

                    VerticalSpacer(height = SakoDimensions.spacingNormal)

                    // Progress Bar with enhanced design
                    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                        XPProgressBar(
                            currentXp = level.currentLevelXp,
                            targetXp = level.nextLevelXp,
                            progress = level.progressPercent
                        )

                        VerticalSpacer(height = SakoDimensions.paddingSmall)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${level.currentLevelXp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryRedDark.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${level.nextLevelXp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryRedDark.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } ?: run {
                Surface(
                    shape = SakoCustomShapes.filterChip,
                    color = SakoPrimary
                ) {
                    Text(
                        text = "Level 1 - Pemula",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = SakoDimensions.paddingLarge,
                            vertical = SakoDimensions.paddingMedium
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
                .height(24.dp)
                .clip(RoundedCornerShape(SakoDimensions.paddingMedium))
                .background(Color.White.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(SakoDimensions.paddingMedium))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SakoPrimary,
                                PrimaryRedDark
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
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (progress > 0.4f) Color.White else PrimaryRedDark
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
            .padding(horizontal = SakoDimensions.paddingLarge),
        shape = SakoCustomShapes.featureCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Quiz,
                iconColor = SakoAccent,
                value = totalAttempts.toString(),
                label = "Kuis",
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
            VerticalDivider(
                modifier = Modifier.height(70.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = SakoDimensions.dividerThickness
            )
            StatItem(
                icon = Icons.Default.EmojiEvents,
                iconColor = Color(0xFFFFD700),
                value = completedLevels.toString(),
                label = "Level",
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            )
            VerticalDivider(
                modifier = Modifier.height(70.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = SakoDimensions.dividerThickness
            )
            StatItem(
                icon = Icons.Default.Place,
                iconColor = SakoPrimary,
                value = visitedPlaces.toString(),
                label = "Tempat",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
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
            color = PrimaryRedDark
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = SakoPrimary
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
            .padding(horizontal = SakoDimensions.paddingLarge),
        shape = SakoCustomShapes.featureCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingNormal)
        ) {
            ActionButton(
                icon = Icons.Default.Edit,
                label = "Edit Profil",
                onClick = onEditProfileClick,
                iconColor = SakoPrimary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = SakoDimensions.paddingSmall),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = SakoDimensions.dividerThickness
            )
            // ActionButton(
            //     icon = Icons.Default.Star,
            //     label = "Lihat Badge",
            //     onClick = onBadgesClick,
            //     iconColor = Color(0xFFFFC107),
            //     backgroundColor = Color(0xFFFFF8E1)
            // )
            // HorizontalDivider(
            //     modifier = Modifier.padding(vertical = SakoDimensions.paddingSmall),
            //     color = MaterialTheme.colorScheme.outlineVariant,
            //     thickness = SakoDimensions.dividerThickness
            // )
            ActionButton(
                icon = Icons.Default.Lock,
                label = "Ubah Kata Sandi",
                onClick = onChangePasswordClick,
                iconColor = SakoPrimary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
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
            .padding(vertical = SakoDimensions.paddingMedium, horizontal = SakoDimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(SakoDimensions.iconExtraLarge)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(SakoDimensions.iconMedium)
            )
        }
        Spacer(modifier = Modifier.width(SakoDimensions.spacingNormal))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SakoDimensions.iconMedium)
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

@Composable
private fun PhotoOptionsDialog(
    hasPhoto: Boolean,
    onDismiss: () -> Unit,
    onUploadClick: () -> Unit,
    onViewClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Foto Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Upload/Ganti Foto button
                Card(
                    onClick = onUploadClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasPhoto) Icons.Default.Edit else Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (hasPhoto) "Ganti Foto" else "Upload Foto",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Lihat Foto button (hanya jika ada foto)
                if (hasPhoto) {
                    Card(
                        onClick = onViewClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Lihat Foto",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
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