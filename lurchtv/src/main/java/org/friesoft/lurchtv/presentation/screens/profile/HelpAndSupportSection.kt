package org.friesoft.lurchtv.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.presentation.theme.LurchTVCardShape

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HelpAndSupportSection() {
    Column(modifier = Modifier.padding(horizontal = 72.dp)) {
        Text(
            text = stringResource(id = R.string.help_and_support_section_title),
            style = MaterialTheme.typography.headlineSmall
        )
        HelpAndSupportSectionItem(
            title = stringResource(id = R.string.help_and_support_section_contact_item),
            value = StringConstants.Composable.Placeholders.HelpAndSupportSectionContactValue
        )
        HelpAndSupportSectionItem(
            title = stringResource(id = R.string.help_and_support_section_github_item),
            value = StringConstants.Composable.Placeholders.HelpAndSupportSectionGitHubValue
        )
        HelpAndSupportSectionItem(
            title = stringResource(id = R.string.help_and_support_section_homepage_item),
            value = StringConstants.Composable.Placeholders.HelpAndSupportSectionHomepageValue
        )
    }
}

@Composable
private fun HelpAndSupportSectionItem(
    title: String,
    value: String? = null
) {
    ListItem(
        modifier = Modifier.padding(top = 16.dp),
        selected = false,
        onClick = {},
        trailingContent = {
            value?.let { nnValue ->
                Text(
                    text = nnValue,
                    style = MaterialTheme.typography.titleMedium
                )
            } ?: run {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    modifier = Modifier.size(ListItemDefaults.IconSizeDense),
                    contentDescription = stringResource(id = R.string.help_and_support_section_list_item_icon_description)
                )
            }
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContentColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = ListItemDefaults.shape(shape = LurchTVCardShape)
    )
}
