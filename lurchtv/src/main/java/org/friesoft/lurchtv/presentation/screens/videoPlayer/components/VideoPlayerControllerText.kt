package org.friesoft.lurchtv.presentation.screens.videoPlayer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun VideoPlayerControllerText(text: String) {
    if (isTv()) {
        TvText(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = text,
            color = TvMaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    } else {
        MobileText(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MobileMaterialTheme.typography.bodyMedium
        )
    }
}

