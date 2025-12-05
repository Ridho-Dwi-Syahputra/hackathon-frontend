package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class VideoListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: VideoListData
)

data class VideoListData(
    @SerializedName("videos")
    val videos: List<VideoItem>,

    @SerializedName("pagination")
    val pagination: Pagination?
)

data class Pagination(
    @SerializedName("page")
    val page: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)

data class VideoItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("judul")
    val judul: String,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("youtube_url")
    val youtubeUrl: String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("is_active")
    val isActive: Int,  // Backend returns 1/0 instead of true/false

    @SerializedName("is_favorited")
    val isFavorited: Int,  // Backend returns 1/0 instead of true/false

    @SerializedName("created_at")
    val createdAt: String
) {
    // Helper properties to convert Int to Boolean
    val isActiveBoolean: Boolean get() = isActive == 1
    val isFavoritedBoolean: Boolean get() = isFavorited == 1
}

data class VideoDetailResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: VideoItem
)

data class FavoriteVideoResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: FavoriteData?
)

data class FavoriteData(
    @SerializedName("is_favorited")
    val isFavorited: Boolean
)