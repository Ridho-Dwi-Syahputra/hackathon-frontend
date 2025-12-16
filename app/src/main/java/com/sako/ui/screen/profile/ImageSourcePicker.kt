package com.sako.ui.screen.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sako.ui.components.SakoSpacing
import com.sako.utils.ImageCompressor
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Image Source Picker Dialog
 * Dialog untuk memilih sumber gambar: Camera atau Gallery
 * Note: Cropping 1:1 dilakukan otomatis di backend via Cloudinary transformation
 */
@Composable
fun ImageSourcePickerDialog(
    onDismiss: () -> Unit,
    onImageSelected: (File) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permissionType by remember { mutableStateOf("") }
    var isCompressing by remember { mutableStateOf(false) }

    // Temporary file untuk camera
    var photoFile by remember { mutableStateOf<File?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            scope.launch {
                isCompressing = true
                // Compress sebelum upload
                val compressedFile = photoFile?.let { ImageCompressor.compressImageFile(it) }
                isCompressing = false
                
                compressedFile?.let { onImageSelected(it) }
                onDismiss()
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isCompressing = true
                // Compress sebelum upload
                val compressedFile = ImageCompressor.compressImage(context, uri)
                isCompressing = false
                
                compressedFile?.let { file -> onImageSelected(file) }
                onDismiss()
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            when (permissionType) {
                "camera" -> launchCamera(context) { file, uri ->
                    photoFile = file
                    photoUri = uri
                    cameraLauncher.launch(uri)
                }
                "gallery" -> galleryLauncher.launch("image/*")
            }
        } else {
            showPermissionRationale = true
        }
    }

    // Permission Rationale Dialog
    if (showPermissionRationale) {
        PermissionRationaleDialog(
            permissionType = permissionType,
            onDismiss = { showPermissionRationale = false },
            onOpenSettings = {
                // Open app settings
                val intent = android.content.Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
                showPermissionRationale = false
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pilih Sumber Gambar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(SakoSpacing.Large))

                // Camera Option
                ImageSourceOption(
                    icon = Icons.Default.CameraAlt,
                    label = "Kamera",
                    description = "Ambil foto baru",
                    onClick = {
                        permissionType = "camera"
                        val permission = Manifest.permission.CAMERA
                        
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, permission) -> {
                                // Permission granted, launch camera
                                launchCamera(context) { file, uri ->
                                    photoFile = file
                                    photoUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            }
                            else -> {
                                // Request permission
                                permissionLauncher.launch(permission)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SakoSpacing.Medium))

                // Gallery Option
                ImageSourceOption(
                    icon = Icons.Default.PhotoLibrary,
                    label = "Galeri",
                    description = "Pilih dari galeri",
                    onClick = {
                        permissionType = "gallery"
                        
                        // Untuk Android 13+, tidak perlu permission untuk pick media
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            galleryLauncher.launch("image/*")
                        } else {
                            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                            
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, permission) -> {
                                    galleryLauncher.launch("image/*")
                                }
                                else -> {
                                    permissionLauncher.launch(permission)
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SakoSpacing.Large))

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal")
                }
            }
        }
    }
}

@Composable
private fun ImageSourceOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(SakoSpacing.Medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    permissionType: String,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Izin Diperlukan")
        },
        text = {
            val message = when (permissionType) {
                "camera" -> "Aplikasi memerlukan izin kamera untuk mengambil foto profil Anda. Silakan berikan izin di pengaturan aplikasi."
                "gallery" -> "Aplikasi memerlukan izin penyimpanan untuk memilih foto dari galeri. Silakan berikan izin di pengaturan aplikasi."
                else -> "Aplikasi memerlukan izin untuk melanjutkan."
            }
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Buka Pengaturan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * Create temporary image file
 */
private fun createTempImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "PROFILE_$timeStamp"
    val storageDir = context.cacheDir
    
    return File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
}

/**
 * Launch camera with FileProvider
 */
private fun launchCamera(
    context: android.content.Context,
    onReady: (File, Uri) -> Unit
) {
    try {
        val photoFile = createTempImageFile(context)
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        onReady(photoFile, photoUri)
    } catch (e: Exception) {
        android.util.Log.e("ImagePicker", "Error creating camera file", e)
    }
}
