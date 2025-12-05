package com.sako.ui.screen.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.R
import com.sako.data.remote.response.ReviewItem
import com.sako.data.remote.response.TouristPlaceDetail
import com.sako.data.remote.response.ReviewsData
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoConfirmationDialog
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.theme.SakoPrimary
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMapScreen(
    placeId: String,
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToAddReview: (String) -> Unit,
    onNavigateToEditReview: (String, String, String, Int, String?) -> Unit, // Added placeName parameter
    modifier: Modifier = Modifier
) {
    val placeDetailResource by viewModel.touristPlaceDetail.collectAsState()
    val reviewsResource by viewModel.reviewsList.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showUserReviewMenu by remember { mutableStateOf(false) }

    LaunchedEffect(placeId) {
        viewModel.loadTouristPlaceDetail(placeId)
        viewModel.loadPlaceReviews(placeId)
    }

    BackgroundImage {
        when (val resource = placeDetailResource) {
            is Resource.Loading -> {
                LoadingState()
            }
            is Resource.Error -> {
                ErrorState(
                    message = resource.error,
                    onRetry = {
                        viewModel.loadTouristPlaceDetail(placeId)
                        viewModel.loadPlaceReviews(placeId)
                    },
                    onNavigateBack = onNavigateBack
                )
            }
            is Resource.Success -> {
                DetailContent(
                    detail = resource.data,
                    reviewsResource = reviewsResource,
                    onNavigateBack = onNavigateBack,
                    onNavigateToAddReview = onNavigateToAddReview,
                    onNavigateToEditReview = onNavigateToEditReview,
                    onNavigateToScan = onNavigateToScan,
                    onToggleLike = { reviewId -> 
                        viewModel.toggleReviewLike(reviewId)
                    },
                    onDeleteReview = { reviewId ->
                        showDeleteDialog = reviewId
                    },
                    onShowUserReviewMenu = { show ->
                        showUserReviewMenu = show
                    },
                    showUserReviewMenu = showUserReviewMenu
                )
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { reviewId ->
        SakoConfirmationDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = "Hapus Ulasan",
            message = "Apakah Anda yakin ingin menghapus ulasan ini?",
            confirmButtonText = "Hapus",
            onConfirm = {
                viewModel.deleteReview(reviewId)
                showDeleteDialog = null
                // Reload data after deletion
                viewModel.loadTouristPlaceDetail(placeId)
                viewModel.loadPlaceReviews(placeId)
            },
            cancelButtonText = "Batal"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    detail: TouristPlaceDetail,
    reviewsResource: Resource<ReviewsData>,
    onNavigateBack: () -> Unit,
    onNavigateToAddReview: (String) -> Unit,
    onNavigateToEditReview: (String, String, String, Int, String?) -> Unit,
    onNavigateToScan: () -> Unit,
    onToggleLike: (String) -> Unit,
    onDeleteReview: (String) -> Unit,
    onShowUserReviewMenu: (Boolean) -> Unit,
    showUserReviewMenu: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Custom Top Bar dengan back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = detail.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header Image
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AsyncImage(
                        model = detail.imageUrl,
                        contentDescription = detail.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.sako),
                        placeholder = painterResource(id = R.drawable.sako)
                    )
                }
            }

            // Place Info
            item {
                PlaceInfoCard(detail = detail)
            }

            // QR Scan Section
            item {
                QRScanSection(
                    isScanEnabled = detail.isScanEnabled,
                    onNavigateToScan = onNavigateToScan
                )
            }

            // Reviews Section
            item {
                ReviewsSection(
                    reviewsResource = reviewsResource,
                    placeId = detail.id,
                    placeName = detail.name,
                    onNavigateToAddReview = onNavigateToAddReview,
                    onNavigateToEditReview = onNavigateToEditReview,
                    onToggleLike = onToggleLike,
                    onDeleteReview = onDeleteReview,
                    onShowUserReviewMenu = onShowUserReviewMenu,
                    showUserReviewMenu = showUserReviewMenu
                )
            }
        }
    }
}

@Composable
private fun PlaceInfoCard(
    detail: TouristPlaceDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = detail.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (detail.averageRating > 0) {
                        String.format("%.1f", detail.averageRating)
                    } else {
                        "Belum ada rating"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Address
            if (!detail.address.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = SakoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = detail.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Description
            if (!detail.description.isNullOrEmpty()) {
                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = detail.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}

@Composable
private fun QRScanSection(
    isScanEnabled: Boolean,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isScanEnabled) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR Code",
                    tint = if (isScanEnabled) SakoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Check-in di Lokasi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isScanEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    
                    Text(
                        text = if (isScanEnabled) {
                            "Scan QR code untuk check-in di tempat wisata ini"
                        } else {
                            "Anda sudah check-in di tempat wisata ini"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isScanEnabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            if (isScanEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                
                SakoPrimaryButton(
                    text = "Scan QR Code",
                    onClick = onNavigateToScan,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Check-in complete",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Check-in berhasil!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection(
    reviewsResource: Resource<ReviewsData>,
    placeId: String,
    placeName: String,
    onNavigateToAddReview: (String) -> Unit,
    onNavigateToEditReview: (String, String, String, Int, String?) -> Unit,
    onToggleLike: (String) -> Unit,
    onDeleteReview: (String) -> Unit,
    onShowUserReviewMenu: (Boolean) -> Unit,
    showUserReviewMenu: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Ulasan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (reviewsResource) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SakoPrimary)
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = "Gagal memuat ulasan: ${reviewsResource.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is Resource.Success -> {
                    val reviewsData = reviewsResource.data
                    
                    // User Review Section
                    UserReviewSection(
                        userReview = reviewsData.userReview,
                        placeId = placeId,
                        placeName = placeName,
                        onNavigateToAddReview = onNavigateToAddReview,
                        onNavigateToEditReview = onNavigateToEditReview,
                        onDeleteReview = onDeleteReview,
                        onShowUserReviewMenu = onShowUserReviewMenu,
                        showUserReviewMenu = showUserReviewMenu
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Other Reviews Section
                    OtherReviewsSection(
                        otherReviews = reviewsData.otherReviews,
                        onToggleLike = onToggleLike
                    )
                }
            }
        }
    }
}

@Composable
private fun UserReviewSection(
    userReview: ReviewItem?,
    placeId: String,
    placeName: String,
    onNavigateToAddReview: (String) -> Unit,
    onNavigateToEditReview: (String, String, String, Int, String?) -> Unit,
    onDeleteReview: (String) -> Unit,
    onShowUserReviewMenu: (Boolean) -> Unit,
    showUserReviewMenu: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Ulasan Kamu",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (userReview == null) {
            // No user review - show add review button
            EmptyUserReviewCard(
                onNavigateToAddReview = { onNavigateToAddReview(placeId) }
            )
        } else {
            // User has review - show review with menu
            UserReviewCard(
                review = userReview,
                onShowMenu = { onShowUserReviewMenu(true) },
                showMenu = showUserReviewMenu,
                onDismissMenu = { onShowUserReviewMenu(false) },
                onEditReview = {
                    onNavigateToEditReview(
                        userReview.id,
                        placeId,
                        placeName,
                        userReview.rating,
                        userReview.reviewText
                    )
                    onShowUserReviewMenu(false)
                },
                onDeleteReview = {
                    onDeleteReview(userReview.id)
                    onShowUserReviewMenu(false)
                }
            )
        }
    }
}

@Composable
private fun EmptyUserReviewCard(
    onNavigateToAddReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Belum ada ulasan",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Bagikan pengalaman Anda di tempat ini",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = onNavigateToAddReview,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SakoPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Review",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Ulasan")
            }
        }
    }
}

@Composable
private fun UserReviewCard(
    review: ReviewItem,
    onShowMenu: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onEditReview: () -> Unit,
    onDeleteReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User info
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (review.userName?.firstOrNull()?.uppercaseChar() ?: 'U').toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "Kamu",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Menu button
                Box {
                    IconButton(onClick = onShowMenu) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = onDismissMenu
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Ulasan") },
                            onClick = onEditReview,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Ulasan") },
                            onClick = onDeleteReview,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating
            RatingDisplay(rating = review.rating)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Review text
            if (!review.reviewText.isNullOrEmpty()) {
                Text(
                    text = review.reviewText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date
            Text(
                text = formatDate(review.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun OtherReviewsSection(
    otherReviews: List<ReviewItem>,
    onToggleLike: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (otherReviews.isNotEmpty()) {
            Text(
                text = "Ulasan Lainnya",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            otherReviews.forEach { review ->
                OtherReviewCard(
                    review = review,
                    onToggleLike = { onToggleLike(review.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Text(
                text = "Belum ada ulasan lain",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OtherReviewCard(
    review: ReviewItem,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (review.userName?.firstOrNull()?.uppercaseChar() ?: 'U').toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
                
                Text(
                    text = review.userName ?: "Anonim",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating
            RatingDisplay(rating = review.rating)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Review text
            if (!review.reviewText.isNullOrEmpty()) {
                Text(
                    text = review.reviewText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Like button and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(review.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onToggleLike() }
                ) {
                    Image(
                        painter = painterResource(
                            id = if (review.isLikedByMe) R.drawable.is_liked else R.drawable.not_liked
                        ),
                        contentDescription = if (review.isLikedByMe) "Unlike" else "Like",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = review.totalLikes.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingDisplay(
    rating: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(color = SakoPrimary)
            Text(
                text = "Memuat detail tempat...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text("Kembali")
                }
                SakoPrimaryButton(
                    text = "Coba Lagi",
                    onClick = onRetry
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        // Try alternative format
        try {
            val altFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            val date = altFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e2: Exception) {
            dateString
        }
    }
}