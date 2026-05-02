package org.friesoft.lurchtv.presentation.screens.videos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun DotSeparatedRow(
    modifier: Modifier = Modifier,
    texts: List<String>
) {
    val isTv = isTv()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        texts.forEachIndexed { index, text ->
            if (isTv) {
                TvText(
                    text = text,
                    style = TvMaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
            } else {
                MobileText(
                    text = text,
                    style = MobileMaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = MobileMaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index != texts.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isTv) TvMaterialTheme.colorScheme.onSurface
                            else MobileMaterialTheme.colorScheme.onSurfaceVariant
                        )
                        .size(4.dp)
                )
            }
        }
    }
}

