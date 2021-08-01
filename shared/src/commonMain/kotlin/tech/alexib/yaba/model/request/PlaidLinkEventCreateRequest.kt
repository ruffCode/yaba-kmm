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
package tech.alexib.yaba.model.request

import tech.alexib.yaba.CreateLinkEventMutation

sealed class PlaidLinkEventCreateRequest {
    data class Exit(
        val requestId: String? = null,
        val errorCode: String? = null,
        val errorType: String? = null,
        val linkSessionId: String = "",
    ) : PlaidLinkEventCreateRequest()

    data class Success(
        val linkSessionId: String,
        val type: String = "LinkSuccess"
    ) : PlaidLinkEventCreateRequest()

    fun toMutation(): CreateLinkEventMutation {
        return when (this) {
            is Exit -> CreateLinkEventMutation(
                type = "LinkExit",
                requestId = requestId,
                errorCode = errorCode,
                errorType = errorType,
                linkSessionId = linkSessionId
            )
            is Success -> CreateLinkEventMutation(
                type = "LinkSuccess",
                linkSessionId = linkSessionId,
                errorCode = null,
                errorType = null,
                requestId = null
            )
        }
    }
}
