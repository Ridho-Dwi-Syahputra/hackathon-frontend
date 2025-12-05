package com.sako.ui.screen.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sako.R
import com.sako.ui.components.*
import com.sako.ui.theme.SakoTheme
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahUlasanScreen(
    placeId: String,
    placeName: String,
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var showValidationDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val reviewResult by viewModel.reviewResult.collectAsState()

    // Handle review result
    LaunchedEffect(reviewResult) {
        when (reviewResult) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                // Handle error - bisa tambahkan error dialog jika diperlukan
            }
            else -> {}
        }
    }

    BackgroundImage {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header dengan Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Arrow Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "Kembali",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Tambah Ulasan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Place Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tempat Wisata",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = placeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Rating Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Berikan Rating",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Star Rating Component
                    StarRatingComponent(
                        rating = rating,
                        onRatingChanged = { newRating ->
                            rating = newRating
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = when (rating) {
                            1 -> "Sangat Buruk"
                            2 -> "Buruk"
                            3 -> "Cukup"
                            4 -> "Baik" 
                            5 -> "Sangat Baik"
                            else -> "Pilih rating"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Review Text Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tulis Ulasan (Opsional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SakoTextInputField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = "Bagikan pengalaman Anda",
                        placeholder = "Ceritakan pengalaman Anda mengunjungi tempat ini...",
                        singleLine = false,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Submit Button
            SakoPrimaryButton(
                text = if (reviewResult is Resource.Loading) "Menyimpan..." else "Simpan Ulasan",
                onClick = {
                    if (rating > 0) {
                        viewModel.addReview(
                            touristPlaceId = placeId,
                            rating = rating,
                            reviewText = reviewText.takeIf { it.isNotBlank() }
                        )
                    } else {
                        showValidationDialog = true
                    }
                },
                enabled = reviewResult !is Resource.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Validation Dialog
    if (showValidationDialog) {
        SakoConfirmationDialog(
            onDismissRequest = { showValidationDialog = false },
            title = "Rating Diperlukan",
            message = "Mohon berikan rating terlebih dahulu sebelum menyimpan ulasan.",
            confirmButtonText = "OK",
            onConfirm = { /* Dialog will dismiss automatically */ }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        SakoStatusDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onNavigateBack()
            },
            icon = painterResource(id = R.drawable.success),
            title = "Ulasan Berhasil Disimpan",
            message = "Terima kasih! Ulasan Anda telah berhasil ditambahkan dan akan membantu pengunjung lain.",
            buttonText = "Kembali",
            onConfirm = { 
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun StarRatingComponent(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val starNumber = index + 1
            val isSelected = starNumber <= rating
            
            Image(
                painter = painterResource(id = R.drawable.star),
                contentDescription = "Rating $starNumber",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onRatingChanged(starNumber) }
                    .padding(4.dp),
                colorFilter = ColorFilter.tint(
                    if (isSelected) 
                        Color(0xFFFFD700) // Gold color for selected stars
                    else 
                        Color(0xFFBDBDBD) // Gray color for unselected stars
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TambahUlasanScreenPreview() {
    SakoTheme {
        // Preview tidak bisa menggunakan ViewModel, jadi buat mock
    }
}