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
class HomeScreeViewModel @Inject constructor(videoRepository: VideoRepository) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        videoRepository.getFeaturedVideos(),
        videoRepository.getTrendingVideos(),
        videoRepository.getTop10Videos(),
        videoRepository.getNowPlayingVideos(),
    ) { featuredVideoList, trendingVideoList, top10VideoList, nowPlayingVideoList ->
        HomeScreenUiState.Ready(
            featuredVideoList,
            trendingVideoList,
            top10VideoList,
            nowPlayingVideoList
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
        val trendingVideoList: VideoList,
        val top10VideoList: VideoList,
        val nowPlayingVideoList: VideoList
    ) : HomeScreenUiState
}
