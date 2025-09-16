package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.VideoCategoryDetails
import org.friesoft.lurchtv.data.entities.VideoCategoryList
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.entities.VideoList
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getFeaturedVideos(): Flow<VideoList>
    fun getTrendingVideos(): Flow<VideoList>
    fun getTop10Videos(): Flow<VideoList>
    fun getNowPlayingVideos(): Flow<VideoList>
    fun getVideoCategories(): Flow<VideoCategoryList>
    suspend fun getVideoCategoryDetails(categoryId: String): VideoCategoryDetails
    suspend fun getVideoDetails(videoId: String): VideoDetails
    suspend fun searchVideos(query: String): VideoList
    fun getVideosWithLongThumbnail(): Flow<VideoList>
    fun getVideos(): Flow<VideoList>
    fun getPopularVideosThisWeek(): Flow<VideoList>
    fun getFavouriteVideos(): Flow<VideoList>
}
