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
package tech.alexib.yaba.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.statusBarsPadding
import tech.alexib.yaba.android.navigation.AppNavigation
import tech.alexib.yaba.android.navigation.Route
import tech.alexib.yaba.android.navigation.shouldShowBottomBar

@Composable
fun MainAppLayout(
    finishActivity: () -> Unit
) {
    val navController = rememberNavController()
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp
    Scaffold(
        bottomBar = {
            val selectedRoute by navController.currentRouteAsState()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            if (shouldShowBottomBar(navBackStackEntry)) {
                YabaBottomBar(
                    selectedRoute = selectedRoute,
                    onSelected = { selected ->
                        navController.navigate(selected.route) {
                            launchSingleTop = true
                            // restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                // saveState = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        modifier = Modifier
            .width(width.dp)
            .statusBarsPadding()
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            AppNavigation(navController = navController, finishActivity)
        }
    }
}

// ** All credit to Chris Banes
@Stable
@Composable
private fun NavController.currentRouteAsState(): State<Route> {

    val selectedRoute = remember { mutableStateOf<Route>(Route.HomeFeed) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Route.HomeFeed.route } -> {
                    selectedRoute.value = Route.HomeFeed
                }
                destination.hierarchy.any { it.route == Route.Accounts.route } -> {
                    selectedRoute.value = Route.Accounts
                }
                destination.hierarchy.any { it.route == Route.Transactions.route } -> {
                    selectedRoute.value = Route.Transactions
                }
                destination.hierarchy.any { it.route == Route.Settings.route } -> {
                    selectedRoute.value = Route.Settings
                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedRoute
}
