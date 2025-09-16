package org.friesoft.lurchtv.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.MovieList
import org.friesoft.lurchtv.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val internalSearchState = MutableSharedFlow<SearchState>()

    fun query(queryString: String) {
        viewModelScope.launch { postQuery(queryString) }
    }

    private suspend fun postQuery(queryString: String) {
        internalSearchState.emit(SearchState.Searching)
        val result = movieRepository.searchMovies(query = queryString)
        internalSearchState.emit(SearchState.Done(result))
    }

    val searchState = internalSearchState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchState.Done(emptyList())
    )
}

sealed interface SearchState {
    data object Searching : SearchState
    data class Done(val movieList: MovieList) : SearchState
}
