package org.friesoft.lurchtv.presentation.screens.favourites

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.entities.VideoList
import org.friesoft.lurchtv.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FavouriteScreenViewModel @Inject constructor(
    videoRepository: VideoRepository
) : ViewModel() {

    private val selectedFilterList = MutableStateFlow(FilterList())

    val uiState: StateFlow<FavouriteScreenUiState> = combine(
        selectedFilterList,
        videoRepository.getFavouriteVideos()
    ) { filterList, videoList ->
        val idList = filterList.toIdList()
        val filtered = videoList.filterIndexed { index, _ ->
            idList.contains(index)
        }
        FavouriteScreenUiState.Ready(filtered, filterList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavouriteScreenUiState.Loading
    )

    fun updateSelectedFilterList(filterList: FilterList) {
        selectedFilterList.value = filterList
    }

    companion object {
        val filterList = FilterList(
            listOf(
                FilterCondition.Videos,
                FilterCondition.AddedLastWeek,
                FilterCondition.AvailableIn4K
            )
        )
    }
}

sealed interface FavouriteScreenUiState {
    data object Loading : FavouriteScreenUiState
    data class Ready(val favouriteVideoList: VideoList, val selectedFilterList: FilterList) :
        FavouriteScreenUiState
}

@Immutable
data class FilterList(val items: List<FilterCondition> = emptyList()) {
    fun toIdList(): List<Int> {
        if (items.isEmpty()) {
            return FilterCondition.None.idList
        }
        return items.asSequence().map {
            it.idList
        }.fold(emptyList()) { acc, ints ->
            acc + ints
        }
    }
}

@Immutable
enum class FilterCondition(val idList: List<Int>, @StringRes val labelId: Int) {
    None((0..28).toList(), R.string.favorites_unknown),
    Videos((0..9).toList(), R.string.favorites_videos),
    AddedLastWeek((18..23).toList(), R.string.favorites_added_last_week),
    AvailableIn4K((24..28).toList(), R.string.favorites_available_in_4k),
}
