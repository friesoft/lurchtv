package org.friesoft.lurchtv.presentation.screens.favourites

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding

@Composable
fun FavouritesScreen(
    onVideoClick: (videoId: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    favouriteScreenViewModel: FavouriteScreenViewModel = hiltViewModel()
) {
    val uiState by favouriteScreenViewModel.uiState.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is FavouriteScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }
        is FavouriteScreenUiState.Ready -> {
            Catalog(
                favouriteVideoList = s.favouriteVideoList,
                onVideoClick = onVideoClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
                filterList = FavouriteScreenViewModel.filterList,
                selectedFilterList = s.selectedFilterList,
                onSelectedFilterListUpdated = favouriteScreenViewModel::updateSelectedFilterList
            )
        }
    }
}

@Composable
private fun Catalog(
    favouriteVideoList: VideoList,
    filterList: FilterList,
    selectedFilterList: FilterList,
    onVideoClick: (videoId: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    onSelectedFilterListUpdated: (FilterList) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()
    val filteredVideosGridState = rememberLazyGridState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            filteredVideosGridState.firstVisibleItemIndex == 0 &&
                filteredVideosGridState.firstVisibleItemScrollOffset < 100
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) filteredVideosGridState.animateScrollToItem(0)
    }

    val chipRowTopPadding by animateDpAsState(
        targetValue = if (shouldShowTopBar) 0.dp else childPadding.top, label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = childPadding.start)
    ) {
        VideoFilterChipRow(
            filterList = filterList,
            selectedFilterList = selectedFilterList,
            modifier = Modifier.padding(top = chipRowTopPadding),
            onSelectedFilterListUpdated = onSelectedFilterListUpdated
        )
        FilteredVideosGrid(
            state = filteredVideosGridState,
            videoList = favouriteVideoList,
            onVideoClick = onVideoClick
        )
    }
}
