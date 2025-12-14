package com.sako.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.components.SakoTextInputField
import androidx.compose.foundation.text.KeyboardOptions
import com.sako.viewmodel.ProfileViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Change Password Screen - Halaman untuk mengubah password
 * User memasukkan password lama, password baru, dan konfirmasi password baru
 * Integrated dengan ProfileViewModel & changeProfileController.js backend
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Password visibility states
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Validation states
    var oldPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show success snackbar and navigate back
    LaunchedEffect(uiState.updateSuccess, uiState.updateMessage) {
        if (uiState.updateSuccess && uiState.updateMessage != null) {
            snackbarHostState.showSnackbar(
                message = uiState.updateMessage ?: "Password berhasil diubah",
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
            title = { Text("Gagal Ubah Password") },
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
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column {
                            Text(
                                text = "Keamanan Akun",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Pastikan password baru minimal 6 karakter",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Old Password Input
                SakoTextInputField(
                    value = oldPassword,
                    onValueChange = { 
                        oldPassword = it
                        oldPasswordError = if (it.isBlank()) "Password lama tidak boleh kosong" else ""
                    },
                    label = "Password Lama",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                            Icon(
                                imageVector = if (oldPasswordVisible) 
                                    Icons.Default.Visibility 
                                else 
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (oldPasswordVisible) 
                                    "Sembunyikan password" 
                                else 
                                    "Tampilkan password"
                            )
                        }
                    },
                    placeholder = "Masukkan password lama",
                    isError = oldPasswordError.isNotEmpty(),
                    errorMessage = oldPasswordError,
                    visualTransformation = if (oldPasswordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
                
                // New Password Input
                SakoTextInputField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        newPasswordError = when {
                            it.isBlank() -> "Password baru tidak boleh kosong"
                            it.length < 6 -> "Password minimal 6 karakter"
                            it == oldPassword -> "Password baru tidak boleh sama dengan password lama"
                            else -> ""
                        }
                        // Revalidate confirm password if already filled
                        if (confirmPassword.isNotBlank()) {
                            confirmPasswordError = if (confirmPassword != it) 
                                "Password tidak cocok" 
                            else 
                                ""
                        }
                    },
                    label = "Password Baru",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) 
                                    Icons.Default.Visibility 
                                else 
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (newPasswordVisible) 
                                    "Sembunyikan password" 
                                else 
                                    "Tampilkan password"
                            )
                        }
                    },
                    placeholder = "Minimal 6 karakter",
                    isError = newPasswordError.isNotEmpty(),
                    errorMessage = newPasswordError,
                    visualTransformation = if (newPasswordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
                
                // Confirm Password Input
                SakoTextInputField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        confirmPasswordError = when {
                            it.isBlank() -> "Konfirmasi password tidak boleh kosong"
                            it != newPassword -> "Password tidak cocok"
                            else -> ""
                        }
                    },
                    label = "Konfirmasi Password Baru",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) 
                                    Icons.Default.Visibility 
                                else 
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) 
                                    "Sembunyikan password" 
                                else 
                                    "Tampilkan password"
                            )
                        }
                    },
                    placeholder = "Ketik ulang password baru",
                    isError = confirmPasswordError.isNotEmpty(),
                    errorMessage = confirmPasswordError,
                    visualTransformation = if (confirmPasswordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Change Password Button
                SakoPrimaryButton(
                    text = if (uiState.isLoading) "Mengubah Password..." else "Ubah Password",
                    onClick = {
                        // Validate all fields
                        if (oldPassword.isBlank()) {
                            oldPasswordError = "Password lama tidak boleh kosong"
                            return@SakoPrimaryButton
                        }
                        if (newPassword.isBlank()) {
                            newPasswordError = "Password baru tidak boleh kosong"
                            return@SakoPrimaryButton
                        }
                        if (newPassword.length < 6) {
                            newPasswordError = "Password minimal 6 karakter"
                            return@SakoPrimaryButton
                        }
                        if (newPassword == oldPassword) {
                            newPasswordError = "Password baru tidak boleh sama dengan password lama"
                            return@SakoPrimaryButton
                        }
                        if (confirmPassword != newPassword) {
                            confirmPasswordError = "Password tidak cocok"
                            return@SakoPrimaryButton
                        }
                        
                        // Call ViewModel to change password via backend
                        viewModel.changePassword(oldPassword, newPassword)
                    },
                    enabled = !uiState.isLoading && 
                            oldPassword.isNotBlank() && 
                            newPassword.isNotBlank() && 
                            confirmPassword.isNotBlank() &&
                            oldPasswordError.isEmpty() && 
                            newPasswordError.isEmpty() && 
                            confirmPasswordError.isEmpty()
                )
                
                // Password Requirements
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Persyaratan Password:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        PasswordRequirement(
                            text = "Minimal 6 karakter",
                            isMet = newPassword.length >= 6
                        )
                        PasswordRequirement(
                            text = "Berbeda dengan password lama",
                            isMet = newPassword.isNotBlank() && newPassword != oldPassword
                        )
                        PasswordRequirement(
                            text = "Password cocok dengan konfirmasi",
                            isMet = confirmPassword.isNotBlank() && confirmPassword == newPassword
                        )
                    }
                }
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

@Composable
fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isMet) 
                Icons.Default.Lock 
            else 
                Icons.Default.Lock,
            contentDescription = null,
            tint = if (isMet) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isMet) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}