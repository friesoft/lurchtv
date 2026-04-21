package org.friesoft.lurchtv.presentation.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.MaterialTheme
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.ImmersiveVideoList

@Composable
fun Top10VideosList(
    videoList: VideoList,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    lastWatchedVideoId: String? = null,
    onVideoClick: (video: Video) -> Unit
) {
    ImmersiveVideoList(
        videoList = videoList,
        title = stringResource(R.string.top_10_videos_title),
        modifier = modifier,
        showIndexOverImage = true,
        gradientColor = gradientColor,
        lastWatchedVideoId = lastWatchedVideoId,
        onVideoClick = onVideoClick
    )
}
