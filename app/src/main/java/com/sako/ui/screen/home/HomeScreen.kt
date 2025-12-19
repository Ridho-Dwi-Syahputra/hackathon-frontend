package com.sako.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sako.R
import com.sako.data.remote.response.*
import com.sako.ui.components.BackgroundImage
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent
import com.sako.ui.theme.PrimaryRedDark
import com.sako.ui.theme.SakoDimensions
import com.sako.ui.theme.SakoCustomShapes
import com.sako.utils.Resource
import com.sako.viewmodel.HomeViewModel
import com.sako.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToVideo: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    // Load dashboard only if not already loaded (smart caching)
    LaunchedEffect(Unit) {
        viewModel.loadDashboard(forceRefresh = false)
    }
    
    val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()
    BackgroundImage {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SakoPrimary)
                    .padding(top = 16.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sako),
                        contentDescription = "Logo SAKO",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SAKO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
            
            // Content
            when (dashboardState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SakoPrimary)
                    }
                }
                
                is Resource.Success -> {
                    val data = (dashboardState as Resource.Success<DashboardData>).data
                    HomeContent(
                        dashboardData = data,
                        onNavigateToQuiz = onNavigateToQuiz,
                        onNavigateToVideo = onNavigateToVideo,
                        onNavigateToMap = onNavigateToMap
                    )
                }
                
                is Resource.Error -> {
                    val errorMsg = (dashboardState as Resource.Error).error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SakoPrimary
                            )
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    dashboardData: DashboardData,
    onNavigateToQuiz: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SakoDimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(SakoDimensions.spacingLarge)
    ) {
        // User Level & XP Card
        UserLevelCard(levelInfo = dashboardData.userStats.level)
        
        // Stats Overview Card
        StatsOverviewCard(
            quizStats = dashboardData.userStats.quizStats,
            videoStats = dashboardData.userStats.videoStats,
            mapStats = dashboardData.userStats.mapStats,
            totalXp = dashboardData.userStats.totalXp
        )
        
        // Feature Cards dengan spacing lebih jelas
        Text(
            text = "Fitur Utama",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SakoDimensions.spacingNormal)
        ) {
            FeatureCard(
                title = "Kuis",
                icon = Icons.Default.Quiz,
                onClick = onNavigateToQuiz,
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                title = "Video",
                icon = Icons.Default.VideoLibrary,
                onClick = onNavigateToVideo,
                modifier = Modifier.weight(1f)
            )
        }

        FeatureCard(
            title = "Lokasi Budaya",
            icon = Icons.Default.Map,
            onClick = onNavigateToMap,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Recent Activities
        if (dashboardData.recentActivities.quizAttempts.isNotEmpty()) {
            RecentActivitiesSection(activities = dashboardData.recentActivities.quizAttempts)
        }
        
        // Popular Videos
        if (dashboardData.popularContent.videos.isNotEmpty()) {
            PopularVideosSection(videos = dashboardData.popularContent.videos)
        }
        
        // Popular Places
        if (dashboardData.popularContent.places.isNotEmpty()) {
            PopularPlacesSection(places = dashboardData.popularContent.places)
        }
        
        // Achievements
        if (dashboardData.achievements.isNotEmpty()) {
            AchievementsSection(achievements = dashboardData.achievements)
        }
    }
}

@Composable
fun UserLevelCard(levelInfo: LevelInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SakoCustomShapes.userLevelCard,
        colors = CardDefaults.cardColors(
            containerColor = SakoPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Level Badge
                Surface(
                    shape = RoundedCornerShape(SakoDimensions.cornerRadiusLarge),
                    color = SakoAccent,
                    modifier = Modifier.padding(bottom = SakoDimensions.paddingSmall)
                ) {
                    Text(
                        text = "Level ${levelInfo.currentLevel}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRedDark,
                        modifier = Modifier.padding(horizontal = SakoDimensions.paddingMedium, vertical = SakoDimensions.paddingExtraSmall)
                    )
                }
                
                Text(
                    text = levelInfo.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // XP Display
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${levelInfo.xpCurrent}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = SakoAccent
                )
                Text(
                    text = "/ ${levelInfo.xpForNextLevel} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // Progress Bar
        Column(modifier = Modifier.padding(horizontal = SakoDimensions.paddingLarge, vertical = 0.dp)) {
            LinearProgressIndicator(
                progress = { (levelInfo.progressPercentage / 100f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(SakoDimensions.paddingSmall)),
                color = SakoAccent,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(SakoDimensions.paddingSmall))
            
            Text(
                text = "${levelInfo.progressPercentage}% menuju level berikutnya",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(SakoDimensions.paddingMedium))
        }
    }
}

@Composable
fun StatsOverviewCard(
    quizStats: QuizStats,
    videoStats: VideoStats,
    mapStats: MapStats,
    totalXp: Int
) {
    Column {
        // Header dengan icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = SakoDimensions.spacingNormal)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = SakoAccent,
                modifier = Modifier.size(SakoDimensions.iconMedium)
            )
            Spacer(modifier = Modifier.width(SakoDimensions.paddingSmall))
            Text(
                text = "Statistik Saya",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Grid 3 kolom - baris pertama
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SakoDimensions.spacingMedium)
        ) {
            CompactStatCard(
                icon = Icons.Default.Quiz,
                iconColor = SakoAccent,
                value = "${quizStats.completed}",
                label = "Kuis Dikerjakan",
                modifier = Modifier.weight(1f)
            )
            CompactStatCard(
                icon = Icons.Default.Favorite,
                iconColor = SakoPrimary,
                value = "${videoStats.favorites}",
                label = "Video Favorit",
                modifier = Modifier.weight(1f)
            )
            CompactStatCard(
                icon = Icons.Default.Place,
                iconColor = SakoAccent,
                value = "${mapStats.placesVisited}",
                label = "Tempat Dikunjungi",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(SakoDimensions.spacingMedium))
        
        // Grid 3 kolom - baris kedua
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SakoDimensions.spacingMedium)
        ) {
            CompactStatCard(
                icon = Icons.Default.Star,
                iconColor = Color(0xFFFFD700), // Gold
                value = "${quizStats.totalPoints}",
                label = "Total Poin",
                modifier = Modifier.weight(1f)
            )
            CompactStatCard(
                icon = Icons.Default.EmojiEvents,
                iconColor = Color(0xFF4CAF50), // Green
                value = "$totalXp",
                label = "Total XP",
                modifier = Modifier.weight(1f)
            )
            CompactStatCard(
                icon = Icons.Default.VideoLibrary,
                iconColor = SakoAccent,
                value = "${videoStats.collections}",
                label = "Koleksi Video",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CompactStatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = SakoCustomShapes.statCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(SakoDimensions.paddingSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RecentActivitiesSection(activities: List<RecentQuizAttempt>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aktivitas Terakhir",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Kuis",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        activities.take(5).forEach { activity ->
            ActivityItem(activity = activity)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ActivityItem(activity: RecentQuizAttempt) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.levelName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activity.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Poin: ${activity.pointsEarned} | XP: ${activity.xpEarned}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SakoAccent
                )
            }
            
            if (activity.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun PopularVideosSection(videos: List<PopularVideo>) {
    Column {
        Text(
            text = "Video Populer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(SakoDimensions.spacingMedium))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SakoDimensions.spacingMedium),
            modifier = Modifier.fillMaxWidth()
        ) {
            val videoList = videos.take(5)
            items(videoList.size) { index ->
                PopularVideoCard(video = videoList[index])
            }
        }
    }
}

@Composable
fun PopularVideoCard(video: PopularVideo) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = SakoCustomShapes.popularVideoCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationSmall)
    ) {
        Column(
            modifier = Modifier.padding(SakoDimensions.paddingMedium)
        ) {
            // Thumbnail placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(SakoCustomShapes.statCard)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = "Video",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(SakoDimensions.paddingSmall))
            
            Text(
                text = video.judul,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(SakoDimensions.paddingExtraSmall))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Likes",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SakoDimensions.iconExtraSmall)
                )
                Spacer(modifier = Modifier.width(SakoDimensions.paddingExtraSmall))
                Text(
                    text = "${video.favoriteCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PopularPlacesSection(places: List<PopularPlace>) {
    Column {
        Text(
            text = "Tempat Populer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(SakoDimensions.spacingMedium))
        
        places.take(3).forEach { place ->
            PopularPlaceCard(place = place)
            Spacer(modifier = Modifier.height(SakoDimensions.paddingSmall))
        }
    }
}

@Composable
fun PopularPlaceCard(place: PopularPlace) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SakoCustomShapes.featureCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingNormal),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = place.description?.take(50) ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${place.averageRating ?: 0.0} (${place.reviewCount} review)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Place",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<Achievement>) {
    Column {
        Text(
            text = "Pencapaian Terbaru",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(SakoDimensions.spacingMedium))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SakoDimensions.spacingMedium),
            modifier = Modifier.fillMaxWidth()
        ) {
            val achievementList = achievements.take(5)
            items(achievementList.size) { index ->
                AchievementCard(achievement = achievementList[index])
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = SakoCustomShapes.featureCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Badge",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = achievement.badgeName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtitle = when (title) {
        "Kuis" -> "Uji pengetahuan kebudayaan"
        "Video" -> "Belajar sambil nonton"
        "Lokasi Budaya" -> "Jelajahi tempat bersejarah"
        else -> "Mulai eksplorasi"
    }
    
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = SakoCustomShapes.featureCard,
        colors = CardDefaults.cardColors(
            containerColor = SakoAccent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = SakoDimensions.elevationMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SakoDimensions.paddingLarge)
        ) {
            // Icon container dengan background semi-transparent
            Surface(
                shape = RoundedCornerShape(SakoDimensions.paddingMedium),
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(SakoDimensions.iconExtraLarge)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PrimaryRedDark,
                        modifier = Modifier.size(SakoDimensions.iconMedium)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(SakoDimensions.spacingMedium))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryRedDark
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryRedDark.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Helper function untuk extract YouTube video ID dari URL
private fun extractVideoId(url: String?): String {
    if (url.isNullOrBlank()) return ""
    return try {
        when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("watch?v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("/embed/") -> url.substringAfter("/embed/").substringBefore("?")
            else -> url.substringAfterLast("/").substringBefore("?")
        }
    } catch (e: Exception) {
        ""
    }
}