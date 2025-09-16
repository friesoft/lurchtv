package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.ThumbnailType
import org.friesoft.lurchtv.data.entities.toMovie
import org.friesoft.lurchtv.data.util.AssetsReader
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class TvDataSource @Inject constructor(
    assetsReader: AssetsReader
) {
    private val mostPopularTvShowsReader = CachedDataReader {
        readMovieData(assetsReader, StringConstants.Assets.MostPopularTVShows)
    }

    suspend fun getTvShowList() = mostPopularTvShowsReader.read().subList(0, 5).map {
        it.toMovie(ThumbnailType.Long)
    }

    suspend fun getBingeWatchDramaList() = mostPopularTvShowsReader.read().subList(6, 15).map {
        it.toMovie()
    }
}
