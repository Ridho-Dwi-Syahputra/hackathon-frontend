package com.sako.ui.screen.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sako.R
import com.sako.data.remote.response.VideoCollectionWithFlag
import com.sako.ui.components.SakoTextInputField

/**
 * AddToCollectionBottomSheet - Bottom sheet untuk menambahkan video ke koleksi
 * Menampilkan:
 * - List koleksi user dengan checkbox (sudah ada/belum)
 * - Button "Buat Koleksi Baru"
 * - Tombol close
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionBottomSheet(
    videoId: String,
    collections: List<VideoCollectionWithFlag>,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onAddToCollection: (String) -> Unit,
    onRemoveFromCollection: (String) -> Unit,
    onCreateNewCollection: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Tambah ke Koleksi",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            // Empty collections
            else if (collections.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.image),
                        contentDescription = "No collections",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Belum ada koleksi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Collection list
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collections) { collection ->
                        CollectionItem(
                            collection = collection,
                            onClick = {
                                if (collection.isVideoInCollection) {
                                    onRemoveFromCollection(collection.id)
                                } else {
                                    onAddToCollection(collection.id)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create new collection button
            OutlinedButton(
                onClick = onCreateNewCollection,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat koleksi baru",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buat Koleksi Baru")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CollectionItem(
    collection: VideoCollectionWithFlag,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (collection.isVideoInCollection)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = collection.namaKoleksi,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${collection.jumlahVideo} video",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Checkmark if already in collection
            if (collection.isVideoInCollection) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sudah ditambahkan",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * CreateCollectionDialog - Dialog untuk membuat koleksi baru
 */
@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit,
    isLoading: Boolean = false
) {
    var namaKoleksi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title
                Text(
                    text = "Buat Koleksi Baru",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Nama Koleksi Input
                SakoTextInputField(
                    value = namaKoleksi,
                    onValueChange = {
                        namaKoleksi = it
                        errorMessage = null
                    },
                    label = "Nama Koleksi",
                    placeholder = "Contoh: Wisata Favorit",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deskripsi Input (Optional)
                SakoTextInputField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = "Deskripsi (Opsional)",
                    placeholder = "Deskripsikan koleksi ini...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    enabled = !isLoading
                )

                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            when {
                                namaKoleksi.isBlank() -> {
                                    errorMessage = "Nama koleksi tidak boleh kosong"
                                }
                                namaKoleksi.length > 100 -> {
                                    errorMessage = "Nama koleksi maksimal 100 karakter"
                                }
                                else -> {
                                    onCreate(
                                        namaKoleksi.trim(),
                                        deskripsi.trim().ifEmpty { null }
                                    )
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Buat")
                        }
                    }
                }
            }
        }
    }
}
