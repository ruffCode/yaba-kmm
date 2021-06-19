package tech.alexib.yaba.kmm.android

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.android.ui.auth.Login
import tech.alexib.yaba.kmm.android.ui.auth.RegistrationScreen
import tech.alexib.yaba.kmm.android.ui.auth.Splash
import tech.alexib.yaba.kmm.android.ui.home.Home
import tech.alexib.yaba.kmm.android.ui.home.SplashScreenViewModel

internal sealed class Route(val route: String) {
    object Auth : Route("auth")
    object Home : Route("home")
}

internal sealed class AuthRoute(val route: String) {
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
    object Registration : AuthRoute("registration")
}


@Composable
fun AppNavigation(
    navController: NavHostController,
    finishActivity: () -> Unit
) {

    val authViewModel = getViewModel<SplashScreenViewModel>() { parametersOf(navController) }

    fun navigateHome() {
        navController.navigate(Route.Home.route) {
            launchSingleTop = true
        }
    }
    NavHost(navController = navController, startDestination = Route.Auth.route) {


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

        composable(Route.Home.route) {
            BackHandler {
                finishActivity()
            }

            Log.d("NAVIGATOR", "navigate home")
            Home()
        }
    }
}