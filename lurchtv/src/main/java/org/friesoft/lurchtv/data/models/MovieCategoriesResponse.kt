package org.friesoft.lurchtv.data.models

import kotlinx.serialization.Serializable

@Serializable
data class MovieCategoriesResponseItem(
    val id: String,
    val name: String,
)
