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
package tech.alexib.yaba.android.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navigation
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.android.ui.accounts.AccountsScreen
import tech.alexib.yaba.android.ui.accounts.detail.AccountDetailScreen
import tech.alexib.yaba.android.ui.accounts.detail.AccountDetailScreenParams
import tech.alexib.yaba.android.ui.auth.biometric.BiometricSetupScreen
import tech.alexib.yaba.android.ui.auth.login.Login
import tech.alexib.yaba.android.ui.auth.register.RegistrationScreen
import tech.alexib.yaba.android.ui.auth.splash.SplashScreenViewModel
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.ui.home.Home
import tech.alexib.yaba.android.ui.plaid.PlaidLinkResultScreen
import tech.alexib.yaba.android.ui.plaid.PlaidLinkScreen
import tech.alexib.yaba.android.ui.settings.SettingsScreen
import tech.alexib.yaba.android.ui.settings.SettingsScreenAction
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemDetail
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemDetailScreen
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemsScreen
import tech.alexib.yaba.android.ui.transactions.TransactionDetailScreen
import tech.alexib.yaba.android.ui.transactions.TransactionListScreen
import tech.alexib.yaba.data.store.PlaidLinkScreenResult
import tech.alexib.yaba.util.jSerializer

sealed class Route(val route: String) {
    object HomeFeed : Route("home")
    object Settings : Route("settings")
    object Transactions : Route("transactions")
    object Accounts : Route("accounts")
}

sealed class NestedRoute(val route: String) {

    object HomeFeed : NestedRoute("feedMain")

    object Settings : NestedRoute("settingsMain")
    object LinkedInstitutions : NestedRoute("linkedInstitutions")
    object InstitutionDetail : NestedRoute("institutionDetail")

    object Transactions : NestedRoute("transactionsList")
    object TransactionDetail : NestedRoute("transactionDetail") {
        const val key = "transactionId"
        val argument =
            navArgument(key) { NavType.StringType }
    }

    object Accounts : NestedRoute("accountsList")
    object AccountDetail : NestedRoute("accountDetails") {
        const val key = "accountParam"
        val argument = navArgument(key) { NavType.StringType }
    }
}

sealed class AuthRoute(val route: String) {
    object Auth : AuthRoute("auth")
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
    object Registration : AuthRoute("registration")
    object BiometricSetup : AuthRoute("biometricSetup")
}

sealed class PlaidLinkRoute(val route: String) {
    object PlaidLink : PlaidLinkRoute("plaid")
    object PlaidLauncher : PlaidLinkRoute("plaidLinkLauncher")
    object PlaidLinkResult : PlaidLinkRoute("plaidLinkResult")
}

fun shouldShowBottomBar(navBackStackEntry: NavBackStackEntry?): Boolean {
    return navBackStackEntry?.destination?.hierarchy?.any {
        it.route in listOf(
            Route.HomeFeed.route,
            Route.Settings.route,
            Route.Transactions.route,
            Route.Accounts.route
        )
    } ?: false
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    finishActivity: () -> Unit,
) {

    val viewModel = getViewModel<SplashScreenViewModel> { parametersOf(navController) }

    NavHost(navController = navController, startDestination = AuthRoute.Auth.route) {

        addHomeFeedRoute(navController, finishActivity)
        addAuthRoute(navController, viewModel, finishActivity)
        addSettingsRoute(navController)
        addAccountsRoute(navController)

        addTransactionsRoute(navController)

        addPlaidLinkLauncherRoute(navController)
    }
}

private fun NavGraphBuilder.addHomeFeedRoute(
    navController: NavController,
    finishActivity: () -> Unit
) {
    navigation(NestedRoute.HomeFeed.route, Route.HomeFeed.route) {
        addHome(navController, finishActivity)
    }
}

private fun NavGraphBuilder.addAuthRoute(
    navController: NavController,
    splashScreenViewModel: SplashScreenViewModel,
    finishActivity: () -> Unit
) {
    navigation(
        route = AuthRoute.Auth.route,
        startDestination = AuthRoute.Splash.route
    ) {
        addSplash(splashScreenViewModel)
        addLogin(navController, finishActivity)
        addRegistration(navController, finishActivity)
        addBiometricSetup(navController)
    }
}

