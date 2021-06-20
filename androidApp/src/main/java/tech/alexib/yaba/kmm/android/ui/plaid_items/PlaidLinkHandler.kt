package tech.alexib.yaba.kmm.android.ui.plaid_items

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import com.plaid.link.OpenPlaidLink
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.kmm.model.request.PlaidLinkEventCreateRequest

@OptIn(PlaidActivityResultContract::class)
@Composable
fun PlaidLinkHandler(
    onResult: (LinkResult) -> Unit,
    content: @Composable (ActivityResultLauncher<LinkTokenConfiguration>) -> Unit
) {
    val linkLauncher = rememberLauncherForActivityResult(contract = OpenPlaidLink()) {
        onResult(it)
    }
    content(linkLauncher)

}

//@Composable
//fun PlaidItemLinker(
//    viewModel: PlaidItemsViewModel: PlaidLinkService
//) {
//    PlaidLinkHandler(onResult = { service.handleResult(it) }) { openLink ->
//
//        Button(onClick = {
//            service.linkInstitution { config ->
//                openLink.launch(config)
//            }
//        }) {
//            Text(text = "Link Account")
//        }
//
//    }
//}

interface PlaidLinkService {
    suspend fun handleResult(linkResult: LinkResult)
    suspend fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit)
}

class PlankLinkServiceImpl : PlaidLinkService, KoinComponent {
    private val plaidItemApi: PlaidItemApi by inject()

    override suspend fun handleResult(linkResult: LinkResult) {
        when (linkResult) {
            is LinkSuccess -> {
                Log.e("PLAID SUCCESS", linkResult.metadata.toString())
                handleSuccess(linkResult)
            }
            is LinkExit -> {
                Log.e("PLAID EXIT", linkResult.metadata.toString())
                plaidItemApi.sendLinkEvent(linkResult.toRequest())
            }
        }
    }

    override suspend fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit) {
//        val token = plaidItemApi.createLinkToken().first().get()?.linkToken
//        token?.let {
//            handleToken(linkTokenConfiguration {
//                this.token = it
//            })
//        }
        linkWthContext(handleToken)
    }


    private suspend fun linkWthContext(handleToken: (LinkTokenConfiguration) -> Unit){
        withContext(Dispatchers.IO) {
            val res = plaidItemApi.createLinkToken().first()
            when (res) {
//            null -> Log.d("PLAID CREATE LINK TOKEN RESPONSE","is null")
                is Success -> Log.d("PLAID CREATE LINK TOKEN RESPONS", res.data.linkToken)
                is ErrorResult -> Log.e("PLAID CREATE LINK TOKEN RESPONSE\"", res.error)

            }

            val token: String? = res.getOrThrow()?.linkToken
            Log.d("PLAID CREATE LINK TOKEN RESPONSE", token ?: "no token")
            withContext(Dispatchers.Main) {
                token?.let {
                    handleToken(linkTokenConfiguration {
                        this.token = it
                    })
                }
            }
        }
    }


    private suspend fun handleSuccess(linkSuccess: LinkSuccess) {
        plaidItemApi.sendLinkEvent(linkSuccess.toRequest())
        val request = PlaidItemCreateRequest(
            institutionId = linkSuccess.metadata.institution!!.id,
            publicToken = linkSuccess.publicToken
        )
        plaidItemApi.createPlaidItem(request).collect {
            when (it) {
                is Success -> {
                    Log.d("PLAID", it.data.name)
                }
                is ErrorResult -> {
                    Log.e("PLAID", it.error)
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