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
package tech.alexib.yaba.kmm.android.ui.plaid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.plaid.link.configuration.LinkLogLevel
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.android.BuildConfig
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.kmm.model.request.PlaidLinkEventCreateRequest

class PlaidLinkViewModel(
    private val plaidItemApi: PlaidItemApi
) : ViewModel(), KoinComponent {

    private val resultFlow = MutableStateFlow<PlaidLinkResult>(PlaidLinkResult.Empty)
    val result: StateFlow<PlaidLinkResult> = resultFlow
    private val log: Kermit by inject { parametersOf("PlaidLinkViewModel") }

    fun handleResult(linkResult: LinkResult) {
        when (linkResult) {
            is LinkSuccess -> {
                Log.d("PLAID SUCCESS", linkResult.metadata.toString())
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

                plaidItemApi.sendLinkEvent(linkResult.toRequest())
            } else {
                Log.d("PLAID EXIT", linkResult.metadata.toString())
            }
        }
    }

    fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit) {
        viewModelScope.launch {
            val token: String? = withContext(Dispatchers.IO) {
                return@withContext plaidItemApi.createLinkToken().first().get()?.linkToken
            }
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
            plaidItemApi.sendLinkEvent(linkSuccess.toRequest())

            val request = PlaidItemCreateRequest(
                institutionId = linkSuccess.metadata.institution!!.id,
                publicToken = linkSuccess.publicToken
            )

            when (val response = plaidItemApi.createPlaidItem(request).firstOrNull()) {
                is Success -> resultFlow.value = PlaidLinkResult.Success(response.data)
                is ErrorResult -> {
                    log.e { "Plaid response error ${response.error}" }
                    resultFlow.value = PlaidLinkResult.Error(response.error)
                }
                null -> {
                }
            }
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
