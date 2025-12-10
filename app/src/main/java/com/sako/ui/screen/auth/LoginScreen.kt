package com.sako.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import com.sako.data.pref.UserModel
import com.sako.data.pref.UserPreference
import com.sako.ui.components.BackgroundImage
import com.sako.ui.components.SakoPrimaryButton
import com.sako.ui.components.SakoStatusDialog
import com.sako.ui.components.SakoTextInputField
import com.sako.ui.theme.SakoPrimary
import com.sako.utils.Resource
import com.sako.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel,
    userPreference: UserPreference
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    // Loading state
    var isLoading by remember { mutableStateOf(false) }
    
    // Dialog state
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    // Observe login state
    val loginState by viewModel.loginState.collectAsState()
    
    // Handle login state
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Loading -> {
                isLoading = true
            }
            is Resource.Success -> {
                isLoading = false
                // Session sudah disimpan di AuthRepository, tidak perlu save lagi di sini
                viewModel.clearLoginState()
                isSuccess = true
                dialogMessage = "Login berhasil! Selamat datang kembali."
                showDialog = true
            }
            is Resource.Error -> {
                isLoading = false
                isSuccess = false
                dialogMessage = state.error
                showDialog = true
            }
            null -> {
                isLoading = false
            }
        }
    }
    
    // Validation functions
    fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            emailError = "Email tidak boleh kosong"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email tidak valid"
            false
        } else {
            emailError = null
            true
        }
    }
    
    fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            passwordError = "Kata sandi tidak boleh kosong"
            false
        } else if (password.length < 6) {
            passwordError = "Kata sandi minimal 6 karakter"
            false
        } else {
            passwordError = null
            true
        }
    }
    
    fun performLogin() {
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)
        
        if (isEmailValid && isPasswordValid) {
            viewModel.login(email.trim(), password)
        }
    }

    BackgroundImage {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Logo
            Image(
                painter = painterResource(id = R.drawable.sako),
                contentDescription = "Logo Sako",
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(0.6f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SakoPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email field
            SakoTextInputField(
                value = email,
                onValueChange = { 
                    email = it
                    if (emailError != null) validateEmail(it)
                },
                label = "Email",
                placeholder = "Masukkan email Anda",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = emailError != null,
                errorMessage = emailError ?: "",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password field
            SakoTextInputField(
                value = password,
                onValueChange = { 
                    password = it
                    if (passwordError != null) validatePassword(it)
                },
                label = "Kata Sandi",
                placeholder = "Masukkan kata sandi Anda",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = passwordError != null,
                errorMessage = passwordError ?: "",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Login button
            SakoPrimaryButton(
                text = if (isLoading) "Masuk..." else "Masuk",
                onClick = { performLogin() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Register link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Daftar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SakoPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Status dialog
        if (showDialog) {
            SakoStatusDialog(
                onDismissRequest = { 
                    showDialog = false
                    if (isSuccess) {
                        onLoginSuccess()
                    }
                },
                icon = painterResource(id = if (isSuccess) R.drawable.success else R.drawable.warning),
                title = if (isSuccess) "Login Berhasil" else "Login Gagal",
                message = dialogMessage,
                buttonText = if (isSuccess) "Lanjutkan" else "Coba Lagi",
                onConfirm = { 
                    showDialog = false
                    if (isSuccess) {
                        onLoginSuccess()
                    }
                }
            )
        }
    }
}