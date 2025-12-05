package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

// Response for creating collection
data class VideoCollectionResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: VideoCollectionItem?
)

// Response for getting list of collections
data class VideoCollectionListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<VideoCollectionItem>
)

// Collection item model
data class VideoCollectionItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("id_user")
    val idUser: String,

    @SerializedName("nama_koleksi")
    val namaKoleksi: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @SerializedName("jumlah_video")
    val jumlahVideo: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("latest_video_thumbnail")
    val latestVideoThumbnail: String?
)

// Response for collection detail with videos
data class VideoCollectionDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: VideoCollectionDetailData
)

data class VideoCollectionDetailData(
    @SerializedName("collection")
    val collection: VideoCollectionItem,

    @SerializedName("videos")
    val videos: List<VideoItem>
)

// Response for getting collections where video can be added
data class VideoCollectionsForVideoResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<VideoCollectionWithFlag>
)

data class VideoCollectionWithFlag(
    @SerializedName("id")
    val id: String,

    @SerializedName("id_user")
    val idUser: String,

    @SerializedName("nama_koleksi")
    val namaKoleksi: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @SerializedName("jumlah_video")
    val jumlahVideo: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("video_in_collection")
    val videoInCollection: Int  // 1 = already in collection, 0 = not yet
) {
    val isVideoInCollection: Boolean get() = videoInCollection == 1
}

// Response for add/remove video to/from collection
data class CollectionVideoResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)
