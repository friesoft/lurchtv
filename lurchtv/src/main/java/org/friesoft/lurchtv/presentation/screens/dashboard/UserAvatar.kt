package org.friesoft.lurchtv.presentation.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface
import org.friesoft.lurchtv.presentation.theme.LurchTVBorderWidth

@Composable
fun UserAvatar(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = SelectableSurfaceDefaults.shape(shape = CircleShape),
        border = SelectableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = LurchTVBorderWidth,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                shape = CircleShape
            ),
            selectedBorder = Border(
                border = BorderStroke(
                    width = LurchTVBorderWidth,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ),
        ),
        scale = SelectableSurfaceDefaults.scale(focusedScale = 1f),
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
