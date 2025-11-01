package com.sako.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sako.R
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Loading Screen - Tampilan loading dengan circular progress
 *
 * @param message Pesan loading yang ditampilkan
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoLoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "Memuat data..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )

            VerticalSpacer(height = SakoSpacing.Medium)

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * SAKO Empty State - Tampilan ketika data kosong
 *
 * @param message Pesan yang ditampilkan
 * @param iconRes Resource ID untuk icon (optional)
 * @param actionText Text untuk tombol action (optional)
 * @param onActionClick Callback untuk tombol action (optional)
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(SakoSpacing.Large)
        ) {
            // Icon (optional)
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    alpha = 0.6f
                )

                VerticalSpacer(height = SakoSpacing.Large)
            }

            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Action button (optional)
            if (actionText != null && onActionClick != null) {
                VerticalSpacer(height = SakoSpacing.Large)

                SakoPrimaryButton(
                    text = actionText,
                    onClick = onActionClick,
                    modifier = Modifier.padding(horizontal = SakoSpacing.Large)
                )
            }
        }
    }
}

/**
 * SAKO Error State - Tampilan ketika terjadi error
 *
 * @param message Pesan error
 * @param onRetry Callback untuk retry action
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(SakoSpacing.Large)
        ) {
            // Error icon
            Image(
                painter = painterResource(id = R.drawable.warning), // warning.png
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            VerticalSpacer(height = SakoSpacing.Large)

            // Error message
            Text(
                text = "Terjadi Kesalahan",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            VerticalSpacer(height = SakoSpacing.Small)

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            VerticalSpacer(height = SakoSpacing.Large)

            // Retry button
            SakoPrimaryButton(
                text = "Coba Lagi",
                onClick = onRetry,
                modifier = Modifier.padding(horizontal = SakoSpacing.Large)
            )
        }
    }
}

/**
 * SAKO Content with State - Wrapper untuk handle loading, error, dan empty state
 *
 * @param isLoading State loading
 * @param isError State error
 * @param isEmpty State empty
 * @param errorMessage Pesan error
 * @param emptyMessage Pesan empty state
 * @param onRetry Callback retry untuk error
 * @param loadingMessage Pesan loading (optional)
 * @param content Content utama yang ditampilkan jika berhasil
 */
@Composable
fun SakoContentWithState(
    isLoading: Boolean,
    isError: Boolean,
    isEmpty: Boolean,
    errorMessage: String,
    emptyMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingMessage: String = "Memuat data...",
    content: @Composable () -> Unit
) {
    when {
        isLoading -> {
            SakoLoadingScreen(
                message = loadingMessage,
                modifier = modifier
            )
        }
        isError -> {
            SakoErrorState(
                message = errorMessage,
                onRetry = onRetry,
                modifier = modifier
            )
        }
        isEmpty -> {
            SakoEmptyState(
                message = emptyMessage,
                modifier = modifier
            )
        }
        else -> {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoLoadingScreenPreview() {
    SakoTheme {
        SakoLoadingScreen(message = "Memuat kuis...")
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoEmptyStatePreview() {
    SakoTheme {
        SakoEmptyState(
            message = "Belum ada video favorit",
            actionText = "Jelajahi Video",
            onActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoErrorStatePreview() {
    SakoTheme {
        SakoErrorState(
            message = "Tidak dapat memuat data. Periksa koneksi internet Anda.",
            onRetry = {}
        )
    }
}