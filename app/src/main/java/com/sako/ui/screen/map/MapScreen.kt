package com.sako.ui.screen.map

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.R
import com.sako.data.remote.response.TouristPlaceItem
import com.sako.data.remote.response.VisitedPlaceItem
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.components.SakoTextInputField
import com.sako.ui.theme.SakoPrimary
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel
import java.util.Locale

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val touristPlaces by viewModel.touristPlaces.collectAsState()
    val visitedPlaces by viewModel.visitedPlaces.collectAsState()
    
    var showVisited by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Load only if data not already present (smart loading)
    LaunchedEffect(Unit) {
        if (touristPlaces is Resource.Loading || touristPlaces is Resource.Error) {
            viewModel.loadTouristPlaces(forceRefresh = false)
        }
    }

    // Speech-to-Text launcher
    val context = LocalContext.current
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (spokenText != null) {
                searchQuery = spokenText
                viewModel.searchPlaces(spokenText)
                println("🎤 Voice search: $spokenText")
            }
        }
    }

    BackgroundImage {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header dengan Logo Sako (diperkecil)
            Image(
                painter = painterResource(id = R.drawable.sako),
                contentDescription = "Logo Sako",
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.4f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle text (diperkecil)
            Text(
                text = "Temukan tempat wisata menarik di sekitar Anda",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Search Bar dengan Voice Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SakoTextInputField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (it.isNotBlank()) {
                            viewModel.searchPlaces(it)
                        } else {
                            viewModel.loadTouristPlaces()
                        }
                    },
                    label = "Cari tempat wisata...",
                    leadingIcon = Icons.Default.Search,
                    placeholder = "Cari berdasarkan nama atau lokasi",
                    modifier = Modifier.weight(1f)
                )
                
                // Voice search button
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID") // Bahasa Indonesia
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Katakan nama tempat wisata...")
                        }
                        speechLauncher.launch(intent)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.microphone),
                        contentDescription = "Voice Search",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton(
                    text = "Semua Tempat",
                    isSelected = !showVisited,
                    onClick = {
                        showVisited = false
                        searchQuery = ""
                        viewModel.loadTouristPlaces()
                    },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Dikunjungi",
                    isSelected = showVisited,
                    onClick = {
                        showVisited = true
                        searchQuery = ""
                        viewModel.loadVisitedPlaces()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Content - handle two separate resources to avoid cast issues
            if (showVisited) {
                // Visited Places Tab
                val visitedResource = visitedPlaces // Capture to local variable for smart cast
                when (visitedResource) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = SakoPrimary
                                )
                                Text(
                                    text = "Memuat data...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    is Resource.Success -> {
                        if (visitedResource.data.isEmpty()) {
                            EmptyState(
                                message = "Belum ada tempat yang dikunjungi.\nScan QR di lokasi wisata untuk check-in!"
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = visitedResource.data,
                                    key = { it.id }
                                ) { place ->
                                    VisitedPlaceCard(
                                        place = place,
                                        onClick = { onNavigateToDetail(place.id) }
                                    )
                                }
                                
                                // Spacer untuk BottomNav
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        ErrorState(
                            message = visitedResource.error,
                            onRetry = { viewModel.loadVisitedPlaces() }
                        )
                    }
                }
            } else {
                // All Tourist Places Tab
                val placesResource = touristPlaces // Capture to local variable for smart cast
                when (placesResource) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = SakoPrimary
                                )
                                Text(
                                    text = "Memuat data...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    is Resource.Success -> {
                        if (placesResource.data.isEmpty()) {
                            EmptyState(
                                message = "Tidak ada tempat wisata ditemukan"
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = placesResource.data,
                                    key = { it.id }
                                ) { place ->
                                    TouristPlaceCard(
                                        place = place,
                                        onClick = { onNavigateToDetail(place.id) }
                                    )
                                }
                                
                                // Spacer untuk BottomNav
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        ErrorState(
                            message = placesResource.error,
                            onRetry = { viewModel.loadTouristPlaces() }
                        )
                    }
                }
            }
        }
        
        // Floating Action Button untuk Scan QR
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onNavigateToScan,
                containerColor = SakoPrimary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Scan QR"
                )
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) SakoPrimary else Color.Transparent,
            contentColor = if (isSelected) Color.White else SakoPrimary
        ),
        border = if (!isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, SakoPrimary)
        } else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun TouristPlaceCard(
    place: TouristPlaceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar tempat wisata
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.sako),
                    placeholder = painterResource(id = R.drawable.sako)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Detail tempat
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = place.address ?: "Alamat tidak tersedia",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status kunjungan
                if (place.isVisited) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Dikunjungi",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Dikunjungi",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Belum dikunjungi",
                            tint = SakoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Belum dikunjungi",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            // Arrow icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Lihat detail",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            SakoPrimaryButton(
                text = "Coba Lagi",
                onClick = onRetry,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun VisitedPlaceCard(
    place: VisitedPlaceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar tempat wisata
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.sako),
                    placeholder = painterResource(id = R.drawable.sako)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Detail tempat
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = place.address ?: "Alamat tidak tersedia",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status kunjungan (selalu dikunjungi untuk visited places)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Dikunjungi",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Dikunjungi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            // Arrow icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Lihat detail",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}