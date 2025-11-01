package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Video Query Request (untuk search/filter)
data class VideoQueryRequest(
    @SerializedName("search")
    val search: String? = null,

    @SerializedName("kategori")
    val kategori: String? = null,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("limit")
    val limit: Int = 20
)

// Favorite Video Request
data class FavoriteVideoRequest(
    @SerializedName("video_id")
    val videoId: String
)