package com.sako.data.pref

data class UserModel(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val totalXp: Int = 0,
    val status: String = "active",
    val userImageUrl: String? = null,
    val accessToken: String = "",
    val databaseToken: String = "",
    val fcmToken: String? = null,
    val isLogin: Boolean = false
)