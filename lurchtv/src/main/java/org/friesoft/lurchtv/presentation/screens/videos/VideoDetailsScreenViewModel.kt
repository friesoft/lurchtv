package org.friesoft.lurchtv.presentation.screens.videos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: VideoRepository,
) : ViewModel() {
    val uiState = combine(
        savedStateHandle.getStateFlow<String?>(VideoDetailsScreen.VideoIdBundleKey, null),
        repository.getAllPlaybackPositions()
    ) { id, progressMap ->
        if (id == null) {
            VideoDetailsScreenUiState.Error
        } else {
            val details = repository.getVideoDetails(videoId = id)
            val enrichedSimilar = details.similarVideos.map { video ->
                video.copy(lastPlaybackPosition = progressMap[video.id] ?: 0L)
            }
            VideoDetailsScreenUiState.Done(
                videoDetails = details.copy(
                    similarVideos = enrichedSimilar,
                    lastPlaybackPosition = progressMap[details.id] ?: 0L
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VideoDetailsScreenUiState.Loading
    )
}

sealed class VideoDetailsScreenUiState {
    data object Loading : VideoDetailsScreenUiState()
    data object Error : VideoDetailsScreenUiState()
    data class Done(val videoDetails: VideoDetails) : VideoDetailsScreenUiState()
}
