package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.ThumbnailType
import org.friesoft.lurchtv.data.entities.toVideo
import org.friesoft.lurchtv.data.util.AssetsReader
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class VideoDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val top250VideoDataReader = CachedDataReader {
        readVideoData(assetsReader, StringConstants.Assets.Top250Videos)
    }

    private val mostPopularVideoDataReader = VideoDataReader {
        readVideoData(assetsReader, StringConstants.Assets.MostPopularVideos).map {
            it.toVideo()
        }
    }

    private val videoDataReader = VideoDataReader {
        top250VideoDataReader.read().map {
            it.toVideo()
        }
    }

    private var videoWithLongThumbnailDataReader: VideoDataReader = CachedDataReader {
        top250VideoDataReader.read().map {
            it.toVideo(ThumbnailType.Long)
        }
    }

    private val nowPlayingVideoDataReader: VideoDataReader = VideoDataReader {
        readVideoData(assetsReader, StringConstants.Assets.InTheaters).subList(0, 10).map {
            it.toVideo()
        }
    }

    suspend fun getVideoList(thumbnailType: ThumbnailType = ThumbnailType.Standard) =
        when (thumbnailType) {
            ThumbnailType.Standard -> videoDataReader.read()
            ThumbnailType.Long -> videoWithLongThumbnailDataReader.read()
        }

    suspend fun getFeaturedVideoList() =
        videoWithLongThumbnailDataReader.read().filterIndexed { index, _ ->
            listOf(1, 3, 5, 7, 9).contains(index)
        }

    suspend fun getTrendingVideoList() =
        mostPopularVideoDataReader.read().subList(0, 10)

    suspend fun getTop10VideoList() =
        videoWithLongThumbnailDataReader.read().subList(20, 30)

    suspend fun getNowPlayingVideoList() =
        nowPlayingVideoDataReader.read()

    suspend fun getPopularVideoThisWeek() =
        mostPopularVideoDataReader.read().subList(11, 20)

    suspend fun getFavoriteVideoList() =
        videoDataReader.read().subList(0, 28)
}
