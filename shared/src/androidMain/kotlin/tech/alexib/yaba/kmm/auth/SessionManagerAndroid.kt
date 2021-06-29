package tech.alexib.yaba.kmm.auth

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.data.repository.AuthApiRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult
import tech.alexib.yaba.kmm.model.response.AuthResponse


class SessionManagerAndroid(
    private val yabaAppSettings: AppSettings,
    private val biometricsManager: BiometricsManager
) : SessionManager, BiometricsManager by biometricsManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("SessionManagerImpl") }
    private val authApiRepository: AuthApiRepository by inject()

    init {
        ensureNeverFrozen()
    }

    override fun isLoggedIn(): Flow<Boolean> = yabaAppSettings.token().map { !it.isNullOrEmpty() }

    override suspend fun setToken(token: String) {
        yabaAppSettings.setToken(token)
    }

    override suspend fun logout() {
        yabaAppSettings.clearAuthToken()
        yabaAppSettings.clearUserId()
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return runCatching {
            handleAuthResponse(authApiRepository.login(email, password).getOrThrow())
        }.getOrElse {
            log.e(it) { "Login Error" }
            AuthResult.Error("Authentication Error")
        }
    }

    override suspend fun register(email: String, password: String): AuthResult {
        return runCatching {
            handleAuthResponse(authApiRepository.register(email, password).getOrThrow())

        }.getOrElse {
            AuthResult.Error(it.message ?: "User Registration Error")
        }
    }


    private suspend fun handleAuthResponse(authResponse: AuthResponse): AuthResult {
        return if (authResponse.token.isNotEmpty()) {
            setToken(authResponse.token)
            setUserId(authResponse.id)
            AuthResult.Success
        } else AuthResult.Error("Authentication Error")
    }

    override fun isShowOnBoarding(): Flow<Boolean> = yabaAppSettings.showOnboarding()
    override fun startLogoutTimer() {
        TODO("Not yet implemented")
    }


    override suspend fun setUserId(userId: Uuid) {
        yabaAppSettings.setUserId(userId)
    }

}