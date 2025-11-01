package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class VideoListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<VideoItem>
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
    val isActive: Boolean,

    @SerializedName("is_favorited")
    val isFavorited: Boolean,

    @SerializedName("created_at")
    val createdAt: String
)

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