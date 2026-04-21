package org.friesoft.lurchtv.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f),
    Horizontal(16f / 9f);
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideosRow(
    videoList: VideoList,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    lastWatchedVideoId: String? = null,
    onVideoSelected: (video: Video) -> Unit = {}
) {
    val lazyRow = remember { FocusRequester() }
    val videoFocusRequesters = remember(videoList) {
        videoList.associate { it.id to FocusRequester() }
    }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .focusRequester(lazyRow)
                .focusRestorer {
                    val lastWatchedIndex = videoList.indexOfFirst { it.id == lastWatchedVideoId }
                    if (lastWatchedIndex != -1) {
                        videoFocusRequesters[lastWatchedVideoId]!!
                    } else {
                        videoFocusRequesters[videoList.firstOrNull()?.id]
                            ?: FocusRequester.Default
                    }
                }
        ) {
            itemsIndexed(videoList, key = { _, video -> video.id }) { _, video ->
                VideoRowItem(
                    modifier = Modifier.focusRequester(videoFocusRequesters[video.id]!!),
                    index = videoList.indexOf(video),
                    itemDirection = itemDirection,
                    onVideoSelected = {
                        lazyRow.saveFocusedChild()
                        onVideoSelected(it)
                    },
                    video = video,
                    showItemTitle = showItemTitle,
                    showIndexOverImage = showIndexOverImage
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImmersiveListVideosRow(
    videoList: VideoList,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    lastWatchedVideoId: String? = null,
    onVideoSelected: (Video) -> Unit = {},
    onVideoFocused: (Video) -> Unit = {}
) {
    val lazyRow = remember { FocusRequester() }
    val videoFocusRequesters = remember(videoList) {
        videoList.associate { it.id to FocusRequester() }
    }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding)
                    .padding(vertical = 16.dp)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(start = startPadding, end = endPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .focusRequester(lazyRow)
                .focusRestorer {
                    val lastWatchedIndex = videoList.indexOfFirst { it.id == lastWatchedVideoId }
                    if (lastWatchedIndex != -1) {
                        videoFocusRequesters[lastWatchedVideoId]!!
                    } else {
                        videoFocusRequesters[videoList.firstOrNull()?.id]
                            ?: FocusRequester.Default
                    }
                }
        ) {
            itemsIndexed(
                videoList,
                key = { _, video ->
                    video.id
                }
            ) { _, video ->
                VideoRowItem(
                    modifier = Modifier.focusRequester(videoFocusRequesters[video.id]!!),
                    index = videoList.indexOf(video),
                    itemDirection = itemDirection,
                    onVideoSelected = {
                        lazyRow.saveFocusedChild()
                        onVideoSelected(it)
                    },
                    onVideoFocused = onVideoFocused,
                    video = video,
                    showItemTitle = showItemTitle,
                    showIndexOverImage = showIndexOverImage
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun VideoRowItem(
    index: Int,
    video: Video,
    onVideoSelected: (Video) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    onVideoFocused: (Video) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    val width = if (itemDirection == ItemDirection.Vertical) 150.dp else 240.dp

    VideoCard(
        onClick = { onVideoSelected(video) },
        title = {
            VideoRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                video = video
            )
        },
        modifier = Modifier
            .width(width)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onVideoFocused(video)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
    ) {
        VideoRowItemImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            video = video,
            index = index
        )
    }
}

@Composable
private fun VideoRowItemImage(
    video: Video,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImage(
            video = video,
            modifier = modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (showIndexOverImage) {
                        drawRect(
                            color = Color.Black.copy(
                                alpha = 0.1f
                            )
                        )
                    }
                },
        )
        if (showIndexOverImage) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "#${index.inc()}",
                style = MaterialTheme.typography.displayLarge
                    .copy(
                        shadow = Shadow(
                            offset = Offset(0.5f, 0.5f),
                            blurRadius = 5f
                        ),
                        color = Color.White
                    ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun VideoRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    video: Video,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val videoNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            label = "",
        )
        Text(
            text = video.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(videoNameAlpha)
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
    }
}
