package com.sako.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Text Input Field - Standardized text input untuk seluruh aplikasi
 *
 * @param value Nilai text saat ini
 * @param onValueChange Callback ketika text berubah
 * @param label Label yang ditampilkan di input field
 * @param modifier Modifier untuk styling tambahan
 * @param leadingIcon Icon di sebelah kiri (optional)
 * @param trailingIcon Icon di sebelah kanan (optional)
 * @param isError State error untuk validasi
 * @param errorMessage Pesan error yang ditampilkan
 * @param enabled State enabled/disabled input
 * @param readOnly State read-only input
 * @param visualTransformation Transformasi visual (password, dll)
 * @param keyboardOptions Opsi keyboard (type, ime action, dll)
 * @param placeholder Placeholder text
 * @param singleLine Apakah input single line atau multiline
 * @param maxLines Maksimal baris untuk multiline input
 */
@Composable
fun SakoTextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = if (leadingIcon != null) {
            { Icon(imageVector = leadingIcon, contentDescription = label) }
        } else null,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = if (isError && errorMessage.isNotEmpty()) {
            { Text(text = errorMessage) }
        } else null,
        enabled = enabled,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder) }
        } else null,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@Preview(showBackground = true)
@Composable
private fun SakoTextInputFieldPreview() {
    SakoTheme {
        SakoTextInputField(
            value = "user@example.com",
            onValueChange = {},
            label = "Email",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoTextInputFieldErrorPreview() {
    SakoTheme {
        SakoTextInputField(
            value = "invalid-email",
            onValueChange = {},
            label = "Email",
            leadingIcon = Icons.Default.Email,
            isError = true,
            errorMessage = "Format email tidak valid"
        )
    }
}