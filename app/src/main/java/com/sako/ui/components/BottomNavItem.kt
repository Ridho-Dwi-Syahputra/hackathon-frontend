package com.sako.ui.components

import androidx.compose.ui.graphics.painter.Painter

/**
 * SAKO Bottom Navigation Item - Model data untuk item navigasi
 *
 * @param label Label yang ditampilkan di bawah icon
 * @param icon Icon painter dari drawable resource
 * @param route Route navigasi yang sesuai dengan Screen.kt
 */
data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: String
)