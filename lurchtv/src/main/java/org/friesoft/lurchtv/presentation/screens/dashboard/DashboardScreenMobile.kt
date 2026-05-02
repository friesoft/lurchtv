package org.friesoft.lurchtv.presentation.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.friesoft.lurchtv.data.entities.Video
import org.friesoft.lurchtv.presentation.screens.Screens

@Composable
fun DashboardScreenMobile(
    openVideoDetailsScreen: (videoId: String) -> Unit,
    openVideoPlayer: (Video) -> Unit,
    lastWatchedVideoId: String? = null
) {
    val navController = rememberNavController()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var currentDestination: String? by remember { mutableStateOf(null) }

    val tabs = remember { TopBarTabs + Screens.Profile }

    DisposableEffect(Unit) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route
            destination.route?.let { route ->
                val index = tabs.indexOfFirst { it.invoke() == route }
                if (index != -1) {
                    selectedTabIndex = index
                }
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            val targetRoute = screen()
                            if (currentDestination != targetRoute) {
                                navController.navigate(targetRoute) {
                                    if (screen == tabs[0]) popUpTo(tabs[0].invoke())
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.tabIcon ?: Icons.Default.Person,
                                contentDescription = screen.name
                            )
                        },
                        label = {
                            Text(text = screen.name)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Body(
                openVideoDetailsScreen = openVideoDetailsScreen,
                openVideoPlayer = openVideoPlayer,
                updateTopBarVisibility = { },
                isTopBarVisible = true,
                navController = navController,
                lastWatchedVideoId = lastWatchedVideoId,
                startDestination = Screens.Home()
            )
        }
    }
}
