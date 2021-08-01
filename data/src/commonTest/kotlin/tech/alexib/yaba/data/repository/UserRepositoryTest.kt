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
import tech.alexib.yaba.data.domain.stubs.UserDataStubs
import tech.alexib.yaba.util.suspendTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UserRepositoryTest : BaseRepositoryTest() {
    private val userDao = deps.userDao
    private val authSettings = deps.authSettings
    private val userRepository = deps.userRepository

    @BeforeTest
    fun setup() = suspendTest {
        setupTest()
    }

    @AfterTest
    fun breakdown() = suspendTest {
        cleanup()
    }

    @Test
    fun getsCurrentUser() = suspendTest {
//        authSettings.setUserId(userId)
//        userDao.insert(user)
        userRepository.currentUser().test {
            assertEquals(user, awaitItem())
        }
    }

    @Test
    fun deletesCurrentUser() = suspendTest {
//        authSettings.setUserId(userId)
//        userDao.insert(user)
        userDao.selectById(userId).test {
            assertEquals(user, awaitItem())
            expectNoEvents()
            userRepository.deleteCurrentUser()
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun insertUserData() = suspendTest {
        userDao.deleteById(userId)
        val data = UserDataStubs.userData
        userDao.selectById(userId).test {
            assertEquals(null, awaitItem())
            expectNoEvents()
            userDao.insertUserData(data)
            assertEquals(user, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

//        yabaDb.accountQueries.selectAll(userId, accountMapper).asFlow().mapToList().test {
//            assertTrue(awaitItem().isNotEmpty())
//            cancelAndIgnoreRemainingEvents()
//        }
    }
}
