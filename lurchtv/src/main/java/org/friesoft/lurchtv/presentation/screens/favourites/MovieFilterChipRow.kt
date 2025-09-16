package org.friesoft.lurchtv.presentation.screens.favourites

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.friesoft.lurchtv.presentation.utils.createInitialFocusRestorerModifiers

@Composable
fun MovieFilterChipRow(
    filterList: FilterList,
    selectedFilterList: FilterList,
    onSelectedFilterListUpdated: (FilterList) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRestorerModifiers = createInitialFocusRestorerModifiers()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .then(focusRestorerModifiers.parentModifier),
    ) {
        filterList.items.forEachIndexed { index, filterCondition ->
            val isChecked = selectedFilterList.items.contains(filterCondition)
            MovieFilterChip(
                label = stringResource(id = filterCondition.labelId),
                isChecked = isChecked,
                onCheckedChange = {
                    val updated = if (it) {
                        selectedFilterList.items + listOf(filterCondition)
                    } else {
                        selectedFilterList.items - setOf(filterCondition)
                    }
                    onSelectedFilterListUpdated(FilterList(updated))
                },
                modifier = if (index == 0) {
                    focusRestorerModifiers.childModifier
                } else {
                    Modifier
                }
            )
        }
    }
}
