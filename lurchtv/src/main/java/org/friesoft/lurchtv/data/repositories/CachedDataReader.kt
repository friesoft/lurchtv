package org.friesoft.lurchtv.data.repositories

import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.models.VideoCategoriesResponseItem
import org.friesoft.lurchtv.data.models.VideoResponseItem
import org.friesoft.lurchtv.data.util.AssetsReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class CachedDataReader<T>(private val reader: suspend () -> List<T>) {
    private val mutex = Mutex()
    private lateinit var cache: List<T>

    suspend fun read(): List<T> {
        mutex.withLock {
            if (!::cache.isInitialized) {
                cache = reader()
            }
        }
        return cache
    }
}

internal typealias VideoDataReader = CachedDataReader<Video>

internal suspend fun readVideoData(
    assetsReader: AssetsReader,
    resourceId: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): List<VideoResponseItem> = withContext(dispatcher) {
    assetsReader.getJsonDataFromAsset(resourceId).map {
        Json.decodeFromString<List<VideoResponseItem>>(it)
    }.getOrDefault(emptyList())
}

internal suspend fun readVideoCategoryData(
    assetsReader: AssetsReader,
    resourceId: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): List<VideoCategoriesResponseItem> = withContext(dispatcher) {
    assetsReader.getJsonDataFromAsset(resourceId).map {
        Json.decodeFromString<List<VideoCategoriesResponseItem>>(it)
    }.getOrDefault(emptyList())
}
