package com.sako.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SakoShapes = Shapes(
    // Extra Small - untuk chips, tags kecil
    extraSmall = RoundedCornerShape(2.dp),

    // Small - untuk buttons kecil, input fields
    small = RoundedCornerShape(4.dp),

    // Medium - untuk cards, containers standar
    medium = RoundedCornerShape(8.dp),

    // Large - untuk dialogs, bottom sheets
    large = RoundedCornerShape(16.dp),

    // Extra Large - untuk modals, image containers besar
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes untuk komponen spesifik SAKO
object SakoCustomShapes {
    // Untuk quiz card
    val quizCard = RoundedCornerShape(12.dp)

    // Untuk video thumbnail
    val videoThumbnail = RoundedCornerShape(8.dp)

    // Untuk badge/achievement
    val badge = RoundedCornerShape(50) // Circular

    // Untuk profile image
    val profileImage = RoundedCornerShape(50) // Circular

    // Untuk map location card
    val mapLocationCard = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Untuk bottom navigation bar
    val bottomNav = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Untuk progress indicator
    val progressBar = RoundedCornerShape(8.dp)

    // Untuk alert dialog
    val alertDialog = RoundedCornerShape(20.dp)

    // Untuk text input field
    val textInput = RoundedCornerShape(12.dp)

    // Untuk FAB (Floating Action Button)
    val fab = RoundedCornerShape(16.dp)

    // Untuk category card
    val categoryCard = RoundedCornerShape(16.dp)

    // Untuk level item dalam grid
    val levelItem = RoundedCornerShape(12.dp)

    // Untuk review/ulasan card
    val reviewCard = RoundedCornerShape(12.dp)
}