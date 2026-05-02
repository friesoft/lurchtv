package org.friesoft.lurchtv.presentation.screens.videoPlayer.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton as MobileIconButton
import androidx.compose.material3.Icon as MobileIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon as TvIcon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Surface as TvSurface
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun VideoPlayerControlsIcon(
    isPlaying: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onShowControls: (isSeeking: Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val isTv = isTv()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused && isTv) {
            onShowControls(false)
        }
    }

    if (isTv) {
        TvSurface(
            modifier = modifier.size(40.dp),
            onClick = onClick,
            shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = TvMaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
            interactionSource = interactionSource
        ) {
            TvIcon(
                icon,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentDescription = contentDescription,
                tint = LocalContentColor.current
            )
        }
    } else {
        MobileIconButton(
            onClick = onClick,
            modifier = modifier.size(48.dp) // Larger touch target
        ) {
            MobileIcon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

