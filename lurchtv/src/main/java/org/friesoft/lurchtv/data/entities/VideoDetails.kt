package org.friesoft.lurchtv.data.entities

data class VideoDetails(
    val id: String,
    val videoUri: String,
    val posterUri: String,
    val name: String,
    val description: String,
    val releaseDate: String,
    val categories: List<String>,
    val duration: String,
    val similarVideos: VideoList,
)

fun VideoDetails.toVideo(): Video = Video(
    id = id,
    videoUri = videoUri,
    subtitleUri = null,
    posterUri = posterUri,
    name = name,
    description = description
)
