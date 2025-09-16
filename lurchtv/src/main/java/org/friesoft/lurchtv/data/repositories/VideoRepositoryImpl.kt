package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.VideoCategoryDetails
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.entities.VideoReviewsAndRatings
import org.friesoft.lurchtv.data.entities.ThumbnailType
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.data.util.StringConstants.Video.Reviewer.DefaultCount
import org.friesoft.lurchtv.data.util.StringConstants.Video.Reviewer.DefaultRating
import org.friesoft.lurchtv.data.util.StringConstants.Video.Reviewer.FreshTomatoes
import org.friesoft.lurchtv.data.util.StringConstants.Video.Reviewer.ReviewerName
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoDataSource: VideoDataSource,
    private val videoCategoryDataSource: VideoCategoryDataSource,
) : VideoRepository {

    override fun getFeaturedVideos() = flow {
        val list = videoDataSource.getFeaturedVideoList()
        emit(list)
    }

    override fun getTrendingVideos(): Flow<VideoList> = flow {
        val list = videoDataSource.getTrendingVideoList()
        emit(list)
    }

    override fun getTop10Videos(): Flow<VideoList> = flow {
        val list = videoDataSource.getTop10VideoList()
        emit(list)
    }

    override fun getNowPlayingVideos(): Flow<VideoList> = flow {
        val list = videoDataSource.getNowPlayingVideoList()
        emit(list)
    }

    override fun getVideoCategories() = flow {
        val list = videoCategoryDataSource.getVideoCategoryList()
        emit(list)
    }

    override suspend fun getVideoCategoryDetails(categoryId: String): VideoCategoryDetails {
        val categoryList = videoCategoryDataSource.getVideoCategoryList()
        val category = categoryList.find { categoryId == it.id } ?: categoryList.first()

        val videoList = videoDataSource.getVideoList().shuffled().subList(0, 20)

        return VideoCategoryDetails(
            id = category.id,
            name = category.name,
            videos = videoList
        )
    }

    override suspend fun getVideoDetails(videoId: String): VideoDetails {
        val videoList = videoDataSource.getVideoList()
        val video = videoList.find { it.id == videoId } ?: videoList.first()
        val similarVideoList = videoList.subList(1, 4)

        return VideoDetails(
            id = video.id,
            videoUri = video.videoUri,
            subtitleUri = video.subtitleUri,
            posterUri = video.posterUri,
            name = video.name,
            description = video.description,
            pgRating = "PG-13",
            releaseDate = "2021 (US)",
            categories = listOf("Action", "Adventure", "Fantasy", "Comedy"),
            duration = "1h 59m",
            director = "Larry Page",
            screenplay = "Sundai Pichai",
            music = "Sergey Brin",
            status = "Released",
            originalLanguage = "English",
            budget = "$15M",
            revenue = "$40M",
            similarVideos = similarVideoList,
            reviewsAndRatings = listOf(
                VideoReviewsAndRatings(
                    reviewerName = FreshTomatoes,
                    reviewerIconUri = StringConstants.Video.Reviewer.FreshTomatoesImageUrl,
                    reviewCount = "22",
                    reviewRating = "89%"
                ),
                VideoReviewsAndRatings(
                    reviewerName = ReviewerName,
                    reviewerIconUri = StringConstants.Video.Reviewer.ImageUrl,
                    reviewCount = DefaultCount,
                    reviewRating = DefaultRating
                ),
            ),
        )
    }

    override suspend fun searchVideos(query: String): VideoList {
        return videoDataSource.getVideoList().filter {
            it.name.contains(other = query, ignoreCase = true)
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
        val list = videoDataSource.getFavoriteVideoList()
        emit(list)
    }
}
