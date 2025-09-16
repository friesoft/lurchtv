package org.friesoft.lurchtv.data.entities

import org.friesoft.lurchtv.data.models.VideoResponseItem

data class Video(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val posterUri: String,
    val name: String,
    val description: String
)

fun VideoResponseItem.toVideo(thumbnailType: ThumbnailType = ThumbnailType.Standard): Video {
    val thumbnail = when (thumbnailType) {
        ThumbnailType.Standard -> image_2_3
        ThumbnailType.Long -> image_16_9
    }
    return Video(
        id,
        videoUri,
        subtitleUri,
        thumbnail,
        title,
        fullTitle
    )
}

enum class ThumbnailType {
    Standard,
    Long
}
