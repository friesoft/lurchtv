package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.toMovieCategory
import org.friesoft.lurchtv.data.util.AssetsReader
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class MovieCategoryDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCategoryDataReader = CachedDataReader {
        readMovieCategoryData(assetsReader, StringConstants.Assets.MovieCategories).map {
            it.toMovieCategory()
        }
    }

    suspend fun getMovieCategoryList() = movieCategoryDataReader.read()
}
