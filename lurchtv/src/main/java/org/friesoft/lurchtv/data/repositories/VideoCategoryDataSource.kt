package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.toVideoCategory
import org.friesoft.lurchtv.data.util.AssetsReader
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class VideoCategoryDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val videoCategoryDataReader = CachedDataReader {
        readVideoCategoryData(assetsReader, StringConstants.Assets.VideoCategories).map {
            it.toVideoCategory()
        }
    }

    suspend fun getVideoCategoryList() = videoCategoryDataReader.read()
}
