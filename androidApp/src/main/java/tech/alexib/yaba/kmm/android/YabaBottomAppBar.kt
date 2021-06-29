package tech.alexib.yaba.kmm.android

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
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
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface,modifier = modifier) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                selected = currentRoute?.route == Route.Home.route,
                label = { Text(text = "Home") },
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
                })
//            BottomNavigationItem(icon = { Icon(Icons.Filled.Money, contentDescription = null) },
//                selected = currentRoute == Route.Accounts,
//                label = { Text(text = "Accounts") },
//                onClick = {
//                    navController.navigate(Route.Accounts) {
//                        popUpTo = navController.graph.startDestination
//                        launchSingleTop = true
//                    }
//                })
            BottomNavigationItem(icon = { Icon(Icons.Filled.Settings, contentDescription = "Setting") },
                selected = currentRoute?.route == Route.Settings.route,
                label = { Text(text = "Settings") },
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
                })
        }
    }
}