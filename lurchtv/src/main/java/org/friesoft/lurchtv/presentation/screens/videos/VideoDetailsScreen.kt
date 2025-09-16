package org.friesoft.lurchtv.presentation.screens.videos

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.presentation.common.Error
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.common.VideosRow
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding

object VideoDetailsScreen {
    const val VideoIdBundleKey = "videoId"
}

@Composable
fun VideoDetailsScreen(
    goToVideoPlayer: () -> Unit,
    onBackPressed: () -> Unit,
    refreshScreenWithNewVideo: (Video) -> Unit,
    videoDetailsScreenViewModel: VideoDetailsScreenViewModel = hiltViewModel()
) {
    val uiState by videoDetailsScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is VideoDetailsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is VideoDetailsScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is VideoDetailsScreenUiState.Done -> {
            Details(
                videoDetails = s.videoDetails,
                goToVideoPlayer = goToVideoPlayer,
                onBackPressed = onBackPressed,
                refreshScreenWithNewVideo = refreshScreenWithNewVideo,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun Details(
    videoDetails: VideoDetails,
    goToVideoPlayer: () -> Unit,
    onBackPressed: () -> Unit,
    refreshScreenWithNewVideo: (Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()

    BackHandler(onBack = onBackPressed)
    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item {
            VideoDetails(
                videoDetails = videoDetails,
                goToVideoPlayer = goToVideoPlayer
            )
        }

        item {
            VideosRow(
                title = StringConstants
                    .Composable
                    .videoDetailsScreenSimilarTo(videoDetails.name),
                titleStyle = MaterialTheme.typography.titleMedium,
                videoList = videoDetails.similarVideos,
                onVideoSelected = refreshScreenWithNewVideo
            )
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = childPadding.start)
                    .padding(BottomDividerPadding)
                    .fillMaxWidth()
                    .height(1.dp)
                    .alpha(0.15f)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemModifier = Modifier.width(192.dp)

                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.status),
                    value = videoDetails.status
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.original_language),
                    value = videoDetails.originalLanguage
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.budget),
                    value = videoDetails.budget
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.revenue),
                    value = videoDetails.revenue
                )
            }
        }
    }
}

private val BottomDividerPadding = PaddingValues(vertical = 48.dp)
