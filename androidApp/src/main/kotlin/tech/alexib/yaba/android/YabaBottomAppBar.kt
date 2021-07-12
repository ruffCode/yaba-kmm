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

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun YabaBottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    if (shouldShowBottomBar(navBackStackEntry)) {
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface, modifier = modifier) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                selected = currentRoute?.route == Route.Home.route,
                // label = { Text(text = "Home") },
                onClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
//                        restoreState = true
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Money, contentDescription = null) },
                selected = currentRoute?.route == Route.Accounts.route,
                // label = { Text(text = "Accounts") },
                onClick = {
                    navController.navigate(Route.Accounts.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                        }
                        launchSingleTop = true
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Search, contentDescription = "Transactions") },
                selected = currentRoute?.route == Route.Transactions.route,
                // label = { Text(text = "Transactions") },
                onClick = {
                    navController.navigate(Route.Transactions.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                        }
                        launchSingleTop = true
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Setting") },
                selected = currentRoute?.route == Route.Settings.route,
                // label = { Text(text = "Settings") },
                onClick = {
                    navController.navigate(Route.Settings.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
//                        restoreState = true
                    }
                }
            )
        }
    }
}
