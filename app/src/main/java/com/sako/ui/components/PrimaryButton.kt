package com.sako.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Primary Button - Tombol utama aplikasi dengan warna merah marawa
 *
 * @param text Text yang ditampilkan di button
 * @param onClick Callback ketika button diklik
 * @param modifier Modifier untuk styling tambahan
 * @param enabled State enabled/disabled button
 */
@Composable
fun SakoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * SAKO Secondary Button - Tombol sekunder dengan outline
 *
 * @param text Text yang ditampilkan di button
 * @param onClick Callback ketika button diklik
 * @param modifier Modifier untuk styling tambahan
 * @param enabled State enabled/disabled button
 */
@Composable
fun SakoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoPrimaryButtonPreview() {
    SakoTheme {
        SakoPrimaryButton(
            text = "Masuk",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoPrimaryButtonDisabledPreview() {
    SakoTheme {
        SakoPrimaryButton(
            text = "Masuk",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoSecondaryButtonPreview() {
    SakoTheme {
        SakoSecondaryButton(
            text = "Batalkan",
            onClick = {}
        )
    }
}