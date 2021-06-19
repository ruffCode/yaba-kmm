package tech.alexib.yaba.kmm.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mu.KotlinLogging
import tech.alexib.yaba.kmm.android.AuthRoute
import tech.alexib.yaba.kmm.android.Route
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository

private val logger = KotlinLogging.logger { }

class SplashScreenViewModel(
    private val navHostController: NavHostController,
    private val authRepository: AndroidAuthRepository
) : ViewModel() {

    private fun isLoggedIn(): Flow<Boolean> =
        authRepository.sessionManager.isLoggedIn()

    private fun showOnBoarding(): Flow<Boolean> = authRepository.sessionManager.isShowOnBoarding()


    fun splashScreenNavigation() = viewModelScope.launch {

        when (isLoggedIn().first()) {

            true -> {
                delay(1000)
                navHostController.navigate(Route.Home.route) { launchSingleTop = true }
            }
            false -> {
                when (showOnBoarding().first()) {
                    true -> {
                        delay(1000)
                        navHostController.navigate(AuthRoute.Registration.route)
                    }
                    else -> {
                        delay(1000)
                        navHostController.navigate(AuthRoute.Login.route) { launchSingleTop = true }
                    }

                }

            }
        }
    }


}
