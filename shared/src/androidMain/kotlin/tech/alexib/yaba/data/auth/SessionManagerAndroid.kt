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
package tech.alexib.yaba.data.auth

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.AppSettings
import tech.alexib.yaba.data.repository.AuthApiRepository
import tech.alexib.yaba.data.repository.AuthResult
import tech.alexib.yaba.data.repository.UserRepository
import tech.alexib.yaba.model.response.AuthResponse

class SessionManagerAndroid(
    private val yabaAppSettings: AppSettings,
    private val biometricsManager: BiometricsManager
) : SessionManager, BiometricsManager by biometricsManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("SessionManagerImpl") }
    private val authApiRepository: AuthApiRepository by inject()
    private val userRepository: UserRepository by inject()

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

    @Suppress("EmptyFunctionBlock")
    override fun startLogoutTimer() {
    }

    suspend fun clearAppData() {
        yabaAppSettings.clearAppSettings()
        biometricsManager.clear()
        userRepository.deleteCurrentUser()
    }

    override suspend fun setUserId(userId: Uuid) {
        yabaAppSettings.setUserId(userId)
    }
}
