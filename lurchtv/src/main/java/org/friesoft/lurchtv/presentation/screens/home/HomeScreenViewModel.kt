package org.friesoft.lurchtv.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeScreenViewModel @Inject constructor(videoRepository: VideoRepository) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        videoRepository.getFeaturedVideos(),
        videoRepository.getRecentVideos(),
        videoRepository.getTop10Videos(),
    ) { featuredVideoList, recentVideoList, top10VideoList ->
        HomeScreenUiState.Ready(
            featuredVideoList,
            recentVideoList,
            top10VideoList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val featuredVideoList: VideoList,
        val recentVideoList: VideoList,
        val top10VideoList: VideoList
    ) : HomeScreenUiState
}
