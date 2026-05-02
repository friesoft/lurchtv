package org.friesoft.lurchtv.presentation.screens.videoPlayer

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
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
import org.friesoft.lurchtv.presentation.utils.isTv

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

    val window = (context as? Activity)?.window
    val insetsController = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
    val isTv = isTv()

    DisposableEffect(Unit) {
        if (!isTv) {
            insetsController?.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        onDispose {
            if (!isTv) {
                insetsController?.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val videoPlayerState = rememberVideoPlayerState(
        hideSeconds = 4,
    )

    LaunchedEffect(exoPlayer, videoDetails) {
        exoPlayer.addMediaItem(videoDetails.intoMediaItem())
        videoDetails.similarVideos.forEach {
            exoPlayer.addMediaItem(it.intoMediaItem())
        }
        val savedPosition = viewModel.getPlaybackPosition()
        if (savedPosition > 0) {
            exoPlayer.seekTo(savedPosition)
        }
        exoPlayer.prepare()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
    var playWhenReady by remember { mutableStateOf(exoPlayer.playWhenReady) }
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayWhenReadyChanged(pwr: Boolean, reason: Int) {
                playWhenReady = pwr
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(playWhenReady) {
        videoPlayerState.showControls(isPlaying = playWhenReady)
    }

    val backgroundFocusRequester = remember { FocusRequester() }
    LaunchedEffect(videoPlayerState.isControlsVisible) {
        if (!videoPlayerState.isControlsVisible) {
            backgroundFocusRequester.requestFocus()
        }
    }

    val pulseState = rememberVideoPlayerPulseState()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(backgroundFocusRequester)
            .dPadEvents(
                exoPlayer,
                videoPlayerState,
                pulseState
            )
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView<PlayerView>(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        )

        val focusRequester = remember { FocusRequester() }
        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            onTap = {
                if (videoPlayerState.isControlsVisible) {
                    videoPlayerState.hideControls()
                } else {
                    videoPlayerState.showControls()
                }
            },
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = { /* TODO Implement subtitles */ },
            showControls = { playing, seeking -> videoPlayerState.showControls(playing, seeking) },
            controls = {
                VideoPlayerControls(
                    player = exoPlayer,
                    videoDetails = videoDetails,
                    focusRequester = focusRequester,
                    onShowControls = { videoPlayerState.showControls(isPlaying, it) },
                )
            }
        )
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.onPreviewKeyEvent { event ->
    if (videoPlayerState.isControlsVisible) return@onPreviewKeyEvent false

    val keyCode = event.nativeKeyEvent.keyCode
    val action = event.nativeKeyEvent.action

    if (action == android.view.KeyEvent.ACTION_DOWN) {
        when (keyCode) {
            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                exoPlayer.seekBack()
                pulseState.setType(BACK)
                return@onPreviewKeyEvent true
            }

            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                exoPlayer.seekForward()
                pulseState.setType(FORWARD)
                return@onPreviewKeyEvent true
            }
        }
    }

    if (action == android.view.KeyEvent.ACTION_UP) {
        when (keyCode) {
            android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                videoPlayerState.showControls()
                return@onPreviewKeyEvent true
            }

            android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                videoPlayerState.showControls()
                return@onPreviewKeyEvent true
            }

            android.view.KeyEvent.KEYCODE_DPAD_CENTER, android.view.KeyEvent.KEYCODE_ENTER -> {
                exoPlayer.pause()
                videoPlayerState.showControls()
                return@onPreviewKeyEvent true
            }
        }
    }
    false
}

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
