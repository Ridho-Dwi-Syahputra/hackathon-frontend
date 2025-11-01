package com.sako.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sako.ui.theme.SakoCustomShapes
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Confirmation Dialog - Dialog konfirmasi dengan dua tombol
 * Digunakan untuk: Logout, Ganti Password, Hapus/Ubah Ulasan
 *
 * @param onDismissRequest Callback ketika dialog di-dismiss
 * @param title Judul dialog
 * @param message Pesan dialog (optional)
 * @param confirmButtonText Text tombol konfirmasi
 * @param onConfirm Callback ketika tombol konfirmasi diklik
 * @param cancelButtonText Text tombol batal
 */
@Composable
fun SakoConfirmationDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    cancelButtonText: String = "Batalkan"
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            SakoPrimaryButton(
                text = confirmButtonText,
                onClick = {
                    onConfirm()
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            SakoSecondaryButton(
                text = cancelButtonText,
                onClick = onDismissRequest
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = if (message != null) {
            {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else null,
        shape = SakoCustomShapes.alertDialog,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    )
}

/**
 * SAKO Status Dialog - Dialog status dengan satu tombol
 * Digunakan untuk: Sukses, Gagal, Info notifications
 *
 * @param onDismissRequest Callback ketika dialog di-dismiss
 * @param icon Icon yang ditampilkan (success, warning, email, key)
 * @param title Judul dialog
 * @param message Pesan dialog (optional)
 * @param buttonText Text tombol
 * @param onConfirm Callback ketika tombol diklik
 */
@Composable
fun SakoStatusDialog(
    onDismissRequest: () -> Unit,
    icon: Painter,
    title: String,
    buttonText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = SakoCustomShapes.alertDialog,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Image(
                    painter = icon,
                    contentDescription = title,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Message (optional)
                if (message != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button
                SakoPrimaryButton(
                    text = buttonText,
                    onClick = {
                        onConfirm()
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

/**
 * SAKO Image Picker Dialog - Dialog untuk memilih foto profil
 * Digunakan untuk: Edit Profile (pilih dari kamera atau galeri)
 *
 * @param onDismissRequest Callback ketika dialog di-dismiss
 * @param onCameraClick Callback ketika tombol kamera diklik
 * @param onGalleryClick Callback ketika tombol galeri diklik
 */
@Composable
fun SakoImagePickerDialog(
    onDismissRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = SakoCustomShapes.alertDialog,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Pilih Foto Profil",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Kamera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SakoPrimaryButton(
                        text = "Ambil Foto dari Kamera",
                        onClick = {
                            onCameraClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Galeri
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SakoSecondaryButton(
                        text = "Pilih dari Galeri",
                        onClick = {
                            onGalleryClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Preview untuk development
@Preview(showBackground = true)
@Composable
private fun SakoConfirmationDialogPreview() {
    SakoTheme {
        SakoConfirmationDialog(
            onDismissRequest = {},
            title = "Apakah kamu yakin ingin Logout?",
            confirmButtonText = "Logout",
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoStatusDialogPreview() {
    SakoTheme {
        SakoStatusDialog(
            onDismissRequest = {},
            icon = painterResource(id = android.R.drawable.ic_dialog_info), // Placeholder
            title = "Pendaftaran Berhasil!",
            message = "Silakan login dengan akun baru Anda",
            buttonText = "Masuk",
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoImagePickerDialogPreview() {
    SakoTheme {
        SakoImagePickerDialog(
            onDismissRequest = {},
            onCameraClick = {},
            onGalleryClick = {}
        )
    }
}