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
@file:Suppress("TooManyFunctions")
package tech.alexib.yaba.android.navigation

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import logcat.logcat
import org.koin.androidx.compose.getStateViewModel
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
    object Auth : Route("auth")
    object HomeFeed : Route("home")
    object Accounts : Route("accounts")
    object Transactions : Route("transactions")
    object Settings : Route("settings")
    object PlaidLink : Route("plaid")
}

sealed class NestedRoute(val route: String) {
    fun createRoute(root: Route) = "${root.route}/$route"

    object Auth : NestedRoute("auth")
    object HomeFeed : NestedRoute("home")
    object Accounts : NestedRoute("accounts")
    object Transactions : NestedRoute("transactions")
    object Settings : NestedRoute("settings")
    object PlaidLink : NestedRoute("plaid")

    object Login : NestedRoute("login")
    object Registration : NestedRoute("registration")
    object BiometricSetup : NestedRoute("biometricSetup")

    object LinkedInstitutions : NestedRoute("institution")

    object InstitutionDetail : NestedRoute("institutionDetail") {

        private const val key = "plaidItemDetail"

        val arguments = listOf(
            navArgument(key) {
                NavType.ParcelableType(PlaidItemDetail::class.java)
            }
        )

        fun createRoute(
            root: Route,
            item: PlaidItemDetail,
            bundle: Bundle?
        ): String {
            bundle?.putParcelable(key, item)
            return "${root.route}/$route"
        }

        fun getArg(savedStateHandle: SavedStateHandle): PlaidItemDetail =
            savedStateHandle.get<PlaidItemDetail>(
                key
            )!!
    }

    object TransactionDetail : NestedRoute("transaction/{transactionId}") {

        fun createRoute(
            root: Route,
            transactionId: String
        ): String = "${root.route}/transaction/$transactionId"

        private const val key = "transactionId"

        val arguments: List<NamedNavArgument> = listOf(navArgument(key) { NavType.StringType })

        fun getArg(savedStateHandle: SavedStateHandle) = savedStateHandle.getUuid(key)
    }

    object AccountDetail : NestedRoute("account/{accountParam}") {
        fun createRoute(
            root: Route,
            accountParam: AccountDetailScreenParams,
        ): String {
            val accountParamString = jSerializer.encodeToString(accountParam)
            return "${root.route}/account/$accountParamString"
        }

        fun getArg(savedStateHandle: SavedStateHandle): AccountDetailScreenParams =
            savedStateHandle.getSerialized(
                key
            )

        private const val key = "accountParam"
        val arguments = listOf(navArgument(key) { NavType.StringType })
    }

    object PlaidLinkResult : NestedRoute("plaidLinkResult") {
        private const val key = "linkResult"
        val arguments = listOf(
            navArgument(key) {
                NavType.ParcelableType(PlaidLinkScreenResult::class.java)
            }
        )

        fun createRoute(
            root: Route,
            plaidResult: PlaidLinkScreenResult,
            bundle: SavedStateHandle?,
        ): String {
            bundle?.set(key, plaidResult)
            return "${root.route}/$route"
        }
    }
}

fun shouldShowBottomBar(navBackStackEntry: NavBackStackEntry?): Boolean {

    val show = navBackStackEntry?.destination?.hierarchy?.any {
        it.route in listOf(
            Route.HomeFeed.route,
            Route.Settings.route,
            Route.Transactions.route,
            Route.Accounts.route
        )
    } ?: false

    val isPlaid =
        navBackStackEntry?.destination?.route?.contains("plaid", ignoreCase = true) ?: false
    return show && !isPlaid
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    finishActivity: () -> Unit,
) {

    val viewModel = getViewModel<SplashScreenViewModel> { parametersOf(navController) }

    AnimatedNavHost(
        navController = navController,
        startDestination = Route.Auth.route,
        enterTransition = { initial, target -> defaultYabaEnterTransition(initial, target) },
        exitTransition = { initial, target -> defaultYabaExitTransition(initial, target) },
        popEnterTransition = { _, _ -> defaultYabaPopEnterTransition() },
        popExitTransition = { _, _ -> defaultYabaPopExitTransition() }
    ) {

        addHomeFeedRoute(navController, finishActivity)
        addAuthRoute(navController, viewModel, finishActivity)
        addSettingsRoute(navController)
        addAccountsRoute(navController, Route.Accounts)

        addTransactionsRoute(navController)

        addPlaidLinkLauncherRoute(navController)
    }
}

private fun NavGraphBuilder.addHomeFeedRoute(
    navController: NavController,
    finishActivity: () -> Unit
) {
    navigation(
        startDestination = NestedRoute.HomeFeed.createRoute(Route.HomeFeed),
        route = Route.HomeFeed.route
    ) {
        addHome(navController, finishActivity)
    }
}

private fun NavGraphBuilder.addAuthRoute(
    navController: NavController,
    splashScreenViewModel: SplashScreenViewModel,
    finishActivity: () -> Unit
) {
    navigation(
        route = Route.Auth.route,
        startDestination = NestedRoute.Auth.createRoute(Route.Auth)
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
        startDestination = NestedRoute.Settings.createRoute(Route.Settings),
        route = Route.Settings.route
    ) {
        addSettingsMain(navController, Route.Settings)
        addLinkedInstitutions(navController, Route.Settings)
        addInstitutionDetail(navController, Route.Settings)
    }
}

private fun NavGraphBuilder.addPlaidLinkLauncherRoute(navController: NavController) {
    navigation(
        startDestination = NestedRoute.PlaidLink.createRoute(Route.PlaidLink),
        route = Route.PlaidLink.route
    ) {
        addPlaidLinkLauncher(navController, Route.PlaidLink)
        addPlaidLinkResult(navController, Route.PlaidLink)
    }
}

private fun NavGraphBuilder.addTransactionsRoute(navController: NavController) {
    navigation(NestedRoute.Transactions.createRoute(Route.Transactions), Route.Transactions.route) {
        addTransactionsList(navController, Route.Transactions)
        addTransactionDetail(navController, Route.Transactions)
    }
}

// -------------Auth routes
private fun NavGraphBuilder.addSplash(
    splashScreenViewModel: SplashScreenViewModel,
) {
    composable(NestedRoute.Auth.createRoute(Route.Auth)) {
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
    composable(NestedRoute.Login.createRoute(Route.Auth)) {
        BackHandler {
            finishActivity()
        }
        Login(
            {
                navController.navigate(NestedRoute.Registration.createRoute(Route.Auth))
            },
            navigateHome = { navController.navigateHome() }
        ) {
            navController.navigate(NestedRoute.BiometricSetup.createRoute(Route.Auth))
        }
    }
}

private fun NavGraphBuilder.addRegistration(
    navController: NavController,
    finishActivity: () -> Unit
) {
    composable(NestedRoute.Registration.createRoute(Route.Auth)) {
        BackHandler {
            finishActivity()
        }
        RegistrationScreen({ navController.navigate(NestedRoute.Login.createRoute(Route.Auth)) }) {
            navController.navigate(NestedRoute.BiometricSetup.createRoute(Route.Auth))
        }
    }
}

private fun NavGraphBuilder.addBiometricSetup(
    navController: NavController,
) {
    composable(NestedRoute.BiometricSetup.createRoute(Route.Auth)) {
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
    navController: NavController,
    root: Route
) {
    composable(NestedRoute.Settings.createRoute(root)) {
        BackHandler {
            navController.handleBack()
        }
        SettingsScreen { navDestination ->
            when (navDestination) {
                is SettingsScreenAction.NavDestination.Auth -> navController.navigate(
                    Route.Auth.route
                ) {
                    launchSingleTop = true
                }
                is SettingsScreenAction.NavDestination.LinkedInstitutions ->
                    navController.navigate(
                        NestedRoute.LinkedInstitutions.createRoute(Route.Settings)
                    )
            }
        }
    }
}

private fun NavGraphBuilder.addLinkedInstitutions(
    navController: NavController,
    root: Route
) {
    composable(NestedRoute.LinkedInstitutions.createRoute(root)) {
        BackHandler {
            navController.handleBack()
        }
        PlaidItemsScreen(
            onItemSelected = {
                val item = PlaidItemDetail(it)

                navController.navigate(
                    NestedRoute.InstitutionDetail.createRoute(
                        root,
                        item, navController.currentBackStackEntry?.arguments
                    )
                )
            }
        ) {
            navController.navigate(Route.PlaidLink.route) {
                launchSingleTop = true
            }
        }
    }
}

private fun NavGraphBuilder.addInstitutionDetail(navController: NavController, root: Route) {
    composable(
        route = NestedRoute.InstitutionDetail.createRoute(root),
        arguments = NestedRoute.InstitutionDetail.arguments
    ) {
        PlaidItemDetailScreen(
            viewModel = getStateViewModel(state = {
                navController.previousBackStackEntry?.arguments ?: Bundle()
            })
        ) {
            navController.handleBack()
        }
    }
}

private fun NavGraphBuilder.addHome(navController: NavController, finishActivity: () -> Unit) {
    composable(NestedRoute.HomeFeed.createRoute(Route.HomeFeed)) {
        BackHandler {
            finishActivity()
        }
        Home(
            navigateToPlaidLinkScreen = {
                navController.navigate(Route.PlaidLink.route) {
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
private fun NavGraphBuilder.addTransactionsList(navController: NavController, root: Route) {
    composable(NestedRoute.Transactions.createRoute(root)) {
        BackHandler {
            navController.navigateUp()
        }
        TransactionListScreen(onBack = { navController.navigateHome() }) { transactionId: Uuid ->

            navController.navigate(
                NestedRoute.TransactionDetail.createRoute(
                    root,
                    transactionId.toString()
                )
            )
        }
    }
}

private fun NavGraphBuilder.addTransactionDetail(navController: NavController, root: Route) {

    composable(
        route = NestedRoute.TransactionDetail.createRoute(root),
        arguments = NestedRoute.TransactionDetail.arguments
    ) {
        TransactionDetailScreen(navController.currentBackStackEntry?.arguments) {
            navController.handleBack()
        }
    }
}

// ---------- Plaid Link
private fun NavGraphBuilder.addPlaidLinkLauncher(
    navController: NavController,
    root: Route
) {
    composable(route = NestedRoute.PlaidLink.createRoute(root)) {
        BackHandler {
            navController.handleBack()
        }
        logcat { "addPlaidLinkLauncher recomposing" }

        PlaidLinkScreen(handleResult = { plaidItem: PlaidLinkScreenResult ->

            navController.navigate(
                route = NestedRoute.PlaidLinkResult.createRoute(
                    root,
                    plaidItem,
                    navController.currentBackStackEntry?.savedStateHandle
                )
            ) {
                launchSingleTop = true
            }
        }) {
            navController.navigateHome()
        }
    }
}

private fun NavGraphBuilder.addPlaidLinkResult(
    navController: NavController,
    root: Route
) {
    composable(
        route = NestedRoute.PlaidLinkResult.createRoute(root),
    ) {
        BackHandler {
            navController.navigate(Route.HomeFeed.route) {
                launchSingleTop = true
            }
        }
        navController.previousBackStackEntry?.savedStateHandle?.get<PlaidLinkScreenResult>("linkResult")
            ?.let {
                PlaidLinkResultScreen(
                    it
                ) {
                    navController.navigate(Route.HomeFeed.route) {
                        launchSingleTop = true
                    }
                }
            }
    }
}

private fun NavGraphBuilder.addAccountsRoute(
    navController: NavController,
    root: Route
) {
    navigation(
        startDestination = NestedRoute.Accounts.createRoute(root),
        route = Route.Accounts.route
    ) {
        addAccounts(navController, Route.Accounts)
        addAccountDetail(navController, Route.Accounts)
        addTransactionDetail(navController, Route.Accounts)
    }
}

private fun NavGraphBuilder.addAccounts(navController: NavController, root: Route) {
    composable(NestedRoute.Accounts.createRoute(root)) {
        BackHandler {
            navController.handleBack()
        }
        AccountsScreen { params ->
            navController.navigate(NestedRoute.AccountDetail.createRoute(root, params))
        }
    }
}

private fun NavGraphBuilder.addAccountDetail(navController: NavController, root: Route) {

    composable(
        NestedRoute.AccountDetail.createRoute(root),
        arguments = NestedRoute.AccountDetail.arguments
    ) {

        AccountDetailScreen(
            viewModel = getStateViewModel(state = {
                navController.currentBackStackEntry?.arguments ?: Bundle()
            }),
            onBack = { navController.handleBack() }
        ) { transactionId: Uuid? ->
            transactionId?.let {
                navController.navigate(
                    NestedRoute.TransactionDetail.createRoute(
                        root,
                        it.toString()
                    )
                )
            }
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

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultYabaEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph

    val fadeIn = fadeIn(
        animationSpec = TweenSpec(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        initialAlpha = 0.3f
    )
    val slideIn = slideIntoContainer(
        AnimatedContentScope.SlideDirection.Start,
        animationSpec = TweenSpec(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    )
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn
    }
    return fadeIn + slideIn
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultYabaExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph

    val fadeOut = fadeOut(
        animationSpec = TweenSpec(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
    )
    val slideOut = slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Start,
        animationSpec = TweenSpec(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    )
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut
    }
    return fadeOut + slideOut
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultYabaPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End)
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultYabaPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End)
}

private inline fun <reified T> SavedStateHandle.getSerialized(key: String): T =
    jSerializer.decodeFromString(this.get(key)!!)

private fun SavedStateHandle.getUuid(key: String): Uuid = uuidFrom(this.get(key)!!)
