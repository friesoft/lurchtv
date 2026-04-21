package org.friesoft.lurchtv.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding
import org.friesoft.lurchtv.presentation.utils.bringIntoViewIfChildrenAreFocused

@Composable
fun ImmersiveVideoList(
    videoList: VideoList,
    title: String,
    modifier: Modifier = Modifier,
    showIndexOverImage: Boolean = false,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    gradientColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    lastWatchedVideoId: String? = null,
    onVideoClick: (video: Video) -> Unit
) {
    if (videoList.isEmpty()) return

    var isListFocused by remember { mutableStateOf(false) }
    var selectedVideo by remember(videoList) {
        val initialVideo = videoList.find { it.id == lastWatchedVideoId } ?: videoList.first()
        mutableStateOf(initialVideo)
    }

    val sectionTitle = if (isListFocused) null else title

    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier.bringIntoViewIfChildrenAreFocused(
            PaddingValues(bottom = 116.dp)
        )
    ) {
        ImmersiveBackground(
            video = selectedVideo,
            visible = isListFocused,
            modifier = Modifier
                .height(432.dp)
                .gradientOverlay(gradientColor)
        )
        Column {
            if (isListFocused) {
                Column(
                    modifier = Modifier.padding(
                        start = rememberChildPadding().start,
                        bottom = 40.dp
                    )
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ImmersiveVideoDescription(
                        video = selectedVideo
                    )
                }
            }

            ImmersiveListVideosRow(
                videoList = videoList,
                itemDirection = itemDirection,
                title = sectionTitle,
                showItemTitle = !isListFocused,
                showIndexOverImage = showIndexOverImage,
                onVideoSelected = onVideoClick,
                onVideoFocused = { selectedVideo = it },
                lastWatchedVideoId = lastWatchedVideoId,
                modifier = Modifier.onFocusChanged { isListFocused = it.hasFocus }
            )
        }
    }
}

@Composable
private fun ImmersiveBackground(
    video: Video,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Crossfade(
            targetState = video,
            label = "posterUriCrossfade",
        ) {
            PosterImage(video = it, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ImmersiveVideoDescription(
    video: Video,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = video.name,
            style = MaterialTheme.typography.displaySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.fillMaxWidth(0.5f),
            text = video.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontWeight = FontWeight.Light,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Modifier.gradientOverlay(gradientColor: Color): Modifier =
    drawWithCache {
        val horizontalGradient = Brush.horizontalGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            startX = size.width.times(0.2f),
            endX = size.width.times(0.7f)
        )
        val verticalGradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                gradientColor
            ),
            endY = size.width.times(0.3f)
        )
        val linearGradient = Brush.linearGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            start = Offset(
                size.width.times(0.2f),
                size.height.times(0.5f)
            ),
            end = Offset(
                size.width.times(0.9f),
                0f
            )
        )

        onDrawWithContent {
            drawContent()
            drawRect(horizontalGradient)
            drawRect(verticalGradient)
            drawRect(linearGradient)
        }
    }
