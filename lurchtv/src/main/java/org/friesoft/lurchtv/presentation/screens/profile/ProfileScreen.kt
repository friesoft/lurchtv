package org.friesoft.lurchtv.presentation.screens.profile

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Icon as TvIcon
import androidx.tv.material3.ListItem as TvListItem
import androidx.tv.material3.ListItemDefaults as TvListItemDefaults
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.compose.material3.Icon as MobileIcon
import androidx.compose.material3.ListItem as MobileListItem
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import org.friesoft.lurchtv.R
import org.friesoft.lurchtv.presentation.screens.dashboard.rememberChildPadding
import org.friesoft.lurchtv.presentation.theme.LurchTVTheme
import org.friesoft.lurchtv.presentation.utils.isTv

@Composable
fun ProfileScreen() {
    if (isTv()) {
        ProfileScreenTv()
    } else {
        ProfileScreenMobile()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreenTv(
    @FloatRange(from = 0.0, to = 1.0)
    sidebarWidthFraction: Float = 0.32f
) {
    val childPadding = rememberChildPadding()
    val profileNavController = rememberNavController()

    val backStack by profileNavController.currentBackStackEntryAsState()
    val currentDestination =
        remember(backStack?.destination?.route) { backStack?.destination?.route }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isLeftColumnFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = childPadding.start, vertical = childPadding.top)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = sidebarWidthFraction)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight()
                .onFocusChanged {
                    isLeftColumnFocused = it.hasFocus
                }
                .focusRestorer()
                .focusGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileScreens.entries.forEachIndexed { index, profileScreen ->
                key(index) {
                    TvListItem(
                        trailingContent = {
                            TvIcon(
                                profileScreen.icon,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .padding(start = 4.dp)
                                    .size(20.dp),
                                contentDescription = stringResource(
                                    id = R.string.profile_screen_listItem_icon_content_description,
                                    profileScreen.tabTitle
                                )
                            )
                        },
                        headlineContent = {
                            TvText(
                                text = profileScreen.tabTitle,
                                style = TvMaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        selected = currentDestination == profileScreen.name,
                        onClick = { focusManager.moveFocus(FocusDirection.Right) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (index == 0) Modifier.focusRequester(focusRequester)
                                else Modifier
                            )
                            .onFocusChanged {
                                if (it.isFocused && currentDestination != profileScreen.name) {
                                    profileNavController.navigate(profileScreen()) {
                                        currentDestination?.let { nnCurrentDestination ->
                                            popUpTo(nnCurrentDestination) { inclusive = true }
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            },
                        scale = TvListItemDefaults.scale(focusedScale = 1f),
                        colors = TvListItemDefaults.colors(
                            focusedContainerColor = TvMaterialTheme.colorScheme.inverseSurface,
                            selectedContainerColor = TvMaterialTheme.colorScheme.inverseSurface
                                .copy(alpha = 0.4f),
                            selectedContentColor = TvMaterialTheme.colorScheme.surface,
                        ),
                        shape = TvListItemDefaults.shape(shape = TvMaterialTheme.shapes.extraSmall)
                    )
                }
            }
        }

        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent {
                    if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
                        while (!isLeftColumnFocused) {
                            focusManager.moveFocus(FocusDirection.Left)
                        }
                        return@onPreviewKeyEvent true
                    }
                    false
                },
            navController = profileNavController,
            startDestination = ProfileScreens.About(),
            builder = {
                composable(ProfileScreens.About()) {
                    AboutSection()
                }
                composable(ProfileScreens.Language()) {
                    LanguageSection()
                }
                composable(ProfileScreens.HelpAndSupport()) {
                    HelpAndSupportSection()
                }
            }
        )
    }
}

@Composable
fun ProfileScreenMobile() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        MobileText(
            text = "Profile",
            style = MobileMaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ProfileScreens.entries.forEach { profileScreen ->
            MobileListItem(
                headlineContent = { MobileText(profileScreen.tabTitle) },
                leadingContent = { MobileIcon(profileScreen.icon, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
        }
        
        Spacer(modifier = Modifier.size(24.dp))
        AboutSection()
        Spacer(modifier = Modifier.size(24.dp))
        LanguageSection()
        Spacer(modifier = Modifier.size(24.dp))
        HelpAndSupportSection()
    }
}

@Preview(device = Devices.TV_1080p)
@Composable
fun ProfileScreenPreview() {
    LurchTVTheme {
        Box(modifier = Modifier.background(TvMaterialTheme.colorScheme.surface)) {
            ProfileScreen()
        }
    }
}


