package com.sako.ui.screen.video

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.data.remote.response.VideoItem
import com.sako.R
import com.sako.ui.theme.SakoCustomTypography
import com.sako.ui.components.SakoTextInputField
import com.sako.ui.components.BackgroundImage
import com.sako.ui.theme.SakoTheme
import java.util.Locale

/**
 * VideoListScreen - Menampilkan daftar video dengan search bar, kategori dan FAB favorite
 * Layout:
 * - Search bar di atas
 * - Row kategori di bawahnya (default: All)
 * - List video: setiap card berisi thumbnail (2/3) dan judul + kategori (1/3)
 * - FAB di pojok kanan bawah untuk navigasi ke layar favorite
 */
@Composable
fun VideoListScreen(
    modifier: Modifier = Modifier,
    videos: List<VideoItem>,
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    SakoTheme {
        BackgroundImage {
            Box(modifier = modifier.fillMaxSize()) {
			Column(modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)) {

				// Search bar with voice search
				var query by remember { mutableStateOf("") }
				
				// Speech-to-Text launcher
				val context = LocalContext.current
				val speechLauncher = rememberLauncherForActivityResult(
					contract = ActivityResultContracts.StartActivityForResult()
				) { result ->
					if (result.resultCode == Activity.RESULT_OK) {
						val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
						if (spokenText != null) {
							query = spokenText
							println("🎤 Voice search: $spokenText")
						}
					}
				}
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					SakoTextInputField(
						value = query,
						onValueChange = { query = it },
						label = "Cari video...",
						leadingIcon = Icons.Default.Search,
						placeholder = "Cari berdasarkan judul atau kategori",
						modifier = Modifier.weight(1f)
					)
					
					// Voice search button
					FloatingActionButton(
						onClick = {
							val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
								putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
								putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID") // Bahasa Indonesia
								putExtra(RecognizerIntent.EXTRA_PROMPT, "Katakan kata kunci pencarian...")
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

				// Categories - limit to only these options
				val categories = remember { listOf("All", "Wisata", "Kesenian", "Kuliner", "Adat") }
				var selectedCategory by remember { mutableStateOf("All") }

				androidx.compose.foundation.lazy.LazyRow(
					modifier = Modifier
						.padding(top = 12.dp, bottom = 12.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(6.dp)
				) {
					items(categories) { cat ->
						val isSelected = cat == selectedCategory
						FilterChip(
							selected = isSelected,
							onClick = { selectedCategory = cat },
							label = {
								Text(
									text = cat,
									fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal
								)
							},
							colors = FilterChipDefaults.filterChipColors(
								selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
								selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
								containerColor = MaterialTheme.colorScheme.surface,
								labelColor = MaterialTheme.colorScheme.onSurface
							),
							border = FilterChipDefaults.filterChipBorder(
								enabled = true,
								selected = isSelected,
							borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
								selectedBorderColor = MaterialTheme.colorScheme.primary,
								borderWidth = 1.dp,
								selectedBorderWidth = 1.dp
							),
							shape = RoundedCornerShape(20.dp),
							modifier = Modifier.padding(end = 2.dp)
						)
					}
				}

				// Filtered list
				val filtered = remember(videos, query, selectedCategory) {
					// Debug: print total videos received
					println("VideoListScreen - Total videos: ${videos.size}")
					videos.forEach { v ->
						println("Video: ${v.judul}, Kategori: '${v.kategori}'")
					}
					
					videos.filter { v ->
						val matchesQuery = query.isBlank() || v.judul.contains(query, ignoreCase = true) || v.kategori.contains(query, ignoreCase = true)
						val matchesCategory = selectedCategory == "All" || v.kategori.trim().equals(selectedCategory, ignoreCase = true)
						matchesQuery && matchesCategory
					}
				}

				val listState = rememberLazyListState()

				// Video List dengan layout vertikal (seperti YouTube)
				LazyColumn(
					state = listState, 
					modifier = Modifier.fillMaxWidth(),
					contentPadding = PaddingValues(bottom = 80.dp), // Space untuk FAB
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					if (filtered.isEmpty()) {
						// Empty state
						item {
							Column(
								modifier = Modifier
									.fillMaxWidth()
									.padding(32.dp),
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.Center
							) {
								Icon(
									imageVector = Icons.Default.Search,
									contentDescription = "No results",
									modifier = Modifier.size(64.dp),
									tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
								)
								Spacer(modifier = Modifier.height(16.dp))
								Text(
									text = if (query.isNotBlank()) "Tidak ada hasil untuk \"$query\"" else "Tidak ada video di kategori ini",
									style = MaterialTheme.typography.titleMedium,
									color = MaterialTheme.colorScheme.onSurface,
									textAlign = androidx.compose.ui.text.style.TextAlign.Center
								)
							}
						}
					} else {
						items(
							items = filtered,
							key = { it.id }
						) { video ->
							// YouTube-style vertical card
							Card(
								modifier = Modifier
									.fillMaxWidth()
									.clickable { onNavigateToDetail(video.id) },
								colors = CardDefaults.cardColors(
									containerColor = MaterialTheme.colorScheme.surface
								),
								elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
								shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
							) {
								Column(modifier = Modifier.fillMaxWidth()) {
									// Thumbnail dengan play icon (16:9 ratio)
									Box(
										modifier = Modifier
											.fillMaxWidth()
											.aspectRatio(16f/9f)
									) {
										AsyncImage(
											model = video.thumbnailUrl ?: "https://img.youtube.com/vi/${extractVideoId(video.youtubeUrl)}/maxresdefault.jpg",
											contentDescription = video.judul,
											modifier = Modifier
												.fillMaxSize()
											.clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
											contentScale = androidx.compose.ui.layout.ContentScale.Crop
										)
										
										// Play icon overlay
										Surface(
											modifier = Modifier
												.align(Alignment.Center)
												.size(56.dp),
											color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
											shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
										) {
											Icon(
												imageVector = androidx.compose.material.icons.Icons.Default.PlayCircleOutline,
												contentDescription = "Play",
												modifier = Modifier
													.fillMaxSize()
													.padding(12.dp),
												tint = MaterialTheme.colorScheme.primary
											)
										}
									}
									
									// Info section
									Column(
										modifier = Modifier
											.fillMaxWidth()
											.padding(12.dp)
									) {
										// Title
										Text(
											text = video.judul,
											style = MaterialTheme.typography.titleMedium,
											fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
											maxLines = 2,
											overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
											color = MaterialTheme.colorScheme.onSurface
										)
										
										Spacer(modifier = Modifier.height(8.dp))
										
										// Category badge
										Surface(
											color = MaterialTheme.colorScheme.primaryContainer,
											shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
										) {
											Text(
												text = video.kategori,
												style = MaterialTheme.typography.labelMedium,
												color = MaterialTheme.colorScheme.onPrimaryContainer,
												modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
											)
										}
									}
								}
							}
						}
					}
				}
			}

			// FloatingActionButton - Favorite
			FloatingActionButton(
				onClick = onNavigateToFavorite,
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(16.dp)
			) {
				Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite")
			}
		    }
	    }
    }
}

/**
 * Sample data sebagai fallback agar UI dapat ditampilkan saat backend belum tersedia
 */
private fun sampleVideos(): List<VideoItem> = listOf(
	VideoItem(
		id = "v1",
		judul = "Jelajah Alam Danau Maninjau",
		kategori = "Wisata",
		youtubeUrl = "https://youtu.be/dummy1",
		thumbnailUrl = null,
		deskripsi = "Video jelajah lokasi wisata Danau Maninjau",
		isActive = 1,
		isFavorited = 0,
		createdAt = "2025-01-01"
	),
	VideoItem(
		id = "v2",
		judul = "Mengenal Tari Piring Sumatera Barat",
		kategori = "Kesenian",
		youtubeUrl = "https://youtu.be/dummy2",
		thumbnailUrl = null,
		deskripsi = "Mengenal tarian tradisional dari Sumatera Barat",
		isActive = 1,
		isFavorited = 0,
		createdAt = "2025-02-01"
	),
	VideoItem(
		id = "v3",
		judul = "Talempong",
		kategori = "Kesenian",
		youtubeUrl = "https://youtu.be/dummy3",
		thumbnailUrl = null,
		deskripsi = "Kesenian Musik Talempong",
		isActive = 1,
		isFavorited = 1,
		createdAt = "2025-03-01"
	)
)

// Helper function untuk extract video ID
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
