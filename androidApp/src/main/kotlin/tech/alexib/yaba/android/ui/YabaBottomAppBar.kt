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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation
import tech.alexib.yaba.android.navigation.Route

@Composable
fun YabaBottomBar(
    selectedRoute: Route,
    onSelected: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.97f),
        contentColor = contentColorFor(MaterialTheme.colors.surface),
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
        modifier = modifier,
    ) {
        NavItem(selected = selectedRoute == Route.HomeFeed, Route.HomeFeed) {
            onSelected(Route.HomeFeed)
        }
        NavItem(selected = selectedRoute == Route.Accounts, Route.Accounts) {
            onSelected(Route.Accounts)
        }
        NavItem(selected = selectedRoute == Route.Transactions, Route.Transactions) {
            onSelected(Route.Transactions)
        }
        NavItem(selected = selectedRoute == Route.Settings, Route.Settings) {
            onSelected(Route.Settings)
        }
    }
}

@Composable
private fun YabaBottomNavIcon(selected: Boolean, route: Route) {
    val tint = MaterialTheme.colors.primary

    when (route) {
        is Route.HomeFeed ->
            if (selected) Icon(
                Icons.Filled.Home,
                contentDescription = "Home",
                tint = tint
            ) else
                Icon(Icons.Outlined.Home, "Home")

        is Route.Transactions ->
            if (selected) Icon(
                Icons.Filled.Search,
                contentDescription = "Transactions",
                tint = tint
            ) else
                Icon(Icons.Outlined.Search, "Transactions")

        is Route.Accounts ->
            if (selected) Icon(
                Icons.Filled.AccountBalance,
                contentDescription = "Accounts",
                tint = tint
            ) else
                Icon(Icons.Outlined.AccountBalance, "Accounts")

        is Route.Settings ->
            if (selected) Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = tint
            ) else
                Icon(Icons.Outlined.Settings, "Settings")
        else ->{}
    }
}

@Composable
private fun RowScope.NavItem(
    selected: Boolean,
    route: Route,
    onSelected: (Route) -> Unit
) {
    BottomNavigationItem(
        icon = { YabaBottomNavIcon(selected = selected, route = route) },
        onClick = { onSelected(route) },
        selected = selected
    )
}
