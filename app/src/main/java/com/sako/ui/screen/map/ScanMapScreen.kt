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
import com.sako.ui.components.BackgroundImage
import com.sako.utils.Resource
import com.sako.viewmodel.MapViewModel
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
    
    val checkinResult by viewModel.checkinResult.collectAsState()

    // Request permissions when screen loads
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status !is PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (locationPermissionState.status !is PermissionStatus.Granted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Handle checkin result
    LaunchedEffect(checkinResult) {
        when (val result = checkinResult) {
            is Resource.Success -> {
                isProcessing = false
                showSuccessDialog = true
            }
            is Resource.Error -> {
                isProcessing = false
                errorMessage = result.error
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
                                    
                                    // Get current location and checkin
                                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                    try {
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                            if (location != null) {
                                                viewModel.checkinLocation(
                                                    qrToken = code,
                                                    latitude = location.latitude,
                                                    longitude = location.longitude
                                                )
                                            } else {
                                                errorMessage = "Tidak dapat mendapatkan lokasi"
                                                showErrorDialog = true
                                                isProcessing = false
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        errorMessage = "Izin lokasi tidak diberikan"
                                        showErrorDialog = true
                                        isProcessing = false
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
                title = { Text("Check-in Berhasil!") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (checkinResult as? Resource.Success)?.data?.let { data ->
                            Text("Selamat! Anda telah check-in di ${data.place.name}")
                            Text("XP yang didapat: +${data.xpEarned}")
                            Text("Total XP: ${data.newTotalXp}")
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetCheckinResult()
                            showSuccessDialog = false
                            (checkinResult as? Resource.Success)?.data?.place?.id?.let { placeId ->
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
                            viewModel.resetCheckinResult()
                            showSuccessDialog = false
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
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Gagal Check-in") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetCheckinResult()
                            showErrorDialog = false
                            scannedCode = null
                        }
                    ) {
                        Text("Coba Lagi")
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