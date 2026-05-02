
package org.friesoft.lurchtv.presentation.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    style: TextStyle? = null
) {
    val isTv = isTv()
    Box(modifier = modifier.focusable(), contentAlignment = Alignment.Center) {
        if (isTv) {
            TvText(
                text = stringResource(id = R.string.message_loading),
                style = style ?: TvMaterialTheme.typography.displayMedium
            )
        } else {
            MobileText(
                text = stringResource(id = R.string.message_loading),
                style = style ?: MobileMaterialTheme.typography.displayMedium
            )
        }
    }
}

