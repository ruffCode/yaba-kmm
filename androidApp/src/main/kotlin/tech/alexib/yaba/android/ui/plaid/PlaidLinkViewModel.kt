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
package tech.alexib.yaba.android.ui.plaid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.plaid.link.configuration.LinkLogLevel
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.android.BuildConfig
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.model.request.PlaidLinkEventCreateRequest
import tech.alexib.yaba.model.response.PlaidLinkResult
import tech.alexib.yaba.model.response.plaidLinkResultSuccessStub
import tech.alexib.yaba.util.stateInDefault

class PlaidLinkViewModel(
    private val repository: ItemRepository
) : ViewModel(), KoinComponent {

    private val resultFlow = MutableStateFlow<PlaidLinkResult>(PlaidLinkResult.Empty)
    val result: StateFlow<PlaidLinkResult> =
        resultFlow.stateInDefault(viewModelScope, PlaidLinkResult.Empty)
    private val log: Kermit by inject { parametersOf("PlaidLinkViewModel") }

    //uncomment to debug PlaidLinkResultScreen
//    init {
//        log.d { "PlaidLinkViewModel init" }
//        resultFlow.value = plaidLinkResultSuccessStub
//    }
    fun handleResult(linkResult: LinkResult) {
        when (linkResult) {
            is LinkSuccess -> {
                log.d {
                    """
                    PLAID SUCCESS
                    ${linkResult.metadata}
                """.trimIndent()
                }
                resultFlow.value = PlaidLinkResult.AwaitingResult
                handleSuccess(linkResult)
            }
            is LinkExit -> if (linkResult.error != null) {
                if (linkResult.error?.errorMessage ==
                    "No result returned." &&
                    resultFlow.value !=
                    PlaidLinkResult.AwaitingResult
                ) {
                    resultFlow.value = PlaidLinkResult.Abandoned
                } else {
                    resultFlow.value = PlaidLinkResult.Error(
                        linkResult.error?.errorMessage ?: "UNKNOWN LINK ERROR"
                    )
                }

                repository.sendLinkEvent(linkResult.toRequest())
            } else {
                log.d { "PLAID EXIT  ${linkResult.metadata}" }
            }
        }
    }

    fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit) {
        viewModelScope.launch {
            val token: String? = repository.createLinkToken().first()?.linkToken
            token?.let {
                handleToken(
                    linkTokenConfiguration {
                        this.token = it
                        logLevel =
                            if (BuildConfig.DEBUG) LinkLogLevel.VERBOSE else LinkLogLevel.ERROR
                    }
                )
            }
        }
    }

    private fun handleSuccess(linkSuccess: LinkSuccess) {
        viewModelScope.launch {
            repository.sendLinkEvent(linkSuccess.toRequest())

            val request = PlaidItemCreateRequest(
                institutionId = linkSuccess.metadata.institution!!.id,
                publicToken = linkSuccess.publicToken
            )
            resultFlow.value = repository.createPlaidItem(request).first()

        }
    }

    private fun LinkExit.toRequest() = PlaidLinkEventCreateRequest.Exit(
        requestId = metadata.requestId,
        errorCode = error?.errorCode?.json,
        errorType = error?.errorMessage,
        linkSessionId = metadata.linkSessionId ?: ""
    )

    private fun LinkSuccess.toRequest() = PlaidLinkEventCreateRequest.Success(
        linkSessionId = metadata.linkSessionId
    )
}
