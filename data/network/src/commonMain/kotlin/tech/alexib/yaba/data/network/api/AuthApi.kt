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
package tech.alexib.yaba.data.network.api

import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.network.apollo.YabaApolloClient
import tech.alexib.yaba.data.network.mapper.toAuthResponse
import tech.alexib.yaba.model.User
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput
import tech.alexib.yaba.model.response.AuthResponse
import yaba.schema.LoginMutation
import yaba.schema.RegisterMutation
import yaba.schema.VerifyTokenQuery

interface AuthApi {
    suspend fun login(userLoginInput: UserLoginInput): Flow<DataResult<AuthResponse>>
    suspend fun register(userRegisterInput: UserRegisterInput): Flow<DataResult<AuthResponse>>
    suspend fun verifyToken(): Flow<DataResult<User>>

    class Impl(
        private val client: YabaApolloClient
    ) : AuthApi {

        init {
            ensureNeverFrozen()
        }

        override suspend fun login(
            userLoginInput: UserLoginInput
        ): Flow<DataResult<AuthResponse>> {
            val mutation = LoginMutation(userLoginInput.email, userLoginInput.password)

            return runCatching {
                client.mutate(mutation) {
                    it.login.toAuthResponse()
                }
            }.getOrThrow()
        }

        override suspend fun register(
            userRegisterInput: UserRegisterInput
        ): Flow<DataResult<AuthResponse>> {
            val mutation = RegisterMutation(userRegisterInput.email, userRegisterInput.password)
            return client.mutate(mutation) {
                it.register.toAuthResponse()
            }
        }

        override suspend fun verifyToken(): Flow<DataResult<User>> {
            val query = VerifyTokenQuery()
            return runCatching {
                client.query(query) {
                    val data = it.me
                    User(data.id, data.email)
                }
            }.getOrThrow()
        }
    }
}
