package com.sako.ui.screen.kuis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sako.ui.components.QuizCategoryCard
import com.sako.ui.components.LoadingScreen
import com.sako.ui.components.ErrorScreen
import com.sako.ui.theme.SakoPrimary
import com.sako.utils.Resource
import com.sako.viewmodel.KuisViewModel
import com.sako.viewmodel.ViewModelFactory
import com.sako.data.repository.SakoRepository
import com.sako.ui.components.BackgroundImage

/**
 * Quiz Category Choose Screen
 * Menampilkan list kategori quiz yang tersedia
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCategoryChooseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLevelList: (String) -> Unit,
    viewModel: KuisViewModel,
    modifier: Modifier = Modifier
) {
    val categoriesState by viewModel.categoriesState.collectAsState()

    // Load categories saat pertama kali
    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }
    BackgroundImage {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Top Bar dengan back button transparan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = SakoPrimary
                    )
                }
                Text(
                    text = "Pilih Kategori Kuis",
                    fontWeight = FontWeight.Bold,
                    color = SakoPrimary,
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }

        when (categoriesState) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                val categories = (categoriesState as Resource.Success).data.data
                
                if (categories.isEmpty()) {
                    EmptyStateScreen()
                } else {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Pilih kategori untuk memulai quiz dan tingkatkan pengetahuanmu!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SakoPrimary
                            )
                        }

                        items(categories) { category ->
                            QuizCategoryCard(
                                categoryName = category.name,
                                categoryDescription = category.description ?: "Tidak ada deskripsi",
                                imageUrl = null,
                                progressPercentage = (category.progress?.percentCompleted?.toFloat() ?: 0f) / 100f,
                                completedLevels = category.progress?.completedLevelsCount ?: 0,
                                totalLevels = category.progress?.totalLevelsCount ?: 0,
                                onClick = {
                                    viewModel.selectCategory(category)
                                    onNavigateToLevelList(category.id)
                                }
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                val errorMessage = (categoriesState as Resource.Error).error
                ErrorScreen(
                    message = errorMessage,
                    onRetry = { viewModel.refreshCategories() }
                )
            }
        } 
    } 
    }  
}

@Composable
fun EmptyStateScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Belum Ada Kategori",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SakoPrimary
            )
            Text(
                text = "Kategori quiz akan segera tersedia",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}