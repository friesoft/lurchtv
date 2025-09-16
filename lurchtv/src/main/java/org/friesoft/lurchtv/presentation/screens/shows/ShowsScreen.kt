package org.friesoft.lurchtv.presentation.screens.shows

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
import org.friesoft.lurchtv.data.entities.Movie
import org.friesoft.lurchtv.data.entities.MovieList
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.presentation.common.Loading
import org.friesoft.lurchtv.presentation.common.MoviesRow
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding
import org.friesoft.lurchtv.presentation.screens.movies.MoviesScreenMovieList

@Composable
fun ShowsScreen(
    onTVShowClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    showScreenViewModel: ShowScreenViewModel = hiltViewModel(),
) {
    val uiState = showScreenViewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = uiState.value) {
        is ShowScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is ShowScreenUiState.Ready -> {
            Catalog(
                tvShowList = currentState.tvShowList,
                bingeWatchDramaList = currentState.bingeWatchDramaList,
                onTVShowClick = onTVShowClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun Catalog(
    tvShowList: MovieList,
    bingeWatchDramaList: MovieList,
    onTVShowClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier
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
            MoviesScreenMovieList(
                movieList = tvShowList,
                onMovieClick = onTVShowClick
            )
        }
        item {
            MoviesRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = StringConstants.Composable.BingeWatchDramasTitle,
                movieList = bingeWatchDramaList,
                onMovieSelected = onTVShowClick
            )
        }
    }
}
