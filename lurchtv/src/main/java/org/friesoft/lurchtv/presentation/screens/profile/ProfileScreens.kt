package org.friesoft.lurchtv.presentation.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

enum class ProfileScreens(
    val icon: ImageVector,
    private val title: String? = null,
) {
    About(Icons.Default.Info),
    Language(Icons.Default.Translate),
    HelpAndSupport(title = "Help and Support", icon = Icons.Default.Support);

    operator fun invoke() = name

    val tabTitle = title ?: name
}
