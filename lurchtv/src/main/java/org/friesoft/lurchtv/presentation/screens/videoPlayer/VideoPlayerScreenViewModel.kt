package org.friesoft.lurchtv.presentation.screens.videoPlayer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.data.entities.VideoDetails
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class VideoPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VideoRepository,
) : ViewModel() {
    private val videoId: String? = savedStateHandle[VideoPlayerScreen.VideoIdBundleKey]

    fun savePlaybackPosition(position: Long) {
        videoId?.let { id ->
            viewModelScope.launch {
                repository.savePlaybackPosition(id, position)
            }
        }
    }

    suspend fun getPlaybackPosition(): Long {
        return videoId?.let { repository.getPlaybackPosition(it) } ?: 0L
    }

    val uiState = savedStateHandle
        .getStateFlow<String?>(VideoPlayerScreen.VideoIdBundleKey, null)
        .map { id ->
            if (id == null) {
                VideoPlayerScreenUiState.Error
            } else {
                val details = repository.getVideoDetails(videoId = id)
                VideoPlayerScreenUiState.Done(videoDetails = details)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoPlayerScreenUiState.Loading
        )
}

@Immutable
sealed class VideoPlayerScreenUiState {
    data object Loading : VideoPlayerScreenUiState()
    data object Error : VideoPlayerScreenUiState()
    data class Done(val videoDetails: VideoDetails) : VideoPlayerScreenUiState()
}
