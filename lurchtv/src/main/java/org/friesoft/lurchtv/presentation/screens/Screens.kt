package org.friesoft.lurchtv.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import org.friesoft.lurchtv.presentation.screens.videos.VideoDetailsScreen
import org.friesoft.lurchtv.presentation.screens.videoPlayer.VideoPlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    Profile,
    Home(isTabItem = true),
    Favourites(isTabItem = true),
    Search(isTabItem = true, tabIcon = Icons.Default.Search),
    VideoDetails(listOf(VideoDetailsScreen.VideoIdBundleKey)),
    Dashboard,
    VideoPlayer(listOf(VideoPlayerScreen.VideoIdBundleKey));

    operator fun invoke(): String {
        val argList = StringBuilder()
        args?.let { nnArgs ->
            nnArgs.forEach { arg -> argList.append("/{$arg}") }
        }
        return name + argList
    }

    fun withArgs(vararg args: Any): String {
        val destination = StringBuilder()
        args.forEach { arg -> destination.append("/$arg") }
        return name + destination
    }
}
