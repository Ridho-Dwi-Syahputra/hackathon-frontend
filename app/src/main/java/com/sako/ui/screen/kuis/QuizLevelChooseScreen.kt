package com.sako.ui.screen.kuis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sako.ui.components.QuizLevelCard
import com.sako.ui.components.LevelStatus
import com.sako.ui.components.LoadingScreen
import com.sako.ui.components.ErrorScreen
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent
import com.sako.utils.Resource
import com.sako.viewmodel.KuisViewModel

/**
 * Quiz Level Choose Screen
 * Menampilkan list level dalam kategori yang dipilih
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizLevelChooseScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    viewModel: KuisViewModel,
    modifier: Modifier = Modifier
) {
    val levelsState by viewModel.levelsState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Load levels saat pertama kali
    LaunchedEffect(categoryId) {
        viewModel.getLevelsByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedCategory?.name ?: "Level Quiz",
                            fontWeight = FontWeight.Bold,
                            color = SakoPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        selectedCategory?.description?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
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
        containerColor = Color(0xFFF4F4F4)
    ) { paddingValues ->
        when (levelsState) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                val levelData = (levelsState as Resource.Success).data.data
                val levels = levelData.levels
                
                if (levels.isEmpty()) {
                    EmptyLevelsScreen()
                } else {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category Progress Header
                        item {
                            CategoryProgressCard(
                                categoryName = levelData.category.name,
                                completedLevels = levelData.category.progress?.completedLevelsCount ?: 0,
                                totalLevels = levels.size,
                                progressPercentage = (levelData.category.progress?.percentCompleted?.toFloat() ?: 0f) / 100f
                            )
                        }

                        item {
                            Text(
                                text = "Pilih level untuk memulai quiz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(levels) { level ->
                            val levelStatus = when (level.progress?.status) {
                                "locked" -> LevelStatus.LOCKED
                                "unstarted" -> LevelStatus.UNSTARTED
                                "in_progress" -> LevelStatus.IN_PROGRESS
                                "completed" -> LevelStatus.COMPLETED
                                else -> LevelStatus.LOCKED
                            }

                            QuizLevelCard(
                                levelNumber = level.displayOrder,
                                levelName = level.name,
                                levelDescription = level.description ?: "Tidak ada deskripsi",
                                status = levelStatus,
                                progressPercentage = (level.progress?.bestPercentCorrect?.toFloat() ?: 0f) / 100f,
                                bestScore = level.progress?.bestScorePoints,
                                baseXp = level.baseXp,
                                timeLimit = level.timeLimitSeconds ?: 0,
                                onClick = {
                                    if (levelStatus != LevelStatus.LOCKED) {
                                        onNavigateToQuiz(level.id)
                                    }
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
                val errorMessage = (levelsState as Resource.Error).error
                ErrorScreen(
                    message = errorMessage,
                    onRetry = { viewModel.refreshLevels(categoryId) }
                )
            }
        }
    }
}

@Composable
private fun CategoryProgressCard(
    categoryName: String,
    completedLevels: Int,
    totalLevels: Int,
    progressPercentage: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SakoPrimary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Progress Kategori",
                style = MaterialTheme.typography.labelLarge,
                color = SakoAccent,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$completedLevels dari $totalLevels level selesai",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SakoAccent
                ) {
                    Text(
                        text = "${(progressPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SakoPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = SakoAccent,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun EmptyLevelsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎯",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Belum Ada Level",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SakoPrimary
            )
            Text(
                text = "Level quiz akan segera tersedia",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}