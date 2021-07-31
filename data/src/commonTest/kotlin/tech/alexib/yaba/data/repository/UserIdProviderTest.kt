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
import com.benasher44.uuid.uuidFrom
import tech.alexib.yaba.util.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UserIdProviderTest : BaseRepositoryTest() {

    @Test
    fun providesUserId() = runBlockingTest {
        userIdProvider.userId.test {
            assertEquals(uuidFrom("aa1e60bd-0a48-4e8d-800e-237f42d4a793"), expectItem())
            expectNoEvents()
            authSettings.setUserId(userId)
            assertEquals(userId, expectItem())
            expectNoEvents()
        }
    }
}
