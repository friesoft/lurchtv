package org.friesoft.lurchtv.presentation.screens.videos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.common.VideosRow
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding

@Composable
fun VideosScreen(
    onVideoClick: (video: Video) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    videosScreenViewModel: VideosScreenViewModel = hiltViewModel(),
) {
    val uiState by videosScreenViewModel.uiState.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is VideosScreenUiState.Loading -> Loading()
        is VideosScreenUiState.Ready -> {
            Catalog(
                videoList = s.videoList,
                popularFilmsThisWeek = s.popularFilmsThisWeek,
                onVideoClick = onVideoClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun Catalog(
    videoList: VideoList,
    popularFilmsThisWeek: VideoList,
    onVideoClick: (video: Video) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(top = childPadding.top, bottom = 104.dp)
    ) {
        item {
            VideosScreenVideoList(
                videoList = videoList,
                onVideoClick = onVideoClick
            )
        }
        item {
            VideosRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = StringConstants.Composable.PopularFilmsThisWeekTitle,
                videoList = popularFilmsThisWeek,
                onVideoSelected = onVideoClick
            )
        }
    }
}
