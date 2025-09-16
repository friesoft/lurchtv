package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.toMovieCast
import org.friesoft.lurchtv.data.util.AssetsReader
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class MovieCastDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCastDataReader = CachedDataReader {
        readMovieCastData(assetsReader, StringConstants.Assets.MovieCast).map {
            it.toMovieCast()
        }
    }

    suspend fun getMovieCastList() = movieCastDataReader.read()
}
