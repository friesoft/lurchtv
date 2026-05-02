
package org.friesoft.lurchtv.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface as TvSurface
import androidx.compose.material3.Card as MobileCard
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import org.friesoft.lurchtv.presentation.theme.LurchTVBorderWidth
import org.friesoft.lurchtv.presentation.theme.LurchTVCardShape
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun VideoCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    if (isTv()) {
        StandardCardContainer(
            modifier = modifier,
            title = title,
            imageCard = {
                TvSurface(
                    onClick = onClick,
                    shape = ClickableSurfaceDefaults.shape(LurchTVCardShape),
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(
                                width = LurchTVBorderWidth,
                                color = TvMaterialTheme.colorScheme.onSurface
                            ),
                            shape = LurchTVCardShape
                        )
                    ),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                    content = image
                )
            },
        )
    } else {
        Column(modifier = modifier) {
            Box(
                modifier = Modifier
                    .clip(LurchTVCardShape)
                    .clickable { onClick() }
            ) {
                image()
            }
            Box(modifier = Modifier.padding(top = 4.dp)) {
                title()
            }
        }
    }
}

