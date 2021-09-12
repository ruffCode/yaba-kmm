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
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidFrom
import org.junit.After
import org.junit.Before
import org.junit.Test
import tech.alexib.yaba.util.suspendTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class TransactionRepositoryTest : BaseRepositoryTest() {
    val log = deps.kermit
    private val transactionRepository = deps.transactionRepository

    @Before
    fun setUp() = suspendTest {
        setupTest()
    }

    @After
    fun tearDown() {
        log.d { "tearDown" }
    }

    @Test
    fun recentTransactions() = suspendTest {
        transactionRepository.recentTransactions().test {
            assertTrue(awaitItem().isEmpty())
            insertAllUserData()
            assertEquals(awaitItem().size, 5)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAll() = suspendTest {
        transactionRepository.getAll().test {
            assertEquals(80, awaitItem().size)
            expectNoEvents()
        }
    }

    @Test
    fun getById() = suspendTest {
        transactionRepository.getById(uuidFrom("1889ae96-3c6d-49c1-96e4-c5939be87873")).test {
            assertEquals(12.17, awaitItem()?.amount,)
            expectNoEvents()
        }
    }

    @Test
    fun getByNonExistingId() = suspendTest {
        transactionRepository.getById(uuid4()).test {
            assertNull(awaitItem()?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllByAccountId() = suspendTest {
        transactionRepository.getAllByAccountId(uuidFrom("f4dd6752-dc3c-4695-8bb6-afdd0f121cd2"))
            .test {
                assertEquals(80, awaitItem().size)
                expectNoEvents()
            }
    }
}
