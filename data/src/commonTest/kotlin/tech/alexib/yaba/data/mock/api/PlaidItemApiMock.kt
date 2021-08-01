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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.domain.dto.NewItemDto
import tech.alexib.yaba.data.domain.stubs.UserDataStubs
import tech.alexib.yaba.data.network.api.PlaidItemApi
import tech.alexib.yaba.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.model.request.PlaidLinkEventCreateRequest
import tech.alexib.yaba.model.response.CreateLinkTokenResponse
import tech.alexib.yaba.model.response.PlaidItemCreateResponse

internal class PlaidItemApiMock : PlaidItemApi {

    private val stub = UserDataStubs.PlaidLink
    override fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>> = flow {
        emit(Success(CreateLinkTokenResponse("linkToken")))
    }

    override fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<PlaidItemCreateResponse>> =
        flow {
            delay(100)
            emit(Success(stub.itemCreateResponse()))
        }

    override fun sendLinkEvent(request: PlaidLinkEventCreateRequest) {
    }

    override fun setAccountsToHide(itemId: Uuid, plaidAccountIds: List<String>) {
    }

    override fun fetchNewItemData(itemId: Uuid): Flow<DataResult<NewItemDto>> = flow {
        emit(Success(UserDataStubs.newItemDtoStub))
    }

    override suspend fun unlink(itemId: Uuid) {
    }
}
