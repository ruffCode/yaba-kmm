package tech.alexib.yaba.kmm.android

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.android.ui.auth.Splash
import tech.alexib.yaba.kmm.android.ui.auth.login.Login
import tech.alexib.yaba.kmm.android.ui.auth.register.RegistrationScreen
import tech.alexib.yaba.kmm.android.ui.home.Home
import tech.alexib.yaba.kmm.android.ui.home.SplashScreenViewModel
import tech.alexib.yaba.kmm.android.ui.settings.SettingsScreen
import tech.alexib.yaba.kmm.android.ui.settings.SettingsScreenAction

sealed class Route(val route: String) {
    object Auth : Route("auth")
    object Home : Route("home")
    object Settings : Route("settings")
}

sealed class AuthRoute(val route: String) {
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
    object Registration : AuthRoute("registration")
}


sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("settingsMain")
}


fun shouldShowBottomBar(navBackStackEntry: NavBackStackEntry?): Boolean {
    return navBackStackEntry?.destination?.hierarchy?.any {
        it.route in listOf(
            Route.Home.route,
            SettingsRoute.Main.route
        )
    } ?: false

}

@Composable
fun AppNavigation(
    navController: NavHostController,
    finishActivity: () -> Unit
) {

    val authViewModel = getViewModel<SplashScreenViewModel>() { parametersOf(navController) }

    NavHost(navController = navController, startDestination = Route.Auth.route) {
        fun navigateHome() {
            navController.navigate(Route.Home.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }

        fun handleBack() {
            if (navController.currentBackStackEntry != null) {
                navController.popBackStack()
            }
        }
        navigation(route = Route.Auth.route, startDestination = AuthRoute.Splash.route) {
            composable(AuthRoute.Splash.route) {
                Splash(authViewModel)
            }
            composable(AuthRoute.Login.route) {
                BackHandler {
                    finishActivity()
                }
                Login({

                }) {
                    navigateHome()
                }
            }
            composable(AuthRoute.Registration.route) {
                BackHandler {
                    finishActivity()
                }
                RegistrationScreen {
                    navigateHome()
                }
            }
        }

        navigation(SettingsRoute.Main.route, Route.Settings.route) {
            composable(SettingsRoute.Main.route) {
                BackHandler {
                   handleBack()
                }
                SettingsScreen { navDestination ->
                    when (navDestination) {
                        is SettingsScreenAction.NavDestination.Auth -> navController.navigate(
                            AuthRoute.Splash.route
                        ) {
                            launchSingleTop = true
                        }
                    }

                }
            }
        }

        composable(Route.Home.route) {
            BackHandler {
                finishActivity()
            }

            Log.d("NAVIGATOR", "navigate home")
            Home()
        }
    }
}