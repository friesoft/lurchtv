
package org.friesoft.lurchtv.presentation.screens.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Surface as TvSurface
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.presentation.theme.LurchTVTheme
import org.friesoft.lurchtv.presentation.utils.isTv

enum class VideoPlayerMediaTitleType { AD, LIVE, DEFAULT }

@Composable
fun VideoPlayerMediaTitle(
    title: String,
    secondaryText: String,
    tertiaryText: String,
    modifier: Modifier = Modifier,
    type: VideoPlayerMediaTitleType = VideoPlayerMediaTitleType.DEFAULT
) {
    val isTv = isTv()
    val subTitle = buildString {
        append(secondaryText)
        if (secondaryText.isNotEmpty() && tertiaryText.isNotEmpty()) append(" • ")
        append(tertiaryText)
    }
    Column(modifier.fillMaxWidth()) {
        if (isTv) {
            TvText(title, style = TvMaterialTheme.typography.titleMedium)
        } else {
            MobileText(
                text = title,
                style = MobileMaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
        Spacer(Modifier.height(4.dp))
        Row {
            // TODO: Replaced with Badge component once developed
            when (type) {
                VideoPlayerMediaTitleType.AD -> {
                    if (isTv) {
                        TvText(
                            text = stringResource(R.string.ad),
                            style = TvMaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier
                                .background(Color(0xFFFBC02D), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .alignByBaseline()
                        )
                    } else {
                        MobileText(
                            text = stringResource(R.string.ad),
                            style = MobileMaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier
                                .background(Color(0xFFFBC02D), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .alignByBaseline()
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                VideoPlayerMediaTitleType.LIVE -> {
                    if (isTv) {
                        TvText(
                            text = stringResource(R.string.live),
                            style = TvMaterialTheme.typography.labelSmall,
                            color = TvMaterialTheme.colorScheme.inverseSurface,
                            modifier = Modifier
                                .background(Color(0xFFCC0000), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .alignByBaseline()
                        )
                    } else {
                        MobileText(
                            text = stringResource(R.string.live),
                            style = MobileMaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFFCC0000), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .alignByBaseline()
                        )
                    }

                    Spacer(Modifier.width(8.dp))
                }

                VideoPlayerMediaTitleType.DEFAULT -> {}
            }

            if (isTv) {
                TvText(
                    text = subTitle,
                    style = TvMaterialTheme.typography.labelSmall,
                    modifier = Modifier.alignByBaseline()
                )
            } else {
                MobileText(
                    text = subTitle,
                    style = MobileMaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.alignByBaseline()
                )
            }
        }
    }
}

@Preview(name = "TV Series", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewSeries() {
    LurchTVTheme {
        TvSurface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "True Detective",
                secondaryText = "S1E5",
                tertiaryText = "The Secret Fate Of All Life",
                type = VideoPlayerMediaTitleType.DEFAULT
            )
        }
    }
}

@Preview(name = "Live", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewLive() {
    LurchTVTheme {
        TvSurface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "MacLaren Reveal Their 2022 Car: The MCL36",
                secondaryText = "Formula 1",
                tertiaryText = "54K watching now",
                type = VideoPlayerMediaTitleType.LIVE
            )
        }
    }
}

@Preview(name = "Ads", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewAd() {
    LurchTVTheme {
        TvSurface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "Samsung Galaxy Note20 | Ultra 5G",
                secondaryText = "Get the most powerful Note yet",
                tertiaryText = "",
                type = VideoPlayerMediaTitleType.AD
            )
        }
    }
}

