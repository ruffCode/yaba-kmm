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
package tech.alexib.yaba.data.mock.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.domain.stubs.UserDataStubs
import tech.alexib.yaba.data.network.api.AuthApi
import tech.alexib.yaba.model.User
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput
import tech.alexib.yaba.model.response.AuthResponse

internal class AuthApiMock : AuthApi {
    private val stub = UserDataStubs
    private val validLogin = stub.validLogin
    override suspend fun login(userLoginInput: UserLoginInput): Flow<DataResult<AuthResponse>> =
        flow {
            delay(100)
            if (userLoginInput == validLogin) {
                emit(Success(stub.goodAuthResponse))
            } else {
                emit(ErrorResult<AuthResponse>("Invalid Login"))
            }
        }

    override suspend fun register(userRegisterInput: UserRegisterInput): Flow<DataResult<AuthResponse>> =
        flow {
            delay(100)
            if (userRegisterInput.email != validLogin.email) {
                emit(
                    Success(
                        AuthResponse(
                            id = UserDataStubs.Registration.newId,
                            email = userRegisterInput.email,
                            token = "authtoken"
                        )
                    )
                )
            } else {
                emit(ErrorResult<AuthResponse>("User already registered"))
            }
        }

    override suspend fun verifyToken(): Flow<DataResult<User>> = flow {
        delay(100)
        emit(Success(UserDataStubs.user))
    }
}
