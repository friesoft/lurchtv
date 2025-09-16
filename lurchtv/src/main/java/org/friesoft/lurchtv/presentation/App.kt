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
                    goToVideoPlayer = {
                        navController.navigate(Screens.VideoPlayer())
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
                    openVideoPlayer = {
                        navController.navigate(Screens.VideoPlayer())
                    },
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }
                )
            }
            composable(route = Screens.VideoPlayer()) {
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
