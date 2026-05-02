package org.friesoft.lurchtv.data.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.friesoft.lurchtv.data.entities.ThumbnailType
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.toVideo
import org.friesoft.lurchtv.data.models.VideoResponseItem
import org.friesoft.lurchtv.data.services.GronkhApiService
import javax.inject.Inject

const val PAGE_SIZE = 24

class VideoDataSource @Inject constructor(
    private val apiService: GronkhApiService
)  : PagingSource<Int, VideoResponseItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoResponseItem> {
        val page = params.key ?: 0
        val offset = page * PAGE_SIZE
        return try {
            val response = apiService.getVideos(count = PAGE_SIZE, offset = offset)
            val videos = response.results.videos
            LoadResult.Page(
                data = videos,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (videos.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, VideoResponseItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // Cache responses locally for this session to simulate the previous CachedDataReader behavior
    // and avoid hitting the API too many times for static rows.
    private var cachedVideos: List<VideoResponseItem>? = null
    private var cachedTop10Videos: List<VideoResponseItem>? = null

    private suspend fun getBaseVideos(): List<VideoResponseItem> {
        return cachedVideos ?: try {
            val response = apiService.getVideos(count = 24)
            response.results.videos.also { cachedVideos = it }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getTopDiscoveryVideos(): List<VideoResponseItem> {
        return cachedTop10Videos ?: try {
            val response = apiService.getTop10Videos()
            response.discovery.also { cachedTop10Videos = it }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVideoList(thumbnailType: ThumbnailType = ThumbnailType.Standard): List<Video> {
        return getBaseVideos().map { it.toVideo(thumbnailType) }
    }

    suspend fun getFeaturedVideoList(): List<Video> {
        // Return only the first video as featured
        return getBaseVideos().take(1).map { it.toVideo(ThumbnailType.Long) }
    }

    suspend fun getRecentVideoList(): List<Video> {
        return getBaseVideos().take(12).map { it.toVideo(ThumbnailType.Long) }
    }


    suspend fun getTop10VideoList(): List<Video> {
        return getTopDiscoveryVideos().take(10).map { it.toVideo(ThumbnailType.Long) }
    }

    suspend fun getPopularVideoThisWeek(): List<Video> {
        return getBaseVideos().take(10).map { it.toVideo() }
    }

    suspend fun getFavouriteVideoList(): List<Video> {
        return getBaseVideos().take(24).map { it.toVideo() }
    }
}

