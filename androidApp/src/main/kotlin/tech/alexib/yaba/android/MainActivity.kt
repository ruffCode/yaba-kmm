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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import tech.alexib.yaba.android.ui.MainAppLayout
import tech.alexib.yaba.android.ui.theme.BlueSlate
import tech.alexib.yaba.android.ui.theme.SystemUiController
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.data.auth.activityForBio

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForBio = this
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                val systemUiController = remember { SystemUiController(window) }
                systemUiController.setSystemBarsColor(BlueSlate)
                YabaTheme {
                    MainAppLayout {
                        finish()
                    }
                }
            }
        }
    }
}
