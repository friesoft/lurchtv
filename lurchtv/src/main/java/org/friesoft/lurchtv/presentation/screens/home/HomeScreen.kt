package org.friesoft.lurchtv.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.Error
import org.friesoft.lurchtv.presentation.common.ImmersiveVideoList
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.common.PosterImage
import org.friesoft.lurchtv.presentation.common.VideoCard
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding

import org.friesoft.lurchtv.presentation.common.ItemDirection
import org.friesoft.lurchtv.presentation.common.VideosRow
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun HomeScreen(
    onVideoClick: (video: Video) -> Unit,
    goToVideoPlayer: (video: Video) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    lastWatchedVideoId: String? = null,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val uiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            if (isTv()) {
                CatalogTv(
                    featuredVideos = s.featuredVideoList,
                    recentVideos = s.recentVideoList,
                    top10Videos = s.top10VideoList,
                    onVideoClick = onVideoClick,
                    onScroll = onScroll,
                    goToVideoPlayer = goToVideoPlayer,
                    isTopBarVisible = isTopBarVisible,
                    lastWatchedVideoId = lastWatchedVideoId,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                CatalogMobile(
                    featuredVideos = s.featuredVideoList,
                    recentVideos = s.recentVideoList,
                    top10Videos = s.top10VideoList,
                    onVideoClick = onVideoClick,
                    goToVideoPlayer = goToVideoPlayer,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        is HomeScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        is HomeScreenUiState.Error -> Error(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun CatalogMobile(
    featuredVideos: VideoList,
    recentVideos: VideoList,
    top10Videos: VideoList,
    onVideoClick: (video: Video) -> Unit,
    goToVideoPlayer: (video: Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier
    ) {
        if (featuredVideos.isNotEmpty()) {
            item {
                val featuredVideo = featuredVideos.first()
                Box(modifier = Modifier.padding(16.dp)) {
                    VideoCard(
                        onClick = { onVideoClick(featuredVideo) },
                        modifier = Modifier.fillMaxWidth(),
                        title = {
                             MobileText(
                                text = featuredVideo.name,
                                style = MobileMaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    ) {
                        PosterImage(
                            video = featuredVideo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        )
                    }
                }
            }
        }
        item {
            VideosRow(
                videoList = recentVideos,
                title = stringResource(id = R.string.home_screen_recent_title),
                onVideoSelected = onVideoClick,
                itemDirection = ItemDirection.Horizontal,
                startPadding = 16.dp,
                endPadding = 16.dp
            )
        }
        item {
            VideosRow(
                videoList = top10Videos,
                title = stringResource(id = R.string.top_10_videos_title),
                onVideoSelected = onVideoClick,
                itemDirection = ItemDirection.Vertical,
                showIndexOverImage = true,
                startPadding = 16.dp,
                endPadding = 16.dp
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CatalogTv(
    featuredVideos: VideoList,
    recentVideos: VideoList,
    top10Videos: VideoList,
    onVideoClick: (video: Video) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    goToVideoPlayer: (video: Video) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
    lastWatchedVideoId: String? = null,
) {

    val lazyListState = rememberLazyListState()
    val childPadding = rememberChildPadding()
    var immersiveListHasFocus by remember { mutableStateOf(false) }

    var lastFocusedSection by rememberSaveable { mutableStateOf<String?>(null) }
    val carouselFocusRequester = remember { FocusRequester() }
    val recentsFocusRequester = remember { FocusRequester() }
    val top10FocusRequester = remember { FocusRequester() }

    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset < 300
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        // Setting overscan margin to bottom to ensure the last row's visibility
        modifier = modifier.focusRestorer(
            if (lastWatchedVideoId != null) {
                when (lastFocusedSection) {
                    "carousel" -> carouselFocusRequester
                    "recents" -> recentsFocusRequester
                    "top10" -> top10FocusRequester
                    else -> FocusRequester.Default
                }
            } else {
                FocusRequester.Default
            }
        ),
    ) {

        item(contentType = "FeaturedVideosCarousel") {
            FeaturedVideosCarousel(
                videos = featuredVideos,
                padding = childPadding,
                goToVideoPlayer = {
                    lastFocusedSection = "carousel"
                    goToVideoPlayer(it)
                },
                lastWatchedVideoId = lastWatchedVideoId,
                modifier = Modifier
                    .focusRequester(carouselFocusRequester)
                    .fillMaxWidth()
                    .height(324.dp)
                /*
                 Setting height for the FeaturedVideoCarousel to keep it rendered with same height,
                 regardless of the top bar's visibility
                 */
            )
        }
        item(contentType = "ImmersiveVideoList") {
            ImmersiveVideoList(
                videoList = recentVideos,
                title = stringResource(id = R.string.home_screen_recent_title),
                onVideoClick = {
                    lastFocusedSection = "recents"
                    onVideoClick(it)
                },
                lastWatchedVideoId = lastWatchedVideoId,
                modifier = Modifier.focusRequester(recentsFocusRequester)
            )
        }
        item(contentType = "Top10VideosList") {
            Top10VideosList(
                videoList = top10Videos,
                onVideoClick = {
                    lastFocusedSection = "top10"
                    onVideoClick(it)
                },
                lastWatchedVideoId = lastWatchedVideoId,
                modifier = Modifier
                    .focusRequester(top10FocusRequester)
                    .onFocusChanged {
                        immersiveListHasFocus = it.hasFocus
                    },
            )
        }
    }
}
