package org.friesoft.lurchtv.presentation.screens.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.LocaleListCompat
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.data.util.StringConstants
import org.friesoft.lurchtv.presentation.theme.LurchTVCardShape

@Composable
fun LanguageSection() {
    val configuration = LocalConfiguration.current
    val currentLocale = if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
        AppCompatDelegate.getApplicationLocales()[0]?.language
    } else {
        configuration.locales[0].language
    }
    val items = StringConstants.Composable.Placeholders.LanguageSectionItems
    val locales = listOf("de", "en")

    LazyColumn(modifier = Modifier.padding(horizontal = 72.dp)) {
        item {
            Text(
                text = stringResource(id = R.string.language_section_title),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        items(items.size) { index ->
            // If current locale is not 'de', we treat it as 'en' (default fallback)
            val isSelected = if (locales[index] == "de") {
                currentLocale == "de"
            } else {
                currentLocale != "de"
            }
            ListItem(
                modifier = Modifier.padding(top = 16.dp),
                selected = false,
                onClick = {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(locales[index])
                    AppCompatDelegate.setApplicationLocales(appLocale)
                },
                trailingContent = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(
                                id =
                                R.string.language_section_listItem_icon_content_description,
                                items[index]
                            )
                        )
                    }
                } else null,
                headlineContent = {
                    Text(
                        text = items[index],
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                ),
                shape = ListItemDefaults.shape(shape = LurchTVCardShape)
            )
        }
    }
}
