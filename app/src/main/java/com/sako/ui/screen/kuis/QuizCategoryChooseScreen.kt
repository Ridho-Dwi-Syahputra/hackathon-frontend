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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Pilih Kategori Quiz",
                            fontWeight = FontWeight.Bold,
                            color = SakoPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = SakoPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
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
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Pilih kategori untuk memulai quiz dan tingkatkan pengetahuanmu!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
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

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
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
}

@Composable
fun EmptyStateScreen() {
    TODO("Not yet implemented")
}