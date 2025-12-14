# Panduan Implementasi Search dengan Microphone untuk Map Screen

## Backend API Endpoint

### Search Endpoint
```
GET /api/map/places/search?query={keyword}
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Ditemukan 5 tempat wisata untuk \"pantai\"",
  "data": [
    {
      "tourist_place_id": "1",
      "name": "Pantai Air Manis",
      "description": "Pantai dengan legenda Malin Kundang",
      "address": "Jl. Air Manis, Padang",
      "image_url": "https://...",
      "average_rating": 4.5,
      "is_visited": true,
      "visited_at": "2024-01-15T10:30:00.000Z"
    }
  ]
}
```

## Frontend Implementation

### 1. Tambahkan Dependencies untuk Speech Recognition

Di `MapScreen.kt`, tambahkan import berikut:

```kotlin
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.sako.ui.components.SakoTextInputField
import java.util.Locale
```

### 2. Implementasi Search UI di MapScreen

Ganti bagian header MapScreen (setelah logo) dengan:

```kotlin
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

    LaunchedEffect(Unit) {
        viewModel.loadTouristPlaces()
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
            Spacer(modifier = Modifier.height(60.dp))
            
            // Header dengan Logo Sako
            Image(
                painter = painterResource(id = R.drawable.sako),
                contentDescription = "Logo Sako",
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle text
            Text(
                text = "Temukan tempat wisata menarik di sekitar Anda",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rest of the content...
        }
    }
}
```

### 3. Update MapViewModel untuk Search

Tambahkan fungsi search di `MapViewModel.kt`:

```kotlin
private val _searchResults = MutableStateFlow<Resource<List<TouristPlaceItem>>>(Resource.Loading())
val searchResults: StateFlow<Resource<List<TouristPlaceItem>>> = _searchResults

fun searchPlaces(query: String) {
    viewModelScope.launch {
        _touristPlaces.value = Resource.Loading()
        
        try {
            val response = apiService.searchTouristPlaces(query)
            if (response.isSuccessful && response.body()?.success == true) {
                val places = response.body()?.data ?: emptyList()
                _touristPlaces.value = Resource.Success(places)
            } else {
                _touristPlaces.value = Resource.Error("Pencarian gagal")
            }
        } catch (e: Exception) {
            _touristPlaces.value = Resource.Error(e.message ?: "Terjadi kesalahan")
        }
    }
}
```

### 4. Update ApiService

Tambahkan endpoint search di `ApiService.kt`:

```kotlin
@GET("map/places/search")
suspend fun searchTouristPlaces(
    @Query("query") query: String
): Response<MapPlacesResponse>
```

### 5. Pastikan Drawable Microphone Ada

File `microphone.xml` sudah ada di `res/drawable/microphone.xml` (sudah dibuat sebelumnya untuk video screen)

## Fitur Search

### Backend Features:
- ✅ Search berdasarkan nama tempat wisata
- ✅ Search berdasarkan deskripsi
- ✅ Search berdasarkan alamat
- ✅ Hasil search terurut berdasarkan relevansi (nama cocok = prioritas)
- ✅ Hasil search terurut berdasarkan rating
- ✅ Tetap menampilkan status `is_visited` user
- ✅ Logging lengkap untuk monitoring

### Frontend Features:
- ✅ Search bar dengan icon search
- ✅ Voice search dengan microphone button
- ✅ Real-time search saat mengetik
- ✅ Speech recognition dalam Bahasa Indonesia
- ✅ Filter tetap berfungsi (Semua Tempat / Dikunjungi)
- ✅ Clear search saat switch filter

## Testing Endpoint

### Menggunakan Postman:

```bash
GET http://localhost:3000/api/map/places/search?query=pantai
Authorization: Bearer {your_token}
```

### Menggunakan curl:

```bash
curl -X GET "http://localhost:3000/api/map/places/search?query=pantai" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Search Query Examples

- `pantai` - mencari semua tempat dengan kata "pantai"
- `padang` - mencari berdasarkan lokasi
- `museum` - mencari tempat wisata museum
- `malin` - mencari berdasarkan deskripsi (legenda Malin Kundang)

## Notes

1. Search case-insensitive (tidak peduli huruf besar/kecil)
2. Search menggunakan LIKE dengan wildcard `%keyword%`
3. Hasil terurut prioritas: nama match > rating > alfabetis
4. Voice search otomatis trigger search API
5. Clear search query saat ganti tab filter
6. Search hanya pada data aktif (`is_active = 1`)

## Contoh Implementasi Lengkap

Lihat file berikut sebagai referensi:
- Backend Controller: `src/controllers/modul-map/detailMapController.js` - method `searchPlaces`
- Backend Model: `src/models/modul-map/detailMapModel.js` - method `searchPlaces`
- Backend Routes: `src/routes/mapRoutes.js` - route `/places/search`
- Frontend Reference: `app/src/main/java/com/sako/ui/screen/video/VideoListScreen.kt`

## Error Handling

Backend akan return error dalam kasus berikut:
- Query kosong: HTTP 400 - "Query pencarian tidak boleh kosong"
- Server error: HTTP 500 - "Terjadi kesalahan saat melakukan pencarian"

Semua error akan di-log dengan detail timestamp dan user_id untuk debugging.
