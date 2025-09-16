package org.friesoft.lurchtv.data.entities

import org.friesoft.lurchtv.data.models.VideoCategoriesResponseItem

data class VideoCategory(
    val id: String,
    val name: String,
)

fun VideoCategoriesResponseItem.toVideoCategory(): VideoCategory =
    VideoCategory(id, name)
