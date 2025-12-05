package com.sako.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sako.R
import com.sako.ui.components.*
import com.sako.ui.theme.SakoTheme
import com.sako.viewmodel.AuthViewModel
import com.sako.utils.Resource

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Error states untuk validasi
    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    
    // UI states
    var isLoading by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()
    
    // Handle register state changes
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is Resource.Success -> {
                isLoading = false
                showSuccessDialog = true
                viewModel.clearRegisterState()
            }
            is Resource.Error -> {
                isLoading = false
                errorMessage = state.error
                showErrorSnackbar = true
            }
            is Resource.Loading -> {
                isLoading = true
                showErrorSnackbar = false
            }
            null -> {
                isLoading = false
            }
        }
    }
    
    // Validation functions
    fun validateFullName(): Boolean {
        return when {
            fullName.isBlank() -> {
                fullNameError = "Nama lengkap tidak boleh kosong"
                false
            }
            fullName.length < 2 -> {
                fullNameError = "Nama lengkap minimal 2 karakter"
                false
            }
            else -> {
                fullNameError = ""
                true
            }
        }
    }
    
    fun validateEmail(): Boolean {
        return when {
            email.isBlank() -> {
                emailError = "Email tidak boleh kosong"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Format email tidak valid"
                false
            }
            else -> {
                emailError = ""
                true
            }
        }
    }
    
    fun validatePassword(): Boolean {
        return when {
            password.isBlank() -> {
                passwordError = "Kata sandi tidak boleh kosong"
                false
            }
            password.length < 6 -> {
                passwordError = "Kata sandi minimal 6 karakter"
                false
            }
            else -> {
                passwordError = ""
                true
            }
        }
    }
    
    fun validateConfirmPassword(): Boolean {
        return when {
            confirmPassword.isBlank() -> {
                confirmPasswordError = "Konfirmasi kata sandi tidak boleh kosong"
                false
            }
            confirmPassword != password -> {
                confirmPasswordError = "Kata sandi tidak cocok"
                false
            }
            else -> {
                confirmPasswordError = ""
                true
            }
        }
    }
    
    fun performRegister() {
        val isFullNameValid = validateFullName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()
        
        if (isFullNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
            viewModel.register(fullName, email, password)
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        SakoStatusDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onRegisterSuccess()
            },
            icon = painterResource(id = R.drawable.success),
            title = "Pendaftaran Berhasil!",
            message = "Akun Anda telah berhasil dibuat. Silakan login untuk melanjutkan.",
            buttonText = "Lanjut ke Login",
            onConfirm = {
                showSuccessDialog = false
                onRegisterSuccess()
            }
        )
    }
    
    SakoTheme {
        Scaffold(
            snackbarHost = {
                // Sticky Error Bar
                if (showErrorSnackbar) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.warning),
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { showErrorSnackbar = false }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            BackgroundImage(alpha = 0.1f) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.sako),
                        contentDescription = "Logo SAKO",
                        modifier = Modifier.size(120.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quote text
                    Text(
                        text = "\"Di sini nanti quotes\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Email Field
                    SakoTextInputField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (emailError.isNotEmpty()) validateEmail()
                        },
                        label = "Email",
                        placeholder = "Masukkan Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        isError = emailError.isNotEmpty(),
                        errorMessage = emailError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Full Name Field
                    SakoTextInputField(
                        value = fullName,
                        onValueChange = { 
                            fullName = it
                            if (fullNameError.isNotEmpty()) validateFullName()
                        },
                        label = "Nama Lengkap",
                        placeholder = "Masukkan Nama Lengkap",
                        leadingIcon = Icons.Default.Person,
                        isError = fullNameError.isNotEmpty(),
                        errorMessage = fullNameError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password Field
                    SakoTextInputField(
                        value = password,
                        onValueChange = { 
                            password = it
                            if (passwordError.isNotEmpty()) validatePassword()
                        },
                        label = "Kata Sandi",
                        placeholder = "Masukkan Kata Sandi",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        isError = passwordError.isNotEmpty(),
                        errorMessage = passwordError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Confirm Password Field
                    SakoTextInputField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            if (confirmPasswordError.isNotEmpty()) validateConfirmPassword()
                        },
                        label = "Konfirmasi Kata Sandi",
                        placeholder = "Masukkan Ulang Kata Sandi",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                            ) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        isError = confirmPasswordError.isNotEmpty(),
                        errorMessage = confirmPasswordError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Register Button
                    SakoPrimaryButton(
                        text = if (isLoading) "Mendaftar..." else "Daftar",
                        onClick = { performRegister() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Login Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sudah punya akun? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Login!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}