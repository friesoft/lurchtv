package org.friesoft.lurchtv.data.entities

import org.friesoft.lurchtv.data.models.VideoResponseItem

data class Video(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val posterUri: String,
    val name: String,
    val description: String,
    val episode: Int = 0,
    val videoLength: Int = 0,
    val views: Int = 0,
    val createdAt: String = "",
    val lastPlaybackPosition: Long = 0
)

fun VideoResponseItem.toVideo(thumbnailType: ThumbnailType = ThumbnailType.Standard): Video {
    return Video(
        id = episode.toString(),
        videoUri = "", // Needs playlist call
        subtitleUri = null,
        posterUri = previewUrl,
        name = title,
        description = "Views: $views",
        episode = episode,
        videoLength = videoLength,
        views = views,
        createdAt = createdAt
    )
}

enum class ThumbnailType {
    Standard,
    Long
}
