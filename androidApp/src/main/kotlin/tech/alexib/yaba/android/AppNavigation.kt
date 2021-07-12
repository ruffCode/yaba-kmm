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

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
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
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.android.ui.accounts.AccountsScreen
import tech.alexib.yaba.android.ui.auth.biometric.BiometricSetupScreen
import tech.alexib.yaba.android.ui.auth.login.Login
import tech.alexib.yaba.android.ui.auth.register.RegistrationScreen
import tech.alexib.yaba.android.ui.auth.splash.Splash
import tech.alexib.yaba.android.ui.auth.splash.SplashScreenViewModel
import tech.alexib.yaba.android.ui.home.Home
import tech.alexib.yaba.android.ui.plaid.PlaidLinkResultScreen
import tech.alexib.yaba.android.ui.plaid.PlaidLinkScreen
import tech.alexib.yaba.android.ui.plaid.PlaidLinkScreenResult
import tech.alexib.yaba.android.ui.settings.SettingsScreen
import tech.alexib.yaba.android.ui.settings.SettingsScreenAction
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemDetail
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemDetailScreen
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemsScreen
import tech.alexib.yaba.android.ui.transactions.TransactionDetailScreen
import tech.alexib.yaba.android.ui.transactions.TransactionListScreen

sealed class Route(val route: String) {
    object Auth : Route("auth")
    object Home : Route("home")
    object Settings : Route("settings")
    object PlaidLink : Route("plaid")
    object Transactions : Route("transactions")
    object TransactionDetail : Route("transactionDetail") {
        const val key = "transactionId"
        val argument =
            navArgument(key) { NavType.StringType }
    }

    object Accounts : Route("accounts")
}

sealed class AuthRoute(val route: String) {
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
    object Registration : AuthRoute("registration")
    object BiometricSetup : AuthRoute("biometricSetup")
}

sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("settingsMain")
    object LinkedInstitutions : SettingsRoute("linkedInstitutions")
    object InstitutionDetail : SettingsRoute("institutionDetail")
}

sealed class PlaidLinkRoute(val route: String) {
    object Launcher : PlaidLinkRoute("plaidLinkLauncher")
    object PlaidLinkResult : PlaidLinkRoute("plaidLinkResult")
}

fun shouldShowBottomBar(navBackStackEntry: NavBackStackEntry?): Boolean {
    return navBackStackEntry?.destination?.hierarchy?.any {
        it.route in listOf(
            Route.Home.route,
            SettingsRoute.Main.route,
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
    val authViewModel = getViewModel<SplashScreenViewModel> { parametersOf(navController) }

    NavHost(navController = navController, startDestination = Route.Auth.route) {

        addHome(navController, finishActivity)
        addAuthRoute(navController, authViewModel, finishActivity)
        addSettingsRoute(navController)
        addAccounts(navController)

        addTransactions(navController)
        addTransactionDetail(navController)

        addPlaidLinkLauncherRoute(navController)
    }
}

private fun NavGraphBuilder.addAuthRoute(
    navController: NavController,
    splashScreenViewModel: SplashScreenViewModel,
    finishActivity: () -> Unit
) {
    navigation(
        route = Route.Auth.route,
        startDestination = AuthRoute.Splash.route
    ) {
        addSplash(splashScreenViewModel)
        addLogin(navController, finishActivity)
        addRegistration(navController, finishActivity)
        addBiometricSetup(navController)
    }
}

private fun NavGraphBuilder.addSplash(
    splashScreenViewModel: SplashScreenViewModel
) {
    composable(AuthRoute.Splash.route) {
        Splash(splashScreenViewModel)
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

private fun NavController.handleBack() {
    if (this.currentBackStackEntry != null) {
        this.popBackStack()
    }
}

private fun NavController.navigateHome() {
    val navController = this
    navController.navigate(Route.Home.route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}

private fun NavGraphBuilder.addSettingsRoute(
    navController: NavController
) {
    navigation(
        SettingsRoute.Main.route,
        Route.Settings.route
    ) {
        addSettingsMain(navController)
        addLinkedInstitutions(navController)
        addInstitutionDetail(navController)
    }
}

private fun NavGraphBuilder.addSettingsMain(
    navController: NavController
) {
    composable(SettingsRoute.Main.route) {
        BackHandler {
            navController.handleBack()
        }
        SettingsScreen { navDestination ->
            when (navDestination) {
                is SettingsScreenAction.NavDestination.Auth -> navController.navigate(
                    AuthRoute.Splash.route
                ) {
                    launchSingleTop = true
                }
                is SettingsScreenAction.NavDestination.LinkedInstitutions ->
                    navController.navigate(
                        SettingsRoute.LinkedInstitutions.route
                    )
            }
        }
    }
}

private fun NavGraphBuilder.addLinkedInstitutions(
    navController: NavController
) {
    composable(SettingsRoute.LinkedInstitutions.route) {
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
                navController.navigate(SettingsRoute.InstitutionDetail.route)
            }
        ) {
            navController.navigate(PlaidLinkRoute.Launcher.route) {
                launchSingleTop = true
            }
        }
    }
}

private fun NavGraphBuilder.addInstitutionDetail(navController: NavController) {
    composable(
        SettingsRoute.InstitutionDetail.route,
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
    composable(Route.Home.route) {
        BackHandler {
            finishActivity()
        }
        Home(
            navigateToPlaidLinkScreen = {
                navController.navigate(PlaidLinkRoute.Launcher.route) {
                    launchSingleTop = true
                }
            },
            navigateToTransactionsScreen = {
                navController.navigate(Route.Transactions.route)
            }
        )
    }
}

private fun NavGraphBuilder.addTransactions(navController: NavController) {
    composable(Route.Transactions.route) {
        BackHandler {
            navController.navigateHome()
        }
        TransactionListScreen(onBack = { navController.navigateHome() }) {
            navController.currentBackStackEntry?.arguments?.putString(
                Route.TransactionDetail.key,
                it.toString()
            )
            navController.navigate(Route.TransactionDetail.route)
        }
    }
}

private fun NavGraphBuilder.addTransactionDetail(navController: NavController) {
    composable(
        Route.TransactionDetail.route,
        arguments = listOf(Route.TransactionDetail.argument)
    ) {
        val param: String? =
            navController.previousBackStackEntry?.arguments?.getString(
                Route.TransactionDetail.key
            )
        checkNotNull(param) {
            "transactionId was null"
        }

        TransactionDetailScreen(uuidFrom(param)) {
            navController.handleBack()
        }
    }
}

private fun NavGraphBuilder.addPlaidLinkLauncherRoute(navController: NavController) {
    navigation(PlaidLinkRoute.Launcher.route, Route.PlaidLink.route) {
        addPlaidLinkLauncher(navController)
        addPlaidLinkResult(navController)
    }
}

private fun NavGraphBuilder.addPlaidLinkLauncher(navController: NavController) {
    composable(PlaidLinkRoute.Launcher.route) {
        BackHandler {
            navController.handleBack()
        }
        PlaidLinkScreen({ navController.navigateHome() }) { plaidItem ->

            navController.currentBackStackEntry?.arguments?.putParcelable(
                "plaidItem",
                plaidItem
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
                NavType.ParcelableType(PlaidLinkScreenResult::class.java)
            }
        )
    ) {
        BackHandler {
            navController.navigateHome()
        }
        val result: PlaidLinkScreenResult? =
            navController.previousBackStackEntry?.arguments?.getParcelable<PlaidLinkScreenResult>(
                "plaidItem"
            )
        checkNotNull(result) {
            "PlaidLinkScreenResult was null"
        }

        PlaidLinkResultScreen(result = result) {
            navController.navigateHome()
        }
    }
}

private fun NavGraphBuilder.addAccounts(navController: NavController) {
    composable(Route.Accounts.route) {
        BackHandler {
            navController.navigateHome()
        }
        AccountsScreen()
    }
}
