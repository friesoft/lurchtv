
package org.friesoft.lurchtv.presentation.screens.videoPlayer.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.PlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import org.friesoft.lurchtv.data.util.StringConstants
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerSeeker(
    player: Player,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    state: PlayPauseButtonState = rememberPlayPauseButtonState(player),
    onSeek: (Float) -> Unit = {
        player.seekTo(player.duration.times(it).toLong())
    },
    onShowControls: (isSeeking: Boolean) -> Unit = {},
) {
    val contentDurationMs = player.contentDuration
    val isDurationValid = contentDurationMs != C.TIME_UNSET
    val contentDuration = if (isDurationValid) contentDurationMs.milliseconds else kotlin.time.Duration.INFINITE

    var currentPositionMs by remember(player) { mutableLongStateOf(0L) }
    val currentPosition = currentPositionMs.milliseconds

    var isSeeking by remember { mutableStateOf(false) }
    var seekProgress by remember { mutableFloatStateOf(0f) }

    // TODO: Update in a more thoughtful manner
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            currentPositionMs = player.currentPosition
        }
    }

    fun formatTime(duration: kotlin.time.Duration): String {
        if (duration.isInfinite()) return "--:--"
        return duration.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }
    }

    val contentProgressString = formatTime(currentPosition)
    val contentDurationString = formatTime(contentDuration)
    val seekProgressString = if (isDurationValid && !contentDuration.isInfinite()) {
        formatTime(contentDuration.times(seekProgress.toDouble()))
    } else {
        "--:--"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoPlayerControlsIcon(
            modifier = Modifier.focusRequester(focusRequester),
            icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause,
            onClick = state::onClick,
            isPlaying = player.isPlaying,
            contentDescription = StringConstants
                .Composable
                .VideoPlayerControlPlayPauseButton
        )
        VideoPlayerControllerText(text = if (isSeeking) seekProgressString else contentProgressString)
        VideoPlayerControllerIndicator(
            progress = if (isDurationValid && contentDuration.inWholeMilliseconds > 0) {
                (currentPosition / contentDuration).toFloat()
            } else {
                0f
            },
            onSeek = onSeek,
            onShowControls = onShowControls,
            onSeekingStatusChanged = { seeking, progress ->
                isSeeking = seeking
                seekProgress = progress
            }
        )
        VideoPlayerControllerText(text = contentDurationString)
    }
}

private fun Number.padStartWith0() = this.toString().padStart(2, '0')
