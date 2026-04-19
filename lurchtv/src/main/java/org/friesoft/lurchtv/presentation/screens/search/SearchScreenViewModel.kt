package org.friesoft.lurchtv.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val internalSearchState = MutableSharedFlow<SearchState>()

    fun query(queryString: String) {
        viewModelScope.launch { postQuery(queryString) }
    }

    private suspend fun postQuery(queryString: String) {
        internalSearchState.emit(SearchState.Searching)
        val result = videoRepository.searchVideosCategorized(query = queryString)
        internalSearchState.emit(SearchState.Done(result))
    }

    val searchState = combine(
        internalSearchState,
        videoRepository.getAllPlaybackPositions()
    ) { state, progressMap ->
        if (state is SearchState.Done) {
            val enrichedCategories = state.categories.mapValues { (_, videos) ->
                videos.map { video ->
                    video.copy(lastPlaybackPosition = progressMap[video.id] ?: 0L)
                }
            }
            SearchState.Done(enrichedCategories)
        } else {
            state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchState.Done(emptyMap())
    )
}

sealed interface SearchState {
    data object Searching : SearchState
    data class Done(val categories: Map<String, VideoList>) : SearchState
}
