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
package tech.alexib.yaba.model.response

import com.benasher44.uuid.uuidFrom
import tech.alexib.yaba.model.defaultLogoBase64

sealed class PlaidLinkResult {
    data class Success(val data: PlaidItemCreateResponse) : PlaidLinkResult()
    data class Error(val message: String) : PlaidLinkResult()
    object Abandoned : PlaidLinkResult()
    object Empty : PlaidLinkResult()
    object AwaitingResult : PlaidLinkResult()
}

val id1 = uuidFrom("d159f98a-f985-458e-8f60-5ce04cd7dec4")
val plaidLinkResultSuccessStub = PlaidLinkResult.Success(
    PlaidItemCreateResponse(
        id = id1,
        logo = defaultLogoBase64,
        name = "Wells Fargo",
        accounts = listOf(
            PlaidItemCreateResponse.Account(
                mask = "0934",
                name = "Checking 1",
                plaidAccountId = "d429cb36-df8f-4244-8fb5-764b56388272",
            ),
            PlaidItemCreateResponse.Account(
                mask = "8744",
                name = "Checking 2",
                plaidAccountId = "0099e0cc-7619-4362-bf1a-c31499037e7c",
            ),
            PlaidItemCreateResponse.Account(
                mask = "2345",
                name = "Savings 1",
                plaidAccountId = "ebdd811f-38cf-44e9-a419-e50ca191ffb7",
            ),
            PlaidItemCreateResponse.Account(
                mask = "2222",
                name = "Credit Card",
                plaidAccountId = "0099e0cc-7619-4362-bf1a-c31499037e7c",
            )
        )
    )
)
