package com.sako.ui.screen.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sako.data.remote.response.VideoItem
import com.sako.ui.theme.SakoCustomTypography
import com.sako.ui.components.SakoTextInputField
import com.sako.ui.components.BackgroundImage
import com.sako.ui.theme.SakoTheme

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
    videos: List<VideoItem> = sampleVideos(),
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    SakoTheme {
        BackgroundImage {
            Box(modifier = modifier.fillMaxSize()) {
			Column(modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)) {

				// Search bar
				var query by remember { mutableStateOf("") }
				SakoTextInputField(
					value = query,
					onValueChange = { query = it },
					label = "Cari video...",
					leadingIcon = Icons.Default.Search,
					placeholder = "Cari berdasarkan judul atau kategori"
				)

				// Categories
				val categories = remember(videos) {
					listOf("All") + videos.map { it.kategori }.distinct()
				}
				var selectedCategory by remember { mutableStateOf("All") }

				androidx.compose.foundation.lazy.LazyRow(
					modifier = Modifier
						.padding(top = 12.dp, bottom = 12.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					items(categories) { cat ->
						val isSelected = cat == selectedCategory
						Surface(
							shape = MaterialTheme.shapes.small,
							tonalElevation = if (isSelected) 4.dp else 0.dp,
							color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
							modifier = Modifier
								.clickable { selectedCategory = cat }
								.padding(vertical = 4.dp)
						) {
							Text(
								text = cat,
								style = MaterialTheme.typography.labelLarge,
								modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
								color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
							)
						}
					}
				}

				// Filtered list
				val filtered = remember(videos, query, selectedCategory) {
					videos.filter { v ->
						val matchesQuery = query.isBlank() || v.judul.contains(query, ignoreCase = true) || v.kategori.contains(query, ignoreCase = true)
						val matchesCategory = selectedCategory == "All" || v.kategori == selectedCategory
						matchesQuery && matchesCategory
					}
				}

				val listState = rememberLazyListState()

				LazyColumn(state = listState, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
					items(filtered) { video ->
						Card(
							modifier = Modifier
								.fillMaxWidth()
								.aspectRatio(4f/3f) // Set rasio tinggi:lebar = 3:4
								.clickable { onNavigateToDetail(video.id) },
							elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
						) {
							Column(modifier = Modifier.fillMaxSize()) {
								// Thumbnail 2/3 (gunakan placeholder jika thumbnail tidak tersedia)
								if (video.thumbnailUrl.isNullOrBlank()) {
									// Placeholder box
									Box(
										modifier = Modifier
										.fillMaxWidth()
										.weight(3f)
											.padding(4.dp),
										contentAlignment = Alignment.Center
									) {
										Surface(
											shape = MaterialTheme.shapes.small,
											color = MaterialTheme.colorScheme.surfaceVariant,
											modifier = Modifier
												.fillMaxHeight()
												.fillMaxWidth()
										) {
											Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
												Icon(
													imageVector = Icons.Default.Image,
													contentDescription = "placeholder",
													tint = MaterialTheme.colorScheme.onSurfaceVariant,
													modifier = Modifier
														.align(Alignment.Center)
												)
											}
										}
									}
								} else {
									AsyncImage(
										model = video.thumbnailUrl,
										contentDescription = video.judul,
										modifier = Modifier
											.weight(2f)
											.fillMaxHeight()
									)
								}

								// Info section (1/4 height)
								Column(
									modifier = Modifier
										.fillMaxWidth()
										.weight(1f)
										.padding(8.dp),
									verticalArrangement = Arrangement.Center
								) {
									Text(text = video.judul, style = SakoCustomTypography.videoTitle, maxLines = 2)
									Spacer(modifier = Modifier.height(8.dp))
									Text(text = video.kategori, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
		judul = "Pengenalan SAKO: Sejarah dan Tujuan",
		kategori = "Pengenalan Sako",
		youtubeUrl = "https://youtu.be/dummy1",
		thumbnailUrl = null,
		deskripsi = "Video pengantar tentang SAKO",
		isActive = true,
		isFavorited = false,
		createdAt = "2025-01-01"
	),
	VideoItem(
		id = "v2",
		judul = "Flora & Fauna: Keanekaragaman Hayati",
		kategori = "Flora & Fauna",
		youtubeUrl = "https://youtu.be/dummy2",
		thumbnailUrl = null,
		deskripsi = "Mengenal flora dan fauna",
		isActive = true,
		isFavorited = false,
		createdAt = "2025-02-01"
	),
	VideoItem(
		id = "v3",
		judul = "Konservasi di SAKO",
		kategori = "Konservasi",
		youtubeUrl = "https://youtu.be/dummy3",
		thumbnailUrl = null,
		deskripsi = "Upaya konservasi lokal",
		isActive = true,
		isFavorited = true,
		createdAt = "2025-03-01"
	)
)
