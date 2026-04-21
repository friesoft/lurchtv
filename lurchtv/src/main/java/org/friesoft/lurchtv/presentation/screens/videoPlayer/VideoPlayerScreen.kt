package org.friesoft.lurchtv.presentation.screens.videoPlayer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.presentation.common.Error
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerControls
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerPulse
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerPulse.Type.BACK
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerPulse.Type.FORWARD
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.VideoPlayerState
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.rememberPlayer
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import org.friesoft.lurchtv.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import org.friesoft.lurchtv.presentation.utils.handleDPadKeyEvents

object VideoPlayerScreen {
    const val VideoIdBundleKey = "videoId"
}

/**
 * [Work in progress] A composable screen for playing a video.
 *
 * @param onBackPressed The callback to invoke when the user presses the back button.
 * @param videoPlayerScreenViewModel The view model for the video player screen.
 */
@Composable
fun VideoPlayerScreen(
    onBackPressed: () -> Unit,
    videoPlayerScreenViewModel: VideoPlayerScreenViewModel = hiltViewModel()
) {
    val uiState by videoPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()

    // TODO: Handle Loading & Error states
    when (val s = uiState) {
        is VideoPlayerScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is VideoPlayerScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is VideoPlayerScreenUiState.Done -> {
            VideoPlayerScreenContent(
                videoDetails = s.videoDetails,
                onBackPressed = onBackPressed,
                viewModel = videoPlayerScreenViewModel
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreenContent(
    videoDetails: VideoDetails,
    onBackPressed: () -> Unit,
    viewModel: VideoPlayerScreenViewModel
) {
    val context = LocalContext.current
    val exoPlayer = rememberPlayer(context)

    val videoPlayerState = rememberVideoPlayerState(
        hideSeconds = 4,
    )

    LaunchedEffect(exoPlayer, videoDetails) {
        exoPlayer.addMediaItem(videoDetails.intoMediaItem())
        videoDetails.similarVideos.forEach {
            exoPlayer.addMediaItem(it.intoMediaItem())
        }
        val savedPosition = viewModel.getPlaybackPosition()
        exoPlayer.seekTo(savedPosition)
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePlaybackPosition(exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    BackHandler(onBack = {
        if (videoPlayerState.isControlsVisible) {
            videoPlayerState.hideControls()
        } else {
            viewModel.savePlaybackPosition(exoPlayer.currentPosition)
            onBackPressed()
        }
    })

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    val pulseState = rememberVideoPlayerPulseState()

    Box(
        Modifier
            .dPadEvents(
                exoPlayer,
                videoPlayerState,
                pulseState
            )
            .focusable()
    ) {
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.resizeWithContentScale(
                contentScale = ContentScale.Fit,
                sourceSizeDp = null
            )
        )

        val focusRequester = remember { FocusRequester() }
        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = { /* TODO Implement subtitles */ },
            showControls = videoPlayerState::showControls,
            controls = {
                VideoPlayerControls(
                    player = exoPlayer,
                    videoDetails = videoDetails,
                    focusRequester = focusRequester,
                    onShowControls = { videoPlayerState.showControls(isPlaying) },
                )
            }
        )
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.isControlsVisible) {
            exoPlayer.seekBack()
            pulseState.setType(BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.isControlsVisible) {
            exoPlayer.seekForward()
            pulseState.setType(FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        exoPlayer.pause()
        videoPlayerState.showControls()
    }
)

private fun VideoDetails.intoMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(videoUri)
        .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
        .build()
}

private fun Video.intoMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(videoUri)
        .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
        .build()
}
