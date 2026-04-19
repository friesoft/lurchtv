package org.friesoft.lurchtv.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import org.friesoft.lurchtv.data.entities.VideoCategory
import org.friesoft.lurchtv.data.entities.toVideoCategory
import org.friesoft.lurchtv.data.models.VideoCategoriesResponseItem
import org.friesoft.lurchtv.data.util.StringConstants
import javax.inject.Inject

class VideoCategoryDataSource @Inject constructor(
    private val json: Json,
    @ApplicationContext private val context: Context
) {
    private fun readCategoryData(assetName: String): List<VideoCategoriesResponseItem> {
        val jsonString = context.assets.open(assetName).bufferedReader().use { it.readText() }
        return json.decodeFromString(jsonString)
    }

    suspend fun getVideoCategoryList(): List<VideoCategory> {
        return readCategoryData(StringConstants.Assets.VideoCategories).map {
            it.toVideoCategory()
        }
    }
}
