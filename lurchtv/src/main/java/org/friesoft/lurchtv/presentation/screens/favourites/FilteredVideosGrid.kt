package org.friesoft.lurchtv.presentation.screens.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.presentation.common.VideoCard
import org.friesoft.lurchtv.presentation.common.PosterImage
import org.friesoft.lurchtv.presentation.theme.LurchTVBottomListPadding

@Composable
fun FilteredVideosGrid(
    state: LazyGridState,
    videoList: VideoList,
    onVideoClick: (videoId: String) -> Unit,
) {
    LazyVerticalGrid(
        state = state,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(6),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = LurchTVBottomListPadding),
    ) {
        items(videoList, key = { it.id }) { video ->
            VideoCard(
                onClick = { onVideoClick(video.id) },
                modifier = Modifier.aspectRatio(1 / 1.5f),
            ) {
                PosterImage(video = video, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
