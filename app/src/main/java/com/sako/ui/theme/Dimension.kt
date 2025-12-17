package com.sako.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Sistem dimensi terpusat untuk aplikasi SAKO
 * Mengikuti Material Design spacing guidelines dengan kustomisasi
 */
object SakoDimensions {
    // Padding & Margin System (4dp increment)
    val paddingExtraSmall = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 12.dp
    val paddingNormal = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp
    val paddingHuge = 48.dp
    
    // Spacing between elements
    val spacingExtraSmall = 4.dp
    val spacingSmall = 8.dp
    val spacingMedium = 12.dp
    val spacingNormal = 16.dp
    val spacingLarge = 24.dp
    val spacingExtraLarge = 32.dp
    
    // Elevation levels
    val elevationNone = 0.dp
    val elevationSmall = 2.dp
    val elevationMedium = 4.dp
    val elevationLarge = 8.dp
    val elevationExtraLarge = 16.dp
    
    // Icon sizes
    val iconExtraSmall = 16.dp
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    val iconExtraLarge = 48.dp
    val iconHuge = 64.dp
    
    // Component-specific sizes
    val buttonHeight = 48.dp
    val buttonHeightSmall = 36.dp
    val buttonHeightLarge = 56.dp
    
    val cardMinHeight = 80.dp
    val thumbnailHeight = 180.dp
    val thumbnailHeightSmall = 120.dp
    
    val fabSize = 56.dp
    val fabSizeSmall = 40.dp
    
    val chipHeight = 32.dp
    val chipPaddingHorizontal = 16.dp
    
    val dividerThickness = 1.dp
    val dividerThicknessThick = 2.dp
    
    // Corner radius (already defined in Shape.kt, but for inline use)
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusNormal = 16.dp
    val cornerRadiusLarge = 20.dp
    val cornerRadiusExtraLarge = 24.dp
    val cornerRadiusFull = 50 // percentage for fully rounded
    
    // Specific component dimensions
    val userLevelCardHeight = 120.dp
    val statCardSize = 100.dp
    val featureCardMinHeight = 100.dp
    val videoCardThumbnailAspectRatio = 16f / 9f
}
