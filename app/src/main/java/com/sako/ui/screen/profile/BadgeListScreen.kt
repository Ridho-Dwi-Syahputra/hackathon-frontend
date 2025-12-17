package com.sako.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sako.data.remote.response.Badge
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoLoadingScreen
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent
import com.sako.utils.Resource
import com.sako.viewmodel.ProfileViewModel
import com.sako.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Badge List Screen - Gallery Badge User
 * Menampilkan semua badge: yang sudah dimiliki dan yang masih terkunci
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeListScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val badgeState by viewModel.badgeState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    // Load badges on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAllBadges()
    }

    Scaffold(
        topBar = {
            BadgeListTopBar(
                onBackClick = { navController.popBackStack() },
                earnedCount = (badgeState as? Resource.Success)?.data?.owned?.size ?: 0,
                totalCount = (badgeState as? Resource.Success)?.data?.totalBadges ?: 0
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        BackgroundImage(alpha = 0.3f) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (badgeState) {
                    is Resource.Loading -> {
                        SakoLoadingScreen(
                            message = "Memuat badge...",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is Resource.Error -> {
                        ErrorBadgeContent(
                            error = (badgeState as Resource.Error).error,
                            onRetry = { viewModel.loadAllBadges() }
                        )
                    }
                    is Resource.Success -> {
                        val data = (badgeState as Resource.Success).data
                        
                        // Tab Row
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = SakoPrimary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = {
                                    Text(
                                        "Dimiliki (${data.owned.size})",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = {
                                    Text(
                                        "Terkunci (${data.locked.size})",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }

                        // Content
                        when (selectedTab) {
                            0 -> OwnedBadgesGrid(badges = data.owned)
                            1 -> LockedBadgesGrid(
                                badges = data.locked,
                                progress = data.progress ?: emptyMap()
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
private fun BadgeListTopBar(
    onBackClick: () -> Unit,
    earnedCount: Int,
    totalCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Koleksi Badge",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (totalCount > 0) {
                    Text(
                        text = "$earnedCount dari $totalCount Badge",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    )
}

@Composable
private fun OwnedBadgesGrid(badges: List<Badge>) {
    if (badges.isEmpty()) {
        EmptyBadgeContent(
            message = "Belum ada badge yang dimiliki.\nMulai main quiz untuk mendapatkan badge!",
            icon = Icons.Default.EmojiEvents
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges) { badge ->
                BadgeCard(
                    badge = badge,
                    isLocked = false
                )
            }
        }
    }
}

@Composable
private fun LockedBadgesGrid(
    badges: List<Badge>,
    progress: Map<String, Any>
) {
    if (badges.isEmpty()) {
        EmptyBadgeContent(
            message = "Selamat! Kamu sudah mengoleksi semua badge! 🎉",
            icon = Icons.Default.EmojiEvents
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges) { badge ->
                BadgeCard(
                    badge = badge,
                    isLocked = true,
                    progress = progress[badge.id] as? Map<String, Any>
                )
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badge: Badge,
    isLocked: Boolean,
    progress: Map<String, Any>? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (progress != null) 220.dp else 200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) 
                Color(0xFFE0E0E0) 
            else 
                Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLocked) 2.dp else 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .alpha(if (isLocked) 0.6f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Badge Icon/Image
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Gray
                    )
                } else {
                    // Trophy icon atau AsyncImage jika ada image_url
                    if (!badge.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = badge.imageUrl,
                            contentDescription = badge.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "🏆",
                            fontSize = 50.sp
                        )
                    }
                }
            }

            // Badge Name
            Text(
                text = badge.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isLocked) Color.Gray else SakoPrimary
            )

            // Badge Description
            Text(
                text = badge.description ?: "",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray
            )

            // Earned Date (if owned)
            if (!isLocked && badge.earnedAt != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatDate(badge.earnedAt),
                    fontSize = 10.sp,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }

            // Progress Bar (if locked with progress data)
            if (isLocked && progress != null) {
                Spacer(modifier = Modifier.weight(1f))
                val current = (progress["current"] as? Number)?.toInt() ?: 0
                val target = (progress["target"] as? Number)?.toInt() ?: 100
                val percentage = (progress["percentage"] as? Number)?.toFloat() ?: 0f

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = (percentage / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = SakoAccent,
                        trackColor = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$current / $target",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyBadgeContent(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorBadgeContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Gagal memuat badge",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SakoPrimary
                )
            ) {
                Text("Coba Lagi")
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}