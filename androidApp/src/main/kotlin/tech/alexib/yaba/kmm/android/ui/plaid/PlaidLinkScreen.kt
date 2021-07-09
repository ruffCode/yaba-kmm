package tech.alexib.yaba.kmm.android.ui.plaid

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import com.benasher44.uuid.Uuid
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.result.LinkResult
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.ui.components.LoadingScreen
import tech.alexib.yaba.kmm.android.ui.components.defaultLogoBase64
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.kmm.model.response.PlaidItemCreateResponse

@Composable
fun PlaidLinkScreen(
    navigateHome: () -> Unit,
    handleResult: (PlaidLinkScreenResult) -> Unit,
) {
    val viewModel: PlaidLinkViewModel = getViewModel()

    PlaidLinkScreen(viewModel, navigateHome = navigateHome, handleResult = handleResult)
}

sealed class PlaidLinkScreenAction {
    object NavigateHome : PlaidLinkScreenAction()
    data class ShowError(val error: String) : PlaidLinkScreenAction()
    data class HandleSuccess(val data: PlaidLinkScreenResult) : PlaidLinkScreenAction()
    data class HandleLinkResult(val data: LinkResult) : PlaidLinkScreenAction()
}

sealed class PlaidLinkResult {
    data class Success(val data: PlaidItemCreateResponse) : PlaidLinkResult()
    data class Error(val message: String) : PlaidLinkResult()
    object Abandoned : PlaidLinkResult()
    object Empty : PlaidLinkResult()
    object AwaitingResult : PlaidLinkResult()
}

@OptIn(PlaidActivityResultContract::class)
@Composable
fun PlaidLinkScreen(
    viewModel: PlaidLinkViewModel,
    navigateHome: () -> Unit,
    handleResult: (PlaidLinkScreenResult) -> Unit,
) {
    val state =
        rememberFlowWithLifecycle(flow = viewModel.result)
            .collectAsState(initial = PlaidLinkResult.Empty)

    PlaidLinkScreen(state = state.value, viewModel) { action ->
        when (action) {
            PlaidLinkScreenAction.NavigateHome -> navigateHome()
            is PlaidLinkScreenAction.ShowError -> {
                Log.e("PlaidLinkScreenAction", action.error)
                navigateHome()
            }

            is PlaidLinkScreenAction.HandleLinkResult -> viewModel.handleResult(action.data)
            is PlaidLinkScreenAction.HandleSuccess -> handleResult(action.data)
        }
    }
}

@Composable
private fun PlaidLinkScreen(
    state: PlaidLinkResult,
    viewModel: PlaidLinkViewModel,
    actioner: (PlaidLinkScreenAction) -> Unit
) {
    Box {
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
                }
            )

            is PlaidLinkResult.Success -> actioner(
                PlaidLinkScreenAction.HandleSuccess(
                    state.data.toPlaidItem()
                )
            )
            is PlaidLinkResult.AwaitingResult -> {
                Log.d("PLAID", "AwaitingResult")

                LoadingScreen()
            }
            is PlaidLinkResult.Error -> actioner(PlaidLinkScreenAction.ShowError(state.message))
            is PlaidLinkResult.Abandoned -> actioner(PlaidLinkScreenAction.NavigateHome)
        }
    }
}

@Immutable
@Parcelize
data class PlaidLinkScreenResult(
    val id: Uuid,
    val name: String,
    val logo: String = defaultLogoBase64,
    val accounts: List<Account>
) : Parcelable {
    @Immutable
    @Parcelize
    data class Account(
        val mask: String,
        val name: String,
        val plaidAccountId: String,
        var show: Boolean = true
    ) : Parcelable
}

private fun PlaidItemCreateResponse.toPlaidItem(): PlaidLinkScreenResult =
    PlaidLinkScreenResult(
        id = id,
        name = name,
        logo = logo,
        accounts = accounts.map { account ->
            PlaidLinkScreenResult.Account(
                mask = account.mask,
                name = account.name,
                plaidAccountId = account.plaidAccountId,
                show = true
            )
        }
    )
