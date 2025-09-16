package org.friesoft.lurchtv.presentation.screens.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideosScreenViewModel @Inject constructor(
    videoRepository: VideoRepository
) : ViewModel() {

    val uiState = combine(
        videoRepository.getVideosWithLongThumbnail(),
        videoRepository.getPopularVideosThisWeek(),
    ) { (videoList, popularFilmsThisWeek) ->
        VideosScreenUiState.Ready(
            videoList = videoList,
            popularFilmsThisWeek = popularFilmsThisWeek
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VideosScreenUiState.Loading
    )
}

sealed interface VideosScreenUiState {
    data object Loading : VideosScreenUiState
    data class Ready(
        val videoList: VideoList,
        val popularFilmsThisWeek: VideoList
    ) : VideosScreenUiState
}
