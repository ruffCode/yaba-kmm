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

import app.cash.turbine.test
import tech.alexib.yaba.data.domain.stubs.UserDataDtoStubs
import tech.alexib.yaba.model.response.AuthResult
import tech.alexib.yaba.util.suspendTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class AuthRepositoryTest : BaseRepositoryTest() {

    private val stub = UserDataDtoStubs
    private val authRepository = deps.authRepository
    private val userIdProvider = deps.userIdProvider
    private val goodEmail = stub.validLogin.email
    private val goodPw = stub.validLogin.password

    @BeforeTest
    fun setup() = suspendTest {
        authRepository.logout()
    }
    @Test
    fun authTest() = suspendTest {
        authRepository.isLoggedIn().test {
            assertFalse(awaitItem())
            expectNoEvents()
            val authResult = authRepository.login(goodEmail, goodPw)
            assertTrue(authResult is AuthResult.Success)
            assertTrue(awaitItem())
            expectNoEvents()
            authRepository.logout()
            assertFalse(awaitItem())
            val registerResult = authRepository.register("alexi@test.com", "passwordpass123")
            assertTrue(registerResult is AuthResult.Success)
            assertTrue(awaitItem())
            expectNoEvents()
            assertEquals(UserDataDtoStubs.Registration.newId, userIdProvider.userId.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @AfterTest
    fun breakDown() = suspendTest {
        cleanup()
    }
}
