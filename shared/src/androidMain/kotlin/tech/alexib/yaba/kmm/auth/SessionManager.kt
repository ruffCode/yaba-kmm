package tech.alexib.yaba.kmm.auth

import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

import tech.alexib.yaba.kmm.YabaAppSettings
import tech.alexib.yaba.kmm.data.auth.SessionManager

class SessionManagerImpl(
    private val yabaAppSettings: YabaAppSettings
) : SessionManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("SessionManagerImpl") }

    override fun isLoggedIn(): Flow<Boolean> = yabaAppSettings.authToken.map { !it.isNullOrEmpty() }

    override suspend fun setToken(token: String) {
        log.d { "setting token $token" }
        yabaAppSettings.setToken(token)
    }

    override suspend fun logout() {
        yabaAppSettings.clearAuthToken()
    }

    override suspend fun getToken(): String? = yabaAppSettings.token().firstOrNull()

    override fun isBioEnabled(): Flow<Boolean> = yabaAppSettings.isBioEnabled()

    override suspend fun enableBio() {
        yabaAppSettings.setBioEnabled(true)
    }

    override fun isShowOnBoarding(): Flow<Boolean> = yabaAppSettings.showOnBoarding
    override fun startLogoutTimer() {
        TODO("Not yet implemented")
    }
}