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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.alexib.yaba.android.navigation.NestedRoute
import tech.alexib.yaba.android.navigation.Route
import tech.alexib.yaba.data.repository.AuthRepository

class SplashScreenViewModel(
    private val navController: NavHostController,
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun splashScreenNavigation() = viewModelScope.launch {
// This is here to make sure we get the most recent value
        delay(100)

        when (authRepository.isLoggedIn().first()) {
            true -> navController.navigate(NestedRoute.HomeFeed.createRoute(Route.HomeFeed)) { launchSingleTop = true }
            false -> if (authRepository.isShowOnBoarding().first()) navController
                .navigate(NestedRoute.Registration.createRoute(Route.Auth))
            else navController.navigate(NestedRoute.Login.createRoute(Route.Auth)) {
                launchSingleTop = true
            }
        }
    }
}
