package com.sako.ui.components

import androidx.annotation.DrawableRes
import com.sako.R

/**
 * Dialog Type Enum - Untuk menentukan tipe dialog status
 * Digunakan bersama SakoStatusDialog
 */
enum class DialogType(
    @DrawableRes val iconRes: Int,
    val defaultTitle: String,
    val defaultButtonText: String
) {
    SUCCESS(
        iconRes = R.drawable.success, // success.png
        defaultTitle = "Berhasil!",
        defaultButtonText = "Selesai"
    ),
    ERROR(
        iconRes = R.drawable.warning, // warning.png
        defaultTitle = "Gagal!",
        defaultButtonText = "Coba Lagi"
    ),
    INFO(
        iconRes = R.drawable.email, // email.png atau icon info lainnya
        defaultTitle = "Informasi",
        defaultButtonText = "Mengerti"
    ),
    EMAIL_SENT(
        iconRes = R.drawable.email, // email.png
        defaultTitle = "Tautan Reset Dikirim",
        defaultButtonText = "Masuk"
    ),
    PASSWORD_CHANGED(
        iconRes = R.drawable.key, // key.png
        defaultTitle = "Berhasil Ubah Password",
        defaultButtonText = "Masuk"
    )
}

/**
 * Dialog State - State management untuk dialog
 */
data class DialogState(
    val isVisible: Boolean = false,
    val type: DialogType = DialogType.INFO,
    val title: String? = null,
    val message: String? = null,
    val buttonText: String? = null,
    val onConfirm: (() -> Unit)? = null
) {
    /**
     * Helper untuk mendapatkan title, dengan fallback ke default
     */
    fun getTitle(): String = title ?: type.defaultTitle

    /**
     * Helper untuk mendapatkan button text, dengan fallback ke default
     */
    fun getButtonText(): String = buttonText ?: type.defaultButtonText
}

/**
 * Confirmation Dialog State - State management untuk confirmation dialog
 */
data class ConfirmationDialogState(
    val isVisible: Boolean = false,
    val title: String = "",
    val message: String? = null,
    val confirmButtonText: String = "Konfirmasi",
    val cancelButtonText: String = "Batalkan",
    val onConfirm: (() -> Unit)? = null
)