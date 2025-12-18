package com.sako.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.components.SakoTextInputField
import androidx.compose.foundation.text.KeyboardOptions
import com.sako.viewmodel.ProfileViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Edit Profile Screen - Halaman untuk mengedit profil pengguna
 * User bisa mengubah nama lengkap dan email
 * Integrated dengan ProfileViewModel & changeProfileController.js backend
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var fullName by remember { mutableStateOf(uiState.userData?.fullName ?: "") }
    var email by remember { mutableStateOf(uiState.userData?.email ?: "") }
    
    // Update fields when userData loads
    LaunchedEffect(uiState.userData) {
        uiState.userData?.let {
            fullName = it.fullName
            email = it.email
        }
    }
    
    // Clear messages when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUpdateStatus()
        }
    }
    
    // Validation states
    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show success snackbar and navigate back
    LaunchedEffect(uiState.updateSuccess, uiState.updateMessage) {
        if (uiState.updateSuccess && uiState.updateMessage != null) {
            snackbarHostState.showSnackbar(
                message = uiState.updateMessage ?: "Profil berhasil diperbarui",
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(1500)
            viewModel.clearUpdateStatus()
            onNavigateBack()
        }
    }
    
    // Show error dialog
    if (uiState.error != null && !uiState.isLoading) {
        AlertDialog(
            onDismissRequest = { viewModel.clearUpdateStatus() },
            title = { Text("Gagal Update Profil") },
            text = { Text(uiState.error ?: "Terjadi kesalahan") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearUpdateStatus() }) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    
    BackgroundImage {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Content column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 64.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Perbarui informasi profil Anda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Full Name Input
                SakoTextInputField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        fullNameError = if (it.isBlank()) "Nama lengkap tidak boleh kosong" else ""
                    },
                    label = "Nama Lengkap",
                    leadingIcon = Icons.Default.Person,
                    placeholder = "Masukkan nama lengkap",
                    isError = fullNameError.isNotEmpty(),
                    errorMessage = fullNameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                
                // Email Input
                SakoTextInputField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = when {
                            it.isBlank() -> "Email tidak boleh kosong"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() -> 
                                "Format email tidak valid"
                            else -> ""
                        }
                    },
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    placeholder = "contoh@email.com",
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Save Button
                SakoPrimaryButton(
                    text = if (uiState.isLoading) "Menyimpan..." else "Simpan Perubahan",
                    onClick = {
                        // Validate
                        if (fullName.isBlank()) {
                            fullNameError = "Nama lengkap tidak boleh kosong"
                            return@SakoPrimaryButton
                        }
                        if (email.isBlank()) {
                            emailError = "Email tidak boleh kosong"
                            return@SakoPrimaryButton
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Format email tidak valid"
                            return@SakoPrimaryButton
                        }
                        
                        // Call ViewModel to update profile via backend
                        viewModel.updateProfile(fullName, email)
                    },
                    enabled = !uiState.isLoading && fullName.isNotBlank() && email.isNotBlank() &&
                            fullNameError.isEmpty() && emailError.isEmpty()
                )
                
                // Information Text
                Text(
                    text = "Catatan: Perubahan akan disimpan ke akun Anda",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                }
                
                // Back button - floating di atas dengan zIndex
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 2.dp, start = 2.dp)
                        .zIndex(10f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}