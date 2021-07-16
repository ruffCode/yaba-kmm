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
package tech.alexib.yaba.android.ui.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.alexib.yaba.android.navigation.AuthRoute
import tech.alexib.yaba.android.navigation.Route
import tech.alexib.yaba.data.auth.SessionManagerAndroid

class SplashScreenViewModel(
    private val navHostController: NavHostController,
    private val sessionManager: SessionManagerAndroid
) : ViewModel() {

    private fun isLoggedIn(): Flow<Boolean> =
        sessionManager.isLoggedIn()

    private fun showOnBoarding(): Flow<Boolean> = sessionManager.isShowOnBoarding()

    fun splashScreenNavigation() = viewModelScope.launch {
        delay(500)
        when (isLoggedIn().first()) {
            true -> navHostController.navigate(Route.HomeFeed.route) { launchSingleTop = true }
            false -> if (showOnBoarding().first()) navHostController
                .navigate(AuthRoute.Registration.route)
            else navHostController.navigate(AuthRoute.Login.route) {
                launchSingleTop = true
            }
        }
    }
}
