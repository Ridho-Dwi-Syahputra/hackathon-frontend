package com.sako.ui.screen.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sako.R
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.components.SakoConfirmationDialog
import com.sako.ui.components.SakoStatusDialog
import com.sako.ui.theme.SakoPrimary
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUlasanScreen(
    reviewId: String,
    placeId: String,
    placeName: String,
    initialRating: Int,
    initialReviewText: String?,
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableIntStateOf(initialRating) }
    var reviewText by remember { mutableStateOf(initialReviewText ?: "") }
    var showValidationDialog by remember { mutableStateOf(false) }
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
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Arrow back button using arrow.png
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onNavigateBack() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Edit Ulasan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Place Name Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = placeName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Rating Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Rating",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Star Rating using star.png
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Image(
                                    painter = painterResource(id = R.drawable.star),
                                    contentDescription = "Star ${index + 1}",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { rating = index + 1 },
                                    colorFilter = if (index < rating) {
                                        null // Keep original color (yellow/gold)
                                    } else {
                                        androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                                    }
                                )
                            }
                        }

                        // Rating Description
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = SakoPrimary
                            )
                        }
                    }
                }

                // Review Text Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ulasan (Opsional)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { 
                                Text(
                                    text = "Ceritakan pengalaman Anda berkunjung ke tempat ini...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SakoPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                cursorColor = SakoPrimary
                            )
                        )
                    }
                }

                // Error Message
                val currentReviewResult = reviewResult
                if (currentReviewResult is Resource.Error) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = currentReviewResult.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Submit Button
                SakoPrimaryButton(
                    text = if (reviewResult is Resource.Loading) "Menyimpan..." else "Perbarui Ulasan",
                    onClick = {
                        if (rating > 0) {
                            showValidationDialog = true
                        }
                    },
                    enabled = rating > 0 && reviewResult !is Resource.Loading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Validation Dialog - EditMapScreen - PopUp Validasi.svg
        if (showValidationDialog) {
            SakoConfirmationDialog(
                onDismissRequest = { showValidationDialog = false },
                title = "Perbarui Ulasan",
                message = "Apakah Anda yakin ingin memperbarui ulasan ini?",
                confirmButtonText = "Ya, Perbarui",
                onConfirm = {
                    viewModel.updateReview(
                        reviewId = reviewId,
                        touristPlaceId = placeId,
                        rating = rating,
                        reviewText = reviewText.ifBlank { null }
                    )
                    showValidationDialog = false
                },
                cancelButtonText = "Batal"
            )
        }

        // Success Dialog - EditMapScreen - Pop up Success.svg
        if (showSuccessDialog) {
            SakoStatusDialog(
                onDismissRequest = { },
                icon = painterResource(id = R.drawable.success),
                title = "Berhasil!",
                message = "Ulasan Anda telah berhasil diperbarui.",
                buttonText = "OK",
                onConfirm = {
                    viewModel.resetReviewResult()
                    onNavigateBack()
                }
            )
        }
    }
}