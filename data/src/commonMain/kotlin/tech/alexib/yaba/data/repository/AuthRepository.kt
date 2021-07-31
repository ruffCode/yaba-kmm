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
package tech.alexib.yaba.data.repository

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import tech.alexib.yaba.data.network.api.AuthApi
import tech.alexib.yaba.data.settings.AuthSettings
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput
import tech.alexib.yaba.model.response.AuthResponse
import tech.alexib.yaba.model.response.AuthResult

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun logout()
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun startLogoutTimer()
    fun isShowOnBoarding(): Flow<Boolean>
}

internal class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authSettings: AuthSettings,
    private val log: Kermit
) : AuthRepository {
    override fun isLoggedIn(): Flow<Boolean> = authSettings.token().map { !it.isNullOrEmpty() }

    override suspend fun logout() {
        authSettings.clearAuthToken()
        authSettings.clearUserId()
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return runCatching {
            handleAuthResponse(authApi.login(UserLoginInput(email, password)).first().getOrThrow())
        }.getOrElse {
            log.e(it) { "Login Error" }
            AuthResult.Error("Authentication Error")
        }
    }

    override suspend fun register(email: String, password: String): AuthResult {
        return runCatching {
            handleAuthResponse(
                authApi.register(UserRegisterInput(email, password)).first().getOrThrow()
            )
        }.getOrElse {
            AuthResult.Error(it.message ?: "User Registration Error")
        }
    }

    override fun startLogoutTimer() {
    }

    override fun isShowOnBoarding(): Flow<Boolean> = authSettings.showOnboarding()

    private suspend fun setToken(token: String) {
        authSettings.setToken(token)
    }

    private suspend fun setUserId(userId: Uuid) {
        authSettings.setUserId(userId)
    }

    private suspend fun handleAuthResponse(authResponse: AuthResponse): AuthResult {
        return if (authResponse.token.isNotEmpty()) {
            setToken(authResponse.token)
            setUserId(authResponse.id)
            AuthResult.Success
        } else AuthResult.Error("Authentication Error")
    }
}
