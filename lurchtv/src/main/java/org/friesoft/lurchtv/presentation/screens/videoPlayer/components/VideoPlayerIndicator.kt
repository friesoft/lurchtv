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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import org.friesoft.lurchtv.presentation.utils.handleDPadKeyEvents
import org.friesoft.lurchtv.presentation.utils.ifElse
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun RowScope.VideoPlayerControllerIndicator(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    onShowControls: (isSeeking: Boolean) -> Unit = {},
    onSeekingStatusChanged: (isSeeking: Boolean, seekProgress: Float) -> Unit = { _, _ -> },
) {
    val isTv = isTv()
    if (isTv) {
        VideoPlayerControllerIndicatorTv(
            progress = progress,
            onSeek = onSeek,
            onShowControls = onShowControls,
            onSeekingStatusChanged = onSeekingStatusChanged
        )
    } else {
        VideoPlayerControllerIndicatorMobile(
            progress = progress,
            onSeek = onSeek,
            onShowControls = onShowControls,
            onSeekingStatusChanged = onSeekingStatusChanged
        )
    }
}

@Composable
private fun RowScope.VideoPlayerControllerIndicatorTv(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    onShowControls: (isSeeking: Boolean) -> Unit = {},
    onSeekingStatusChanged: (isSeeking: Boolean, seekProgress: Float) -> Unit = { _, _ -> },
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSelected by remember { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color by rememberUpdatedState(
        newValue = if (isSelected) TvMaterialTheme.colorScheme.primary
        else TvMaterialTheme.colorScheme.onSurface
    )
    val animatedIndicatorHeight by animateDpAsState(
        targetValue = 4.dp.times((if (isFocused) 2.5f else 1f)),
        label = ""
    )
    var seekProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSelected, isFocused) {
        if (isSelected || isFocused) {
            onShowControls(isSelected)
        }
        onSeekingStatusChanged(isSelected, seekProgress)
    }

    LaunchedEffect(seekProgress) {
        onSeekingStatusChanged(isSelected, seekProgress)
    }

    val handleSeekEventModifier = Modifier.onPreviewKeyEvent { event ->
        val keyCode = event.nativeKeyEvent.keyCode
        val action = event.nativeKeyEvent.action
        val repeatCount = event.nativeKeyEvent.repeatCount

        if (action == android.view.KeyEvent.ACTION_DOWN) {
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
        } else if (action == android.view.KeyEvent.ACTION_UP) {
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

@Composable
private fun RowScope.VideoPlayerControllerIndicatorMobile(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    onShowControls: (isSeeking: Boolean) -> Unit = {},
    onSeekingStatusChanged: (isSeeking: Boolean, seekProgress: Float) -> Unit = { _, _ -> },
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    Slider(
        value = if (isDragging) dragProgress else progress,
        onValueChange = {
            isDragging = true
            dragProgress = it
            onShowControls(true)
            onSeekingStatusChanged(true, it)
        },
        onValueChangeFinished = {
            isDragging = false
            onSeek(dragProgress)
            onSeekingStatusChanged(false, dragProgress)
        },
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp),
        colors = SliderDefaults.colors(
            thumbColor = MobileMaterialTheme.colorScheme.primary,
            activeTrackColor = MobileMaterialTheme.colorScheme.primary,
            inactiveTrackColor = Color.White.copy(alpha = 0.24f)
        )
    )
}

