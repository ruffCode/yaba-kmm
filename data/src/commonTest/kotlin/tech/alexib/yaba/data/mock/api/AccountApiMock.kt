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

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.AccountWithTransactionsDto
import tech.alexib.yaba.data.domain.stubs.UserDataStubs
import tech.alexib.yaba.data.network.api.AccountApi

internal class AccountApiMock : AccountApi {
    override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
    }

    override fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>> =
        flow {
            val result = UserDataStubs.accountByIdWithTransactions(id)
            result?.let {
                emit(
                    Success(
                        it
                    )
                )
            } ?: emit(ErrorResult<AccountWithTransactionsDto>("account not found"))
        }

    override fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>> = flow {
        val accounts = UserDataStubs.groupedAccounts.getValue(itemId)
        emit(Success(accounts))
    }
}
