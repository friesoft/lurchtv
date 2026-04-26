package org.friesoft.lurchtv.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.entities.ThumbnailType
import org.friesoft.lurchtv.data.entities.toVideo
import org.friesoft.lurchtv.data.services.GronkhApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoDataSource: VideoDataSource,
    private val apiService: GronkhApiService,
    private val dataStore: DataStore<Preferences>
) : VideoRepository {

    override fun getFeaturedVideos() = flow {
        val list = videoDataSource.getFeaturedVideoList()
        emit(list)
    }

    override fun getRecentVideos(): Flow<VideoList> = flow {
        val list = videoDataSource.getRecentVideoList()
        emit(list)
    }

    override fun getTop10Videos(): Flow<VideoList> = flow {
        val list = videoDataSource.getTop10VideoList()
        emit(list)
    }

    override suspend fun getVideoDetails(videoId: String): VideoDetails {
        val episodeId = videoId.toIntOrNull() ?: return fallbackVideoDetails(videoId)
        
        return try {
            val info = apiService.getVideoInfo(episodeId)
            val playlist = apiService.getPlaylist(episodeId)
            
            VideoDetails(
                id = info.episode.toString(),
                videoUri = playlist.playlistUrl,
                posterUri = info.previewUrl,
                name = info.title,
                description = "Aufgerufen: ${info.views} mal.\nVeröffentlicht am: ${info.createdAt}",
                releaseDate = info.createdAt.take(10), // e.g., 2024-11-20
                categories = info.chapters.mapNotNull { it.game?.title }.distinct(),
                duration = "${info.sourceLength / 60}m",
                videoLength = info.sourceLength,
                similarVideos = videoDataSource.getVideoList().take(3),
            )
        } catch (e: Exception) {
            fallbackVideoDetails(videoId)
        }
    }

    private suspend fun fallbackVideoDetails(videoId: String): VideoDetails {
        val videoList = videoDataSource.getVideoList()
        val video = videoList.find { it.id == videoId } ?: videoList.first()
        val similarVideoList = videoList.take(3)
        
        return VideoDetails(
            id = video.id,
            videoUri = video.videoUri,
            posterUri = video.posterUri,
            name = video.name,
            description = video.description,
            releaseDate = "2024 (DE)",
            categories = listOf("Gaming"),
            duration = "Unknown",
            similarVideos = similarVideoList,
        )
    }

    override suspend fun searchVideos(query: String): VideoList {
        return try {
            val response = apiService.getVideos(count = 24, query = query)
            response.results.videos.map { it.toVideo() }
        } catch (e: Exception) {
            videoDataSource.getVideoList().filter {
                it.name.contains(other = query, ignoreCase = true)
            }
        }
    }

    override suspend fun searchVideosCategorized(query: String): Map<String, VideoList> {
        return try {
            val response = apiService.getVideos(count = 24, query = query)
            val categories = mutableMapOf<String, VideoList>()
            
            // Add game categories
            response.results.games.forEach { game ->
                if (game.videos.isNotEmpty()) {
                    categories[game.title] = game.videos.map { it.toVideo(ThumbnailType.Long) }
                }
            }
            
            // Add VODs
            if (response.results.videos.isNotEmpty()) {
                categories["VODs"] = response.results.videos.map { it.toVideo(ThumbnailType.Long) }
            }
            
            categories
        } catch (e: Exception) {
            mapOf("Results" to searchVideos(query))
        }
    }

    override fun getVideosWithLongThumbnail() = flow {
        val list = videoDataSource.getVideoList(ThumbnailType.Long)
        emit(list)
    }

    override fun getVideos(): Flow<VideoList> = flow {
        val list = videoDataSource.getVideoList()
        emit(list)
    }

    override fun getPopularVideosThisWeek(): Flow<VideoList> = flow {
        val list = videoDataSource.getPopularVideoThisWeek()
        emit(list)
    }

    override fun getFavouriteVideos(): Flow<VideoList> = flow {
        val list = videoDataSource.getFavouriteVideoList()
        emit(list)
    }

    override suspend fun savePlaybackPosition(videoId: String, position: Long) {
        dataStore.edit { prefs ->
            prefs[longPreferencesKey("pos_$videoId")] = position
        }
    }

    override suspend fun getPlaybackPosition(videoId: String): Long {
        return dataStore.data.map { it[longPreferencesKey("pos_$videoId")] ?: 0L }.first()
    }

    override fun getAllPlaybackPositions(): Flow<Map<String, Long>> {
        return dataStore.data.map { prefs ->
            prefs.asMap().entries
                .filter { it.key.name.startsWith("pos_") }
                .associate { it.key.name.removePrefix("pos_") to (it.value as Long) }
        }
    }
}
