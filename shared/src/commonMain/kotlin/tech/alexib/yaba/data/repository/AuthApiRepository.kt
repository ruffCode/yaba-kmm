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
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.first
import tech.alexib.yaba.data.api.ApolloResponse
import tech.alexib.yaba.data.api.AuthApi
import tech.alexib.yaba.model.User
import tech.alexib.yaba.model.response.AuthResponse
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthApiRepository {
    suspend fun login(email: String, password: String): DataResult<AuthResponse>
    suspend fun register(email: String, password: String): DataResult<AuthResponse>
    suspend fun verifyToken(): DataResult<User>
}

class AuthApiRepositoryImpl(
    private val authApi: AuthApi,
    log: Kermit
) : AuthApiRepository {

    init {
        ensureNeverFrozen()
    }

    private val log = log

    override suspend fun login(email: String, password: String): DataResult<AuthResponse> {
        val input = UserLoginInput(email, password)
        return when (val result = authApi.login(input).first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { it }
                }
                ErrorResult(result.errors.first())
            }
        }
    }

    override suspend fun register(email: String, password: String): DataResult<AuthResponse> {
        val input = UserRegisterInput(email, password)
        return when (val result = authApi.register(input).first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { it }
                }
                ErrorResult(result.errors.first())
            }
        }
    }

    override suspend fun verifyToken(): DataResult<User> {
        return when (val result = authApi.verifyToken().first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { "verify token error $it" }
                }
                ErrorResult(result.message)
            }
        }
    }
}
