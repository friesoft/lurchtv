package org.friesoft.lurchtv.presentation.screens.search

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Surface as TvSurface
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.ImmersiveVideoList
import org.friesoft.lurchtv.presentation.common.ItemDirection
import org.friesoft.lurchtv.presentation.common.VideosRow
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding
import org.friesoft.lurchtv.presentation.theme.LurchTVCardShape
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun SearchScreen(
    onVideoClick: (video: Video) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val isTv = isTv()
    val lazyColumnState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyColumnState.firstVisibleItemIndex == 0 &&
                lazyColumnState.firstVisibleItemScrollOffset < 100
        }
    }

    val searchState by searchScreenViewModel.searchState.collectAsStateWithLifecycle()
    val searchQuery by searchScreenViewModel.searchQuery.collectAsStateWithLifecycle()

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    SearchResult(
        isTv = isTv,
        searchQuery = searchQuery,
        searchState = searchState,
        searchVideos = searchScreenViewModel::query,
        onVideoClick = onVideoClick,
        lazyColumnState = lazyColumnState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResult(
    isTv: Boolean,
    searchQuery: String,
    searchState: SearchState,
    searchVideos: (queryString: String) -> Unit,
    onVideoClick: (video: Video) -> Unit,
    modifier: Modifier = Modifier,
    lazyColumnState: LazyListState = rememberLazyListState(),
) {
    val childPadding = if (isTv) rememberChildPadding() else org.friesoft.lurchtv.presentation.utils.Padding(16.dp, 16.dp, 16.dp, 16.dp)
    val tfFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val tfInteractionSource = remember { MutableInteractionSource() }

    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()
    LazyColumn(
        modifier = modifier,
        state = lazyColumnState
    ) {
        item {
            if (isTv) {
                TvSurface(
                    shape = ClickableSurfaceDefaults.shape(shape = LurchTVCardShape),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = TvMaterialTheme.colorScheme.inverseOnSurface,
                        focusedContainerColor = TvMaterialTheme.colorScheme.inverseOnSurface,
                        pressedContainerColor = TvMaterialTheme.colorScheme.inverseOnSurface,
                        focusedContentColor = TvMaterialTheme.colorScheme.onSurface,
                        pressedContentColor = TvMaterialTheme.colorScheme.onSurface
                    ),
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(
                                width = if (isTfFocused) 2.dp else 1.dp,
                                color = animateColorAsState(
                                    targetValue = if (isTfFocused) TvMaterialTheme.colorScheme.primary
                                    else TvMaterialTheme.colorScheme.border,
                                    label = ""
                                ).value
                            ),
                            shape = LurchTVCardShape
                        )
                    ),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .padding(horizontal = childPadding.start)
                        .padding(top = 8.dp),
                    onClick = { tfFocusRequester.requestFocus() }
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { updatedQuery -> searchVideos(updatedQuery) },
                        decorationBox = {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .padding(start = 20.dp),
                            ) {
                                it()
                                if (searchQuery.isEmpty()) {
                                    TvText(
                                        modifier = Modifier.graphicsLayer { alpha = 0.6f },
                                        text = stringResource(R.string.search_screen_et_placeholder),
                                        style = TvMaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp,
                                horizontal = 8.dp
                            )
                            .focusRequester(tfFocusRequester)
                            .onKeyEvent {
                                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                    when (it.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                                            focusManager.moveFocus(FocusDirection.Down)
                                        }

                                        KeyEvent.KEYCODE_DPAD_UP -> {
                                            focusManager.moveFocus(FocusDirection.Up)
                                        }

                                        KeyEvent.KEYCODE_BACK -> {
                                            focusManager.moveFocus(FocusDirection.Exit)
                                        }
                                    }
                                }
                                true
                            },
                        cursorBrush = Brush.verticalGradient(
                            colors = listOf(
                                LocalContentColor.current,
                                LocalContentColor.current,
                            )
                        ),
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                searchVideos(searchQuery)
                            }
                        ),
                        maxLines = 1,
                        interactionSource = tfInteractionSource,
                        textStyle = TvMaterialTheme.typography.titleSmall.copy(
                            color = TvMaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            } else {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { updatedQuery ->
                        searchVideos(updatedQuery)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = { MobileText(stringResource(R.string.search_screen_et_placeholder)) },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchVideos(searchQuery)
                        }
                    ),
                    maxLines = 1,
                    singleLine = true,
                    shape = LurchTVCardShape
                )
            }
        }

        when (searchState) {
            is SearchState.Searching -> {
                item {
                    if (isTv) {
                        TvText(
                            text = "Searching...",
                            modifier = Modifier.padding(start = childPadding.start, top = 16.dp)
                        )
                    } else {
                        MobileText(text = "Searching...", modifier = Modifier.padding(16.dp))
                    }
                }
            }

            is SearchState.Done -> {
                searchState.categories.forEach { (title, videoList) ->
                    item {
                        if (isTv) {
                            ImmersiveVideoList(
                                title = title,
                                videoList = videoList,
                                onVideoClick = onVideoClick
                            )
                        } else {
                            VideosRow(
                                title = title,
                                videoList = videoList,
                                onVideoSelected = onVideoClick,
                                itemDirection = ItemDirection.Horizontal,
                                startPadding = 16.dp,
                                endPadding = 16.dp
                            )
                        }
                    }
                }
            }
        }
    }
}



