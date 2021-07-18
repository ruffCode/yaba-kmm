/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Kermit
import com.google.accompanist.insets.ProvideWindowInsets
import io.sentry.Sentry
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import tech.alexib.yaba.android.ui.MainAppLayout
import tech.alexib.yaba.android.ui.theme.SysDark
import tech.alexib.yaba.android.ui.theme.SysLight
import tech.alexib.yaba.android.ui.theme.SystemUiController
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.data.auth.activityForBio
import tech.alexib.yaba.data.settings.AppSettings
import tech.alexib.yaba.data.settings.Theme

@JvmInline
value class IsSandbox(val value: Boolean)

val LocalIsSandBoxProvider = staticCompositionLocalOf<IsSandbox>() {
    error("IsSandbox not provided")
}

class MainActivity : AppCompatActivity(), KoinComponent {

    private val appSettings: AppSettings by inject()
    private val log: Kermit by inject { parametersOf("MainActivity") }
    private val isSandBox: Boolean by inject(named("isSandbox"))

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForBio = this
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // val layout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        //
        // if (layout == Configuration.SCREENLAYOUT_SIZE_SMALL || layout ==
        //     Configuration.SCREENLAYOUT_SIZE_NORMAL
        // ) {
        //     requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // }
        setContent {
            val themeState = appSettings.theme().collectAsState(initial = Theme.SYSTEM)
            val useDarkTheme = when (themeState.value) {
                Theme.DARK -> true
                Theme.LIGHT -> false
                else -> isSystemInDarkTheme()
            }
            CompositionLocalProvider(
                LocalIsSandBoxProvider provides IsSandbox(isSandBox)
            ) {
                ProvideWindowInsets(consumeWindowInsets = false) {
                    val systemUiController = remember { SystemUiController(window) }
                    if (!useDarkTheme) {
                        systemUiController.setSystemBarsColor(SysLight)
                    } else {
                        systemUiController.setSystemBarsColor(SysDark)
                    }
                    YabaTheme(useDarkTheme) {
                        MainAppLayout {
                            finish()
                        }
                    }
                }
            }
        }
    }
}
