package org.friesoft.lurchtv.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.friesoft.lurchtv.presentation.screens.Screens
import org.friesoft.lurchtv.presentation.screens.dashboard.DashboardScreen
import org.friesoft.lurchtv.presentation.screens.videos.VideoDetailsScreen
import org.friesoft.lurchtv.presentation.screens.videoPlayer.VideoPlayerScreen

@Composable
fun App(
    onBackPressed: () -> Unit
) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    var lastWatchedVideoId by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screens.Dashboard(),
        builder = {
            composable(
                route = Screens.VideoDetails(),
                arguments = listOf(
                    navArgument(VideoDetailsScreen.VideoIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                VideoDetailsScreen(
                    goToVideoPlayer = { video ->
                        lastWatchedVideoId = video.id
                        navController.navigate(Screens.VideoPlayer.withArgs(video.id))
                    },
                    refreshScreenWithNewVideo = { video ->
                        navController.navigate(
                            Screens.VideoDetails.withArgs(video.id)
                        ) {
                            popUpTo(Screens.VideoDetails()) {
                                inclusive = true
                            }
                        }
                    },
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
            composable(route = Screens.Dashboard()) {
                DashboardScreen(
                    openVideoDetailsScreen = { videoId ->
                        navController.navigate(
                            Screens.VideoDetails.withArgs(videoId)
                        )
                    },
                    openVideoPlayer = { video ->
                        lastWatchedVideoId = video.id
                        navController.navigate(Screens.VideoPlayer.withArgs(video.id))
                    },
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    },
                    lastWatchedVideoId = lastWatchedVideoId
                )
            }
            composable(
                route = Screens.VideoPlayer(),
                arguments = listOf(
                    navArgument(VideoPlayerScreen.VideoIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                VideoPlayerScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
        }
    )
}
