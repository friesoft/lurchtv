
package org.friesoft.lurchtv.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun Error(modifier: Modifier = Modifier) {
    if (isTv()) {
        TvText(
            text = stringResource(id = R.string.message_error),
            modifier = modifier
        )
    } else {
        MobileText(
            text = stringResource(id = R.string.message_error),
            modifier = modifier
        )
    }
}

