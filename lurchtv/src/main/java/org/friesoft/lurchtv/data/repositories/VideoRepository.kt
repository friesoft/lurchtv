package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.VideoCategoryDetails
import org.friesoft.lurchtv.data.entities.VideoCategoryList
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.entities.VideoList
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getFeaturedVideos(): Flow<VideoList>
    fun getRecentVideos(): Flow<VideoList>
    fun getTop10Videos(): Flow<VideoList>
    fun getVideoCategories(): Flow<VideoCategoryList>
    suspend fun getVideoCategoryDetails(categoryId: String): VideoCategoryDetails
    suspend fun getVideoDetails(videoId: String): VideoDetails
    suspend fun searchVideos(query: String): VideoList
    suspend fun searchVideosCategorized(query: String): Map<String, VideoList>
    fun getVideosWithLongThumbnail(): Flow<VideoList>
    fun getVideos(): Flow<VideoList>
    fun getPopularVideosThisWeek(): Flow<VideoList>
    fun getFavouriteVideos(): Flow<VideoList>
    suspend fun savePlaybackPosition(videoId: String, position: Long)
    suspend fun getPlaybackPosition(videoId: String): Long
    fun getAllPlaybackPositions(): Flow<Map<String, Long>>
}
