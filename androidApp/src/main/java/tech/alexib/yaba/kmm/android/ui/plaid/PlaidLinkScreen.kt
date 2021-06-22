package tech.alexib.yaba.kmm.android.ui.plaid

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.result.LinkResult
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.ui.defaultLogoBase64
import tech.alexib.yaba.kmm.android.ui.plaid_items.PlaidLinkHandler
import tech.alexib.yaba.kmm.model.response.PlaidItemCreateResponse


@Composable
fun PlaidLinkScreen(
    navigateHome: () -> Unit,
    handleResult: (PlaidItem) -> Unit,

    ) {

    val viewModel: PlaidLinkViewModel = getViewModel()
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Button(onClick = { handleResult(plaidItemStub) }) {
//            Text(text = "send")
//        }
//    }

    PlaidLinkScreen(viewModel, navigateHome = navigateHome, handleResult = handleResult)
}


//@Composable
//fun PlaidLinkScreen(
//    handleBack: () -> Unit
//) {
//    val viewModel: PlaidLinkViewModel = getViewModel()
//
//    PlaidLinkScreen(viewModel = viewModel)
//
//}


sealed class PlaidLinkScreenAction {
    object NavigateHome : PlaidLinkScreenAction()
    data class ShowError(val error: String) : PlaidLinkScreenAction()
    data class HandleSuccess(val data: PlaidItem) : PlaidLinkScreenAction()
    data class HandleLinkResult(val data: LinkResult) : PlaidLinkScreenAction()

}


sealed class PlaidLinkResult {
    data class Success(val data: PlaidItemCreateResponse) : PlaidLinkResult()
    data class Error(val message: String) : PlaidLinkResult()
    object Abandoned : PlaidLinkResult()
    object Empty : PlaidLinkResult()
}


@OptIn(PlaidActivityResultContract::class)
@Composable
fun PlaidLinkScreen(
    viewModel: PlaidLinkViewModel,
    navigateHome: () -> Unit,
    handleResult: (PlaidItem) -> Unit,
) {

    val state: State<PlaidLinkResult> = viewModel.result.collectAsState()


    PlaidLinkScreen(state = state.value, viewModel) { action ->
        when (action) {
            PlaidLinkScreenAction.NavigateHome ->  navigateHome()
            is PlaidLinkScreenAction.ShowError -> {
                Log.e("PlaidLinkScreenAction", action.error)
                navigateHome()
            }

            is PlaidLinkScreenAction.HandleLinkResult ->  viewModel.handleResult(action.data)
            is PlaidLinkScreenAction.HandleSuccess ->  handleResult(action.data)
        }

    }
}

@Composable
private fun PlaidLinkScreen(
    state: PlaidLinkResult,
    viewModel: PlaidLinkViewModel,
    actioner: (PlaidLinkScreenAction) -> Unit
) {

    if(state is PlaidLinkResult.Success) {
        actioner(PlaidLinkScreenAction.HandleSuccess(state.data.toPlaidItem()))
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            PlaidLinkResult.Empty -> PlaidLinkHandler(
                onResult = { linkResult ->
                    actioner(
                        PlaidLinkScreenAction.HandleLinkResult(
                            linkResult
                        )
                    )
                },
                content = { linkLauncher ->
                    viewModel.linkInstitution { config ->
                        linkLauncher.launch(config)
                    }
                })

            is PlaidLinkResult.Success -> actioner(PlaidLinkScreenAction.HandleSuccess(state.data.toPlaidItem()))

            is PlaidLinkResult.Error -> actioner(PlaidLinkScreenAction.ShowError(state.message))
            is PlaidLinkResult.Abandoned -> actioner(PlaidLinkScreenAction.NavigateHome)
        }
    }
}


@Parcelize
data class PlaidItem(
    val id: Uuid,
    val name: String,
    val logo: String = defaultLogoBase64,
    val accounts: List<Account>
) : Parcelable {
    @Parcelize
    data class Account(
        val mask: String,
        val name: String,
        val plaidAccountId: String,
        var show: Boolean = true
    ) : Parcelable
}

private fun PlaidItemCreateResponse.toPlaidItem(): PlaidItem =
    PlaidItem(
        id = id,
        name = name,
        logo = logo,
        accounts = accounts.map { account ->
            PlaidItem.Account(
                mask = account.mask,
                name = account.name,
                plaidAccountId = account.plaidAccountId,
                show = true
            )
        }
    )
