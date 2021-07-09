package tech.alexib.yaba.kmm.model.request

import com.apollographql.apollo.api.Input
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
                requestId = Input.optional(requestId),
                errorCode = Input.optional(errorCode),
                errorType = Input.optional(errorType),
                linkSessionId = linkSessionId
            )
            is Success -> CreateLinkEventMutation(
                type = "LinkSuccess",
                linkSessionId = linkSessionId
            )
        }
    }
}
