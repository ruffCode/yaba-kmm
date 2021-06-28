package tech.alexib.yaba.kmm.android.ui.plaid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import tech.alexib.yaba.kmm.android.BuildConfig
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.kmm.model.request.PlaidLinkEventCreateRequest
import java.util.*

class PlaidLinkViewModel(
    private val plaidItemApi: PlaidItemApi
) : ViewModel() {

    private val resultFlow = MutableStateFlow<PlaidLinkResult>(PlaidLinkResult.Empty)
    val result: StateFlow<PlaidLinkResult> = resultFlow



    fun handleResult(linkResult: LinkResult) {
        when (linkResult) {
            is LinkSuccess -> {
                Log.e("PLAID SUCCESS", linkResult.metadata.toString())
                handleSuccess(linkResult)

            }
            is LinkExit -> {
                if (linkResult.error != null) {

                    if (linkResult.error?.errorMessage == "No result returned.") {
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
//                resultFlow.value = ErrorResult("exit")
            }

        }
    }

    fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit) {

        viewModelScope.launch {

            val token: String? = withContext(Dispatchers.IO) {
                return@withContext plaidItemApi.createLinkToken().first().get()?.linkToken
            }
            token?.let {
                handleToken(linkTokenConfiguration {
                    this.token = it
                    logLevel = if (BuildConfig.DEBUG) LinkLogLevel.VERBOSE else LinkLogLevel.ERROR
                })
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
            Log.d("PLAID",request.toString())

            when (val response = plaidItemApi.createPlaidItem(request).firstOrNull()) {
                is Success -> {
                    Log.d("PLAID", response.data.name)
                    resultFlow.value = PlaidLinkResult.Success(response.data)
                }
                is ErrorResult -> {
                    Log.e("PLAID", response.error)
                    resultFlow.value = PlaidLinkResult.Error(response.error)
                }
                null -> {
//                    resultFlow.value = ErrorResult("Null response")
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