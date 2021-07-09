package tech.alexib.yaba.kmm.android.ui.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.alexib.yaba.kmm.android.AuthRoute
import tech.alexib.yaba.kmm.android.Route
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid

class SplashScreenViewModel(
    private val navHostController: NavHostController,
    private val sessionManager: SessionManagerAndroid
) : ViewModel() {

    private fun isLoggedIn(): Flow<Boolean> =
        sessionManager.isLoggedIn()

    private fun showOnBoarding(): Flow<Boolean> = sessionManager.isShowOnBoarding()

    fun splashScreenNavigation() = viewModelScope.launch {
        delay(500)
        when (isLoggedIn().first()) {
            true -> navHostController.navigate(Route.Home.route) { launchSingleTop = true }
            false -> if (showOnBoarding().first()) navHostController
                .navigate(AuthRoute.Registration.route)
            else navHostController.navigate(AuthRoute.Login.route) {
                launchSingleTop = true
            }
        }
    }
}
