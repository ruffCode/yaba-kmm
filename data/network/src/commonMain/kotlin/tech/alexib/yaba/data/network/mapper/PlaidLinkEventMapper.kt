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
package tech.alexib.yaba.data.network.mapper

import com.apollographql.apollo3.api.Optional
import tech.alexib.yaba.model.request.PlaidLinkEventCreateRequest
import yaba.schema.CreateLinkEventMutation

internal fun PlaidLinkEventCreateRequest.toMutation(): CreateLinkEventMutation {
    return when (this) {
        is PlaidLinkEventCreateRequest.Exit -> CreateLinkEventMutation(
            type = "LinkExit",
            requestId = Optional.Absent.presentIfNotNull(requestId),
            errorCode = Optional.Absent.presentIfNotNull(errorCode),
            errorType = Optional.Absent.presentIfNotNull(errorType),
            linkSessionId = linkSessionId
        )
        is PlaidLinkEventCreateRequest.Success -> CreateLinkEventMutation(
            type = "LinkSuccess",
            linkSessionId = linkSessionId,
        )
    }
}
