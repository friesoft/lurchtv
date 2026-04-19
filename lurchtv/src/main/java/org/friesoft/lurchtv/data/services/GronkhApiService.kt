package org.friesoft.lurchtv.data.services

import org.friesoft.lurchtv.data.models.ApiResponse
import org.friesoft.lurchtv.data.models.DiscoveryResponse
import org.friesoft.lurchtv.data.models.VideoInfoResponse
import org.friesoft.lurchtv.data.models.PlaylistResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GronkhApiService {
    @GET("v1/search")
    suspend fun getVideos(
        @Query("first") count: Int,
        @Query("after") offset: Int? = null,
        @Query("query") query: String? = null,
        @Query("sort") sort: String = "date",
        @Query("direction") direction: String = "desc"
    ): ApiResponse

    @GET("v1/video/discovery/views")
    suspend fun getTop10Videos(): DiscoveryResponse

    @GET("v1/video/info")
    suspend fun getVideoInfo(
        @Query("episode") episode: Int
    ): VideoInfoResponse

    @GET("v1/video/playlist")
    suspend fun getPlaylist(
        @Query("episode") episode: Int
    ): PlaylistResponse
}