private fun NavGraphBuilder.addSettingsRoute(
    navController: NavController
) {
    navigation(
        NestedRoute.Settings.route,
        Route.Settings.route
    ) {
        addSettingsMain(navController)
        addLinkedInstitutions(navController)
        addInstitutionDetail(navController)
    }
}

private fun NavGraphBuilder.addPlaidLinkLauncherRoute(navController: NavController) {
    navigation(PlaidLinkRoute.PlaidLauncher.route, PlaidLinkRoute.PlaidLink.route) {
        addPlaidLinkLauncher(navController)
        addPlaidLinkResult(navController)
    }
}

private fun NavGraphBuilder.addTransactionsRoute(navController: NavController) {
    navigation(NestedRoute.Transactions.route, Route.Transactions.route) {
        addTransactionsList(navController)
        addTransactionDetail(navController)
    }
}

// -------------Auth routes
private fun NavGraphBuilder.addSplash(
    splashScreenViewModel: SplashScreenViewModel,
) {
    composable(AuthRoute.Splash.route) {
        LaunchedEffect(this) {
            splashScreenViewModel.splashScreenNavigation()
        }
        LoadingScreen("\uD83D\uDC3B with us")

    }
}

private fun NavGraphBuilder.addLogin(
    navController: NavController,
    finishActivity: () -> Unit,
) {
    composable(AuthRoute.Login.route) {
        BackHandler {
            finishActivity()
        }
        Login(
            {
                navController.navigate(AuthRoute.Registration.route)
            },
            navigateHome = { navController.navigateHome() }
        ) {
            navController.navigate(AuthRoute.BiometricSetup.route)
        }
    }
}

private fun NavGraphBuilder.addRegistration(
    navController: NavController,
    finishActivity: () -> Unit
) {
    composable(AuthRoute.Registration.route) {
        BackHandler {
            finishActivity()
        }
        RegistrationScreen({ navController.navigate(AuthRoute.Login.route) }) {
            navController.navigate(AuthRoute.BiometricSetup.route)
        }
    }
}

private fun NavGraphBuilder.addBiometricSetup(
    navController: NavController,
) {
    composable(AuthRoute.BiometricSetup.route) {
        BackHandler {
            navController.navigateHome()
        }
        BiometricSetupScreen {
            navController.navigateHome()
        }
    }
}

// ----------------Settings routes

private fun NavGraphBuilder.addSettingsMain(
    navController: NavController
) {
    composable(NestedRoute.Settings.route) {
        BackHandler {
            navController.handleBack()
        }
        SettingsScreen { navDestination ->
            when (navDestination) {
                is SettingsScreenAction.NavDestination.Auth -> navController.navigate(
                    AuthRoute.Auth.route
                ) {
                    launchSingleTop = true
                }
                is SettingsScreenAction.NavDestination.LinkedInstitutions ->
                    navController.navigate(
                        NestedRoute.LinkedInstitutions.route
                    )
            }
        }
    }
}

private fun NavGraphBuilder.addLinkedInstitutions(
    navController: NavController
) {
    composable(NestedRoute.LinkedInstitutions.route) {
        BackHandler {
            navController.handleBack()
        }
        PlaidItemsScreen(
            onItemSelected = {
                val item = PlaidItemDetail(it)

                navController.currentBackStackEntry?.arguments?.putParcelable(
                    "plaidItemDetail",
                    item
                )
                navController.navigate(NestedRoute.InstitutionDetail.route)
            }
        ) {
            navController.navigate(PlaidLinkRoute.PlaidLink.route) {
                launchSingleTop = true
            }
        }
    }
}

private fun NavGraphBuilder.addInstitutionDetail(navController: NavController) {
    composable(
        NestedRoute.InstitutionDetail.route,
        arguments =
        listOf(
            navArgument("itemId") {
                NavType.ParcelableType(PlaidItemDetail::class.java)
            }
        )
    ) {
        val result: PlaidItemDetail? =
            navController.previousBackStackEntry?.arguments?.getParcelable(
                "plaidItemDetail"
            )
        checkNotNull(result) {
            "plaidItemDetail was null"
        }

        PlaidItemDetailScreen(result) {
            navController.handleBack()
        }
    }
}

