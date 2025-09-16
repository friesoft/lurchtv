package org.friesoft.lurchtv.data.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoCategoriesResponseItem(
    val id: String,
    val name: String,
)
