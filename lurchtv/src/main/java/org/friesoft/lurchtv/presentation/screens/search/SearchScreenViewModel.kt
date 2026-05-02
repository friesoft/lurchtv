package org.friesoft.lurchtv.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val internalSearchState = _searchQuery
        .debounce(500L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.isEmpty()) {
                    emit(SearchState.Done(emptyMap()))
                    return@flow
                }
                emit(SearchState.Searching)
                val result = videoRepository.searchVideosCategorized(query = query)
                emit(SearchState.Done(result))
            }
        }

    fun query(queryString: String) {
        _searchQuery.value = queryString
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