private fun NavGraphBuilder.addHome(navController: NavController, finishActivity: () -> Unit) {
    composable(NestedRoute.HomeFeed.route) {
        BackHandler {
            finishActivity()
        }
        Home(
            navigateToPlaidLinkScreen = {
                navController.navigate(PlaidLinkRoute.PlaidLink.route) {
                    launchSingleTop = true
                }
            },
            navigateToTransactionsScreen = {
                navController.navigate(Route.Transactions.route)
            }
        )
    }
}

// --------------------Transactions
private fun NavGraphBuilder.addTransactionsList(navController: NavController) {
    composable(NestedRoute.Transactions.route) {
        BackHandler {
            navController.navigateHome()
        }
        TransactionListScreen(onBack = { navController.navigateHome() }) {
            navController.currentBackStackEntry?.arguments?.putString(
                NestedRoute.TransactionDetail.key,
                it.toString()
            )
            navController.navigate(NestedRoute.TransactionDetail.route)
        }
    }
}

private fun NavGraphBuilder.addTransactionDetail(navController: NavController) {
    composable(
        NestedRoute.TransactionDetail.route,
        arguments = listOf(NestedRoute.TransactionDetail.argument)
    ) {
        val param: String? =
            navController.previousBackStackEntry?.arguments?.getString(
                NestedRoute.TransactionDetail.key
            )
        checkNotNull(param) {
            "transactionId was null"
        }
        TransactionDetailScreen(uuidFrom(param)) {
            navController.handleBack()
        }
    }
}

// ---------- Plaid Link
private fun NavGraphBuilder.addPlaidLinkLauncher(navController: NavController) {
    composable(PlaidLinkRoute.PlaidLauncher.route) {
        BackHandler {
            navController.handleBack()
        }
        PlaidLinkScreen({ navController.navigateHome() }) { plaidItem ->

            navController.currentBackStackEntry?.arguments?.putString(
                "plaidItem",
                jSerializer.encodeToString(plaidItem)
            )
            navController.navigate(PlaidLinkRoute.PlaidLinkResult.route)
        }
    }
}

private fun NavGraphBuilder.addPlaidLinkResult(navController: NavController) {
    composable(
        PlaidLinkRoute.PlaidLinkResult.route,
        arguments = listOf(
            navArgument("plaidItem") {
                NavType.StringType
            }
        )
    ) {
        BackHandler {
            navController.navigateHome()
        }
        val result: PlaidLinkScreenResult? =
            navController.previousBackStackEntry?.arguments?.getString(
                "plaidItem"
            )?.let { jSerializer.decodeFromString(it) }

        checkNotNull(result) {
            "PlaidLinkScreenResult was null"
        }

        PlaidLinkResultScreen(result = result) {
            navController.navigateHome()
        }
    }
}

private fun NavGraphBuilder.addAccountsRoute(
    navController: NavController
) {
    navigation(NestedRoute.Accounts.route, Route.Accounts.route) {
        addAccounts(navController)
        addAccountDetail(navController)
        addTransactionDetail(navController)
    }
}

private fun NavGraphBuilder.addAccounts(navController: NavController) {
    composable(NestedRoute.Accounts.route) {
        BackHandler {
            navController.handleBack()
        }
        AccountsScreen {
            navController.currentBackStackEntry?.arguments?.putString(
                NestedRoute.AccountDetail.key,
                jSerializer.encodeToString(it)
            )
            navController.navigate(NestedRoute.AccountDetail.route)
        }
    }
}

private fun NavGraphBuilder.addAccountDetail(navController: NavController) {
    composable(
        NestedRoute.AccountDetail.route,
        arguments = listOf(NestedRoute.AccountDetail.argument)
    ) {
        val paramString = navController.previousBackStackEntry?.arguments
            ?.getString(NestedRoute.AccountDetail.key)

        checkNotNull(paramString) {
            "AccountDetailParams required"
        }
        val params: AccountDetailScreenParams = jSerializer.decodeFromString(paramString)

        AccountDetailScreen(params = params, onBack = { navController.handleBack() }) {
            navController.currentBackStackEntry?.arguments?.putString(
                NestedRoute.TransactionDetail.key,
                it.toString()
            )
            navController.navigate(NestedRoute.TransactionDetail.route)
        }
    }
}

private fun NavController.handleBack() {
    if (this.currentBackStackEntry != null) {
        this.popBackStack()
    }
}

private fun NavController.navigateHome() {
    val navController = this
    navController.navigate(Route.HomeFeed.route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}
