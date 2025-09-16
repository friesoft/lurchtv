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
import org.friesoft.lurchtv.data.entities.MovieList
import org.friesoft.lurchtv.presentation.common.MovieCard
import org.friesoft.lurchtv.presentation.common.PosterImage
import org.friesoft.lurchtv.presentation.theme.LurchTVBottomListPadding

@Composable
fun FilteredMoviesGrid(
    state: LazyGridState,
    movieList: MovieList,
    onMovieClick: (movieId: String) -> Unit,
) {
    LazyVerticalGrid(
        state = state,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(6),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = LurchTVBottomListPadding),
    ) {
        items(movieList, key = { it.id }) { movie ->
            MovieCard(
                onClick = { onMovieClick(movie.id) },
                modifier = Modifier.aspectRatio(1 / 1.5f),
            ) {
                PosterImage(movie = movie, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
