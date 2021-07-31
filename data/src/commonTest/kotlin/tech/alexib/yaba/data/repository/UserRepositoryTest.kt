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
import tech.alexib.yaba.data.domain.stubs.UserDataStub
import tech.alexib.yaba.util.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UserRepositoryTest : BaseRepositoryTest() {

    @Test
    fun getsCurrentUser() = suspendTest {
        authSettings.setUserId(userId)
        userDao.insert(user)
        userRepository.currentUser().test {
            assertEquals(user, expectItem())
        }
    }

    @Test
    fun deletesCurrentUser() = suspendTest {
        authSettings.setUserId(userId)
        userDao.insert(user)
        userDao.selectById(userId).test {
            assertEquals(user, expectItem())
            expectNoEvents()
            userRepository.deleteCurrentUser()
            assertEquals(null, expectItem())
        }
    }

    @Test
    fun insertUserData() = suspendTest {
        val data = UserDataStub.userData
        userDao.selectById(userId).test {
            assertEquals(null, expectItem())
            expectNoEvents()
            userDao.insertUserData(data)
            assertEquals(user, expectItem())
        }
    }
}
