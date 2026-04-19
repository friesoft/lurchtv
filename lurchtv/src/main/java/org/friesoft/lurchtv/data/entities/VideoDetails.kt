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
    val videoLength: Int = 0,
    val similarVideos: VideoList,
    val lastPlaybackPosition: Long = 0
)

fun VideoDetails.toVideo(): Video = Video(
    id = id,
    videoUri = videoUri,
    subtitleUri = null,
    posterUri = posterUri,
    name = name,
    description = description,
    videoLength = videoLength,
    lastPlaybackPosition = lastPlaybackPosition
)
