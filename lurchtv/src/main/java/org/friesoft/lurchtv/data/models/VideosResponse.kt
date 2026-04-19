package org.friesoft.lurchtv.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseItem(
    val id: Int,
    val title: String,
    @SerialName("preview_url")
    val previewUrl: String = "",
    @SerialName("video_length")
    val videoLength: Int = 0,
    val views: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    val episode: Int = 0
)

@Serializable
data class ApiResponse(
    val results: ApiResults
)

@Serializable
data class ApiResults(
    val videos: List<VideoResponseItem> = emptyList(),
    val games: List<GameResult> = emptyList()
)

@Serializable
data class GameResult(
    val id: Int,
    val title: String,
    val videos: List<VideoResponseItem> = emptyList()
)

@Serializable
data class DiscoveryResponse(
    val discovery: List<VideoResponseItem> = emptyList()
)

@Serializable
data class VideoInfoResponse(
    val id: Int,
    val title: String,
    @SerialName("created_at")
    val createdAt: String,
    val episode: Int,
    @SerialName("source_length")
    val sourceLength: Int,
    val views: Int,
    @SerialName("preview_url")
    val previewUrl: String,
    @SerialName("vtt_url")
    val vttUrl: String? = null,
    val chapters: List<Chapter> = emptyList(),
    val next: VideoResponseItem? = null,
    val previous: VideoResponseItem? = null
)

@Serializable
data class Chapter(
    val id: Int,
    val title: String,
    val offset: Int,
    val game: Game? = null
)

@Serializable
data class Game(
    val id: Int,
    val title: String
)

@Serializable
data class PlaylistResponse(
    @SerialName("playlist_url")
    val playlistUrl: String
)
