package com.sako.data.model

/**
 * Data class untuk informasi level user
 * Digunakan untuk menampilkan progress XP dan nama level
 */
data class LevelInfo(
    val currentLevel: Int,
    val levelName: String,
    val currentLevelXp: Int,
    val nextLevelXp: Int,
    val progressPercent: Float
)
