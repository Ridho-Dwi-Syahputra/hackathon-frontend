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
    // Home Screen Components
    val userLevelCard = RoundedCornerShape(24.dp)
    val featureCard = RoundedCornerShape(20.dp)
    val statCard = RoundedCornerShape(16.dp)
    val popularVideoCard = RoundedCornerShape(16.dp)
    
    // Video Screen Components
    val videoCard = RoundedCornerShape(20.dp)
    val videoThumbnail = RoundedCornerShape(20.dp)
    val filterChip = RoundedCornerShape(24.dp)
    val playButtonOverlay = RoundedCornerShape(28.dp)
    
    // Quiz Components
    val quizCard = RoundedCornerShape(12.dp)

    // Badge & Profile
    val badge = RoundedCornerShape(50) // Circular
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