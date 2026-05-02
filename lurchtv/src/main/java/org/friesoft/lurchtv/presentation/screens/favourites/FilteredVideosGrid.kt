package org.friesoft.lurchtv.presentation.screens.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon as TvIcon
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.Icon as MobileIcon
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.PosterImage
import org.friesoft.lurchtv.presentation.common.VideoCard
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import org.friesoft.lurchtv.presentation.theme.LurchTVBottomListPadding
import org.friesoft.lurchtv.presentation.utils.isTv

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FilteredVideosGrid(
    state: LazyGridState,
    videoList: VideoList,
    onVideoClick: (videoId: String) -> Unit,
    lastWatchedVideoId: String? = null,
) {
    val isTv = isTv()
    val videoFocusRequesters = remember(videoList) {
        videoList.associate { it.id to FocusRequester() }
    }

    LazyVerticalGrid(
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .focusGroup()
            .focusRestorer {
                val lastWatchedIndex = videoList.indexOfFirst { it.id == lastWatchedVideoId }
                if (lastWatchedIndex != -1) {
                    videoFocusRequesters[lastWatchedVideoId]!!
                } else {
                    videoFocusRequesters[videoList.firstOrNull()?.id] ?: FocusRequester.Default
                }
            },
        columns = if (isTv) GridCells.Fixed(5) else GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = if (isTv) PaddingValues(bottom = LurchTVBottomListPadding) else PaddingValues(16.dp),
    ) {
        items(videoList, key = { it.id }) { video ->
            GridVideoItem(
                modifier = Modifier.focusRequester(videoFocusRequesters[video.id]!!),
                video = video,
                onClick = { onVideoClick(video.id) },
                isTv = isTv
            )
        }
    }
}

@Composable
fun GridVideoItem(
    video: Video,
    onClick: () -> Unit,
    isTv: Boolean,
    modifier: Modifier = Modifier
) {
    VideoCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        title = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (isTv) {
                    TvText(
                        text = video.name,
                        style = TvMaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    MobileText(
                        text = video.name,
                        style = MobileMaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isTv) {
                        TvText(
                            text = "${video.views} Aufrufe",
                            style = TvMaterialTheme.typography.bodySmall,
                            color = TvMaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        TvText(
                            text = video.createdAt.take(10),
                            style = TvMaterialTheme.typography.bodySmall,
                            color = TvMaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        MobileText(
                            text = "${video.views} Aufrufe",
                            style = MobileMaterialTheme.typography.bodySmall,
                            color = MobileMaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        MobileText(
                            text = video.createdAt.take(10),
                            style = MobileMaterialTheme.typography.bodySmall,
                            color = MobileMaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) {
        Box {
            PosterImage(
                video = video,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            // Duration overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTv) {
                    TvIcon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                } else {
                    MobileIcon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                val hours = video.videoLength / 3600
                val minutes = (video.videoLength % 3600) / 60
                val seconds = video.videoLength % 60
                val durationText = if (hours > 0) {
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                }
                if (isTv) {
                    TvText(
                        text = durationText,
                        style = TvMaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                } else {
                    MobileText(
                        text = durationText,
                        style = MobileMaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            // Episode overlay
            if (isTv) {
                TvText(
                    text = "# ${video.episode}",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    style = TvMaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            } else {
                MobileText(
                    text = "# ${video.episode}",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MobileMaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

