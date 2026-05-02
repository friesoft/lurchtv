package org.friesoft.lurchtv.presentation.screens.videos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button as TvButton
import androidx.tv.material3.ButtonDefaults as TvButtonDefaults
import androidx.tv.material3.Icon as TvIcon
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.Button as MobileButton
import androidx.compose.material3.Icon as MobileIcon
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding
import org.friesoft.lurchtv.presentation.theme.LurchTVButtonShape
import org.friesoft.lurchtv.presentation.utils.isTv
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoDetails(
    videoDetails: VideoDetails,
    goToVideoPlayer: () -> Unit
) {
    val isTv = isTv()
    val childPadding = if (isTv) rememberChildPadding() else org.friesoft.lurchtv.presentation.utils.Padding(16.dp, 16.dp, 16.dp, 16.dp)
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isTv) 432.dp else 450.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        VideoImageWithGradients(
            videoDetails = videoDetails,
            isTv = isTv,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (!isTv) Modifier.statusBarsPadding() else Modifier)
        ) {
            Spacer(modifier = Modifier.height(if (isTv) 108.dp else 24.dp))
            Column(
                modifier = Modifier.padding(
                    start = childPadding.start,
                    end = childPadding.end
                )
            ) {
                VideoLargeTitle(videoTitle = videoDetails.name, isTv = isTv)

                Column(
                    modifier = Modifier
                        .fillMaxWidth(if (isTv) 0.7f else 1f)
                        .alpha(0.85f)
                ) {
                    val description = videoDetails.description.ifEmpty {
                        stringResource(
                            R.string.video_details_views_count,
                            videoDetails.views.toString(),
                            videoDetails.createdAt.take(10)
                        )
                    }
                    VideoDescription(description = description, isTv = isTv)
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 12.dp),
                        texts = listOf(
                            videoDetails.releaseDate,
                            videoDetails.categories.take(3).joinToString(", "),
                            videoDetails.duration
                        )
                    )
                    if (videoDetails.lastPlaybackPosition > 0) {
                        val posSeconds = videoDetails.lastPlaybackPosition / 1000
                        val h = posSeconds / 3600
                        val m = (posSeconds % 3600) / 60
                        val s = posSeconds % 60
                        val posText = if (h > 0) {
                            String.format("%02d:%02d:%02d", h, m, s)
                        } else {
                            String.format("%02d:%02d", m, s)
                        }
                        
                        if (isTv) {
                            TvText(
                                text = stringResource(R.string.video_details_watched_until, posText),
                                style = TvMaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else {
                            MobileText(
                                text = stringResource(R.string.video_details_watched_until, posText),
                                style = MobileMaterialTheme.typography.bodySmall,
                                color = MobileMaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        
                        if (videoDetails.videoLength > 0) {
                            val progress = videoDetails.lastPlaybackPosition.toFloat() / (videoDetails.videoLength * 1000f)
                            LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(0.6f)
                                    .height(4.dp),
                                color = if (isTv) TvMaterialTheme.colorScheme.primary else MobileMaterialTheme.colorScheme.primary,
                                trackColor = (if (isTv) TvMaterialTheme.colorScheme.onSurface else MobileMaterialTheme.colorScheme.onSurface).copy(alpha = 0.2f)
                            )
                        }
                    }
                }
                WatchNowButton(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    isTv = isTv,
                    goToVideoPlayer = goToVideoPlayer
                )
            }
        }
    }
}

@Composable
private fun WatchNowButton(
    isTv: Boolean,
    modifier: Modifier = Modifier,
    goToVideoPlayer: () -> Unit
) {
    if (isTv) {
        TvButton(
            onClick = goToVideoPlayer,
            modifier = modifier.padding(top = 24.dp),
            contentPadding = TvButtonDefaults.ButtonWithIconContentPadding,
            shape = TvButtonDefaults.shape(shape = LurchTVButtonShape)
        ) {
            TvIcon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null
            )
            Spacer(Modifier.size(8.dp))
            TvText(
                text = stringResource(R.string.watch_now),
                style = TvMaterialTheme.typography.titleSmall
            )
        }
    } else {
        MobileButton(
            onClick = goToVideoPlayer,
            modifier = modifier.padding(top = 24.dp),
            shape = LurchTVButtonShape
        ) {
            MobileIcon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null
            )
            Spacer(Modifier.size(8.dp))
            MobileText(
                text = stringResource(R.string.watch_now),
                style = MobileMaterialTheme.typography.titleSmall,
                color = MobileMaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun VideoDescription(description: String, isTv: Boolean) {
    if (isTv) {
        TvText(
            text = description,
            style = TvMaterialTheme.typography.titleSmall.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 3
        )
    } else {
        MobileText(
            text = description,
            style = MobileMaterialTheme.typography.titleSmall.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MobileMaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 3
        )
    }
}

@Composable
private fun VideoLargeTitle(videoTitle: String, isTv: Boolean) {
    if (isTv) {
        TvText(
            text = videoTitle,
            style = TvMaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    } else {
        MobileText(
            text = videoTitle,
            style = MobileMaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MobileMaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun VideoImageWithGradients(
    videoDetails: VideoDetails,
    isTv: Boolean,
    modifier: Modifier = Modifier,
) {
    val gradientColor = if (isTv) TvMaterialTheme.colorScheme.surface else MobileMaterialTheme.colorScheme.surface
    val contentDescription = stringResource(
        id = R.string.video_poster_content_description,
        videoDetails.name
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(videoDetails.posterUri)
            .crossfade(true).build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, gradientColor),
                    startY = if (isTv) 600f else 300f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endX = if (isTv) 1000f else 600f,
                    startX = if (isTv) 300f else 0f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = if (isTv) 500f else 200f, y = if (isTv) 500f else 200f),
                    end = Offset(x = if (isTv) 1000f else 600f, y = 0f)
                )
            )
        }
    )
}




