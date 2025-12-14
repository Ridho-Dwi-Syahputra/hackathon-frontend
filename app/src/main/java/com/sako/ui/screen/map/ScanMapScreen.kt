package com.sako.ui.screen.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.sako.data.remote.response.ScanQRData
import com.sako.ui.components.BackgroundImage
import com.sako.utils.Resource
import com.sako.utils.LocationHelper
import com.sako.utils.LocationException
import com.sako.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanMapScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorTitle by remember { mutableStateOf("Gagal Scan QR") }
    var errorIcon by remember { mutableStateOf(Icons.Default.Error) }
    
    val scanResult by viewModel.scanResult.collectAsState()

    // Request permissions when screen loads
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status !is PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (locationPermissionState.status !is PermissionStatus.Granted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Handle scan result
    LaunchedEffect(scanResult) {
        when (val result = scanResult) {
            is Resource.Success<ScanQRData> -> {
                isProcessing = false
                showSuccessDialog = true
            }
            is Resource.Error -> {
                isProcessing = false
                
                // Log error for debugging
                android.util.Log.d("SCAN_QR", "❌ Error received: ${result.error}")
                
                // Customize error dialog based on error message
                when {
                    result.error.contains("sudah pernah dikunjungi", ignoreCase = true) ||
                    result.error.contains("sudah berkunjung", ignoreCase = true) ||
                    result.error.contains("telah berkunjung", ignoreCase = true) ||
                    result.error.contains("already visited", ignoreCase = true) -> {
                        errorTitle = "Sudah Pernah Dikunjungi"
                        errorIcon = Icons.Default.CheckCircle
                        errorMessage = result.error
                    }
                    result.error.contains("tidak ditemukan", ignoreCase = true) ||
                    result.error.contains("not found", ignoreCase = true) ||
                    result.error.contains("tidak valid", ignoreCase = true) ||
                    result.error.contains("invalid", ignoreCase = true) -> {
                        errorTitle = "QR Code Tidak Valid"
                        errorIcon = Icons.Default.Warning
                        errorMessage = "QR Code yang Anda scan tidak terdaftar dalam sistem. Pastikan Anda scan QR Code resmi dari tempat wisata SAKO."
                    }
                    result.error.contains("terlalu jauh", ignoreCase = true) ||
                    result.error.contains("jarak", ignoreCase = true) -> {
                        errorTitle = "Lokasi Terlalu Jauh"
                        errorIcon = Icons.Default.LocationOff
                        errorMessage = result.error
                    }
                    else -> {
                        errorTitle = "Gagal Scan QR"
                        errorIcon = Icons.Default.Error
                        errorMessage = result.error
                    }
                }
                
                showErrorDialog = true
            }
            is Resource.Loading -> {
                isProcessing = true
            }
            null -> {}
        }
    }

    BackgroundImage {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Scan QR Lokasi") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            }
        ) { paddingValues ->
            when {
                cameraPermissionState.status !is PermissionStatus.Granted || locationPermissionState.status !is PermissionStatus.Granted -> {
                    // Permission not granted
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Izin Diperlukan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aplikasi memerlukan izin kamera dan lokasi untuk scan QR code.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    if (cameraPermissionState.status !is PermissionStatus.Granted) {
                                        cameraPermissionState.launchPermissionRequest()
                                    }
                                    if (locationPermissionState.status !is PermissionStatus.Granted) {
                                        locationPermissionState.launchPermissionRequest()
                                    }
                                }
                            ) {
                                Text("Berikan Izin")
                            }
                        }
                    }
                }
                isProcessing -> {
                    // Processing checkin
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Memproses check-in...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                else -> {
                    // Camera Preview
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        CameraPreview(
                            onQrCodeScanned = { code ->
                                if (scannedCode != code && !isProcessing) {
                                    scannedCode = code
                                    isProcessing = true
                                    
                                    android.util.Log.d("SCAN_QR", "🔍 QR Code detected: $code")
                                    
                                    // Get current location and scan QR code with location validation
                                    kotlinx.coroutines.MainScope().launch {
                                        try {
                                            android.util.Log.d("SCAN_QR", "📍 Getting current location...")
                                            val location = LocationHelper.getCurrentLocation(context)
                                            
                                            android.util.Log.d("SCAN_QR", "✅ Location obtained: ${location.latitude}, ${location.longitude}")
                                            
                                            // Scan QR code with user coordinates
                                            viewModel.scanQRCode(
                                                qrToken = code,
                                                userLatitude = location.latitude,
                                                userLongitude = location.longitude
                                            )
                                        } catch (e: LocationException) {
                                            errorMessage = "Tidak dapat mendapatkan lokasi: ${e.message}"
                                            showErrorDialog = true
                                            isProcessing = false
                                            scannedCode = null
                                        } catch (e: Exception) {
                                            errorMessage = "Terjadi kesalahan: ${e.message}"
                                            showErrorDialog = true
                                            isProcessing = false
                                            scannedCode = null
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Scanning overlay
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Arahkan kamera ke QR code lokasi",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("Check-in Berhasil! 🎉") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        (scanResult as? Resource.Success)?.data?.let { data ->
                            // XP Reward Info (Prominent)
                            data.rewardInfo?.let { reward ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "+${reward.xpEarned} XP",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Total XP: ${reward.totalXp}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            
                            // Place Info
                            data.touristPlace?.let { place ->
                                Text(
                                    text = "Lokasi: ${place.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            // Visit Time
                            data.visitedAt?.let { visitTime ->
                                Text(
                                    text = "Waktu kunjungan: $visitTime",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            // Location Validation Info
                            data.locationValidation?.let { validation ->
                                Text(
                                    text = "Jarak: ${LocationHelper.formatDistance(validation.distanceMeters)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetScanResult()
                            showSuccessDialog = false
                            scannedCode = null
                            (scanResult as? Resource.Success)?.data?.touristPlace?.id?.let { placeId ->
                                onNavigateToDetail(placeId)
                            } ?: onNavigateBack()
                        }
                    ) {
                        Text("Lihat Detail")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetScanResult()
                            showSuccessDialog = false
                            scannedCode = null
                            onNavigateBack()
                        }
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }

        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                    scannedCode = null
                },
                icon = {
                    Icon(
                        imageVector = errorIcon,
                        contentDescription = null,
                        tint = when (errorIcon) {
                            Icons.Default.CheckCircle -> MaterialTheme.colorScheme.primary
                            Icons.Default.Warning -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                },
                title = { Text(errorTitle) },
                text = { 
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetScanResult()
                            showErrorDialog = false
                            scannedCode = null
                        }
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun CameraPreview(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy, onQrCodeScanned)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = modifier
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQrCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.format == Barcode.FORMAT_QR_CODE) {
                        barcode.rawValue?.let { value ->
                            onQrCodeScanned(value)
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}