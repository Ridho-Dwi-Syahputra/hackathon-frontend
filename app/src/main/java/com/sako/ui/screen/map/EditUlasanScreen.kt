package com.sako.ui.screen.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sako.ui.components.BackgroundImage
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUlasanScreen(
    reviewId: String,
    placeId: String,
    initialRating: Int,
    initialReviewText: String?,
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableStateOf(initialRating) }
    var reviewText by remember { mutableStateOf(initialReviewText ?: "") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val reviewResult by viewModel.reviewResult.collectAsState()

    // Handle review update result
    LaunchedEffect(reviewResult) {
        when (reviewResult) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                // Error will be shown in the UI
            }
            else -> {}
        }
    }

    BackgroundImage {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Ulasan") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Perbarui ulasan Anda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Rating Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ubah Rating",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star ${index + 1}",
                                    tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    if (rating > 0) {
                        Text(
                            text = when (rating) {
                                1 -> "Sangat Buruk"
                                2 -> "Buruk"
                                3 -> "Cukup"
                                4 -> "Bagus"
                                5 -> "Sangat Bagus"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Review Text Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Edit Ulasan (Opsional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Ceritakan pengalaman Anda berkunjung ke tempat ini...") },
                        maxLines = 10
                    )
                }

                // Error Message
                when (val result = reviewResult) {
                    is Resource.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = result.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    else -> {}
                }

                // Submit Button
                Button(
                    onClick = {
                        if (rating > 0) {
                            viewModel.updateReview(
                                reviewId = reviewId,
                                touristPlaceId = placeId,
                                rating = rating,
                                reviewText = reviewText.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = rating > 0 && reviewResult !is Resource.Loading
                ) {
                    if (reviewResult is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menyimpan...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan")
                    }
                }
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("Berhasil!") },
                text = { Text("Ulasan Anda telah berhasil diperbarui.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetReviewResult()
                            onNavigateBack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}