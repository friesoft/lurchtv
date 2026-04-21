package org.friesoft.lurchtv.presentation.screens.videoPlayer.components

import android.view.KeyEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import org.friesoft.lurchtv.presentation.utils.handleDPadKeyEvents
import org.friesoft.lurchtv.presentation.utils.ifElse

@Composable
fun RowScope.VideoPlayerControllerIndicator(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    onShowControls: (isSeeking: Boolean) -> Unit = {},
    onSeekingStatusChanged: (isSeeking: Boolean, seekProgress: Float) -> Unit = { _, _ -> },
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSelected by remember { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color by rememberUpdatedState(
        newValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface
    )
    val animatedIndicatorHeight by animateDpAsState(
        targetValue = 4.dp.times((if (isFocused) 2.5f else 1f))
    )
    var seekProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSelected) {
        onShowControls(isSelected)
        onSeekingStatusChanged(isSelected, seekProgress)
    }

    LaunchedEffect(seekProgress) {
        onSeekingStatusChanged(isSelected, seekProgress)
    }

    val handleSeekEventModifier = Modifier.onPreviewKeyEvent { event ->
        val keyCode = event.nativeKeyEvent.keyCode
        val action = event.nativeKeyEvent.action
        val repeatCount = event.nativeKeyEvent.repeatCount

        if (action == KeyEvent.ACTION_DOWN) {
            val increment = when {
                repeatCount > 40 -> 0.05f
                repeatCount > 20 -> 0.02f
                else -> 0.01f
            }
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
                    seekProgress = (seekProgress - increment).coerceAtLeast(0f)
                    onShowControls(true)
                    return@onPreviewKeyEvent true
                }

                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                    seekProgress = (seekProgress + increment).coerceAtMost(1f)
                    onShowControls(true)
                    return@onPreviewKeyEvent true
                }
            }
        } else if (action == KeyEvent.ACTION_UP) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                    isSelected = !isSelected
                    onSeek(seekProgress)
                    return@onPreviewKeyEvent true
                }

                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                    return@onPreviewKeyEvent true
                }
            }
        }
        false
    }

    val handleDpadCenterClickModifier = Modifier.handleDPadKeyEvents(
        onEnter = {
            seekProgress = progress
            isSelected = !isSelected
        }
    )

    Canvas(
        modifier = Modifier
            .weight(1f)
            .height(animatedIndicatorHeight)
            .padding(horizontal = 4.dp)
            .ifElse(
                condition = isSelected,
                ifTrueModifier = handleSeekEventModifier,
                ifFalseModifier = handleDpadCenterClickModifier
            )
            .focusable(interactionSource = interactionSource),
        onDraw = {
            val yOffset = size.height.div(2)
            drawLine(
                color = color.copy(alpha = 0.24f),
                start = Offset(x = 0f, y = yOffset),
                end = Offset(x = size.width, y = yOffset),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(x = 0f, y = yOffset),
                end = Offset(
                    x = size.width.times(if (isSelected) seekProgress else progress),
                    y = yOffset
                ),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        }
    )
}
