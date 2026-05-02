package org.friesoft.lurchtv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.friesoft.lurchtv.presentation.App
import org.friesoft.lurchtv.presentation.theme.LurchTVTheme
import org.friesoft.lurchtv.presentation.utils.isTv
import dagger.hilt.android.AndroidEntryPoint
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.LocalContentColor as TvLocalContentColor
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Surface as MobileSurface

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            LurchTVTheme {
                if (isTv()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(TvMaterialTheme.colorScheme.surface)
                    ) {
                        CompositionLocalProvider(
                            TvLocalContentColor provides TvMaterialTheme.colorScheme.onSurface
                        ) {
                            App(
                                onBackPressed = onBackPressedDispatcher::onBackPressed,
                            )
                        }
                    }
                } else {
                    MobileSurface(
                        modifier = Modifier.fillMaxSize(),
                        color = MobileMaterialTheme.colorScheme.background
                    ) {
                        App(
                            onBackPressed = onBackPressedDispatcher::onBackPressed,
                        )
                    }
                }
            }
        }
    }
}

