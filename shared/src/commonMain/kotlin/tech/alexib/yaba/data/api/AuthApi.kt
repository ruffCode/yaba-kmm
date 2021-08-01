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
package tech.alexib.yaba.data.api

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.LoginMutation
import tech.alexib.yaba.RegisterMutation
import tech.alexib.yaba.VerifyTokenQuery
import tech.alexib.yaba.model.User
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput
import tech.alexib.yaba.model.response.AuthResponse

interface AuthApi {
    suspend fun login(userLoginInput: UserLoginInput): Flow<YabaApolloResponse<AuthResponse>>
    suspend fun register(userRegisterInput: UserRegisterInput): Flow<YabaApolloResponse<AuthResponse>>
    suspend fun verifyToken(): Flow<YabaApolloResponse<User>>
}

internal class AuthApiImpl(
    private val apolloApi: ApolloApi
) : AuthApi {

    init {
        ensureNeverFrozen()
    }

    override suspend fun login(
        userLoginInput: UserLoginInput
    ): Flow<YabaApolloResponse<AuthResponse>> {
        val mutation = LoginMutation(userLoginInput.email, userLoginInput.password)

        return runCatching {
            apolloApi.client().safeMutation(mutation) {
                it.login.toAuthResponse()
            }
        }.getOrThrow()
    }

    override suspend fun register(
        userRegisterInput: UserRegisterInput
    ): Flow<YabaApolloResponse<AuthResponse>> {
        val mutation = RegisterMutation(userRegisterInput.email, userRegisterInput.password)
        return runCatching {
            apolloApi.client().safeMutation(mutation) {
                it.register.toAuthResponse()
            }
        }.getOrThrow()
    }

    override suspend fun verifyToken(): Flow<YabaApolloResponse<User>> {
        val query = VerifyTokenQuery()
        return runCatching {
            apolloApi.client().safeQuery(query) {
                val data = it.me
                User(data.id as Uuid, data.email)
            }
        }.getOrThrow()
    }

    private fun LoginMutation.Login.toAuthResponse() = AuthResponse(
        id = id as Uuid,
        email = email,
        token = token
    )

    private fun RegisterMutation.Register.toAuthResponse() = AuthResponse(
        id = id as Uuid,
        email = email,
        token = token
    )
}
