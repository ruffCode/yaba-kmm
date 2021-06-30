package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.ui.components.BankLogo
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.android.util.base64ToBitmap
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.kmm.model.PlaidItemStubs
import tech.alexib.yaba.kmm.model.PlaidItemWIthAccounts


sealed class PlaidItemsScreenAction {
    data class OnItemSelected(val item: PlaidItemWIthAccounts) : PlaidItemsScreenAction()
    object NavigateToLinkInstitution : PlaidItemsScreenAction()
}

@Immutable
data class PlaidItemsScreenState(
    val items: List<PlaidItemWIthAccounts> = emptyList(),
) {
    companion object {
        val Empty = PlaidItemsScreenState()
    }
}

@Composable
fun PlaidItemsScreen(
    onItemSelected: (PlaidItemWIthAccounts) -> Unit,
    navigateToPlaidLink: () -> Unit,
) {
    val viewModel: PlaidItemsScreenViewModel = getViewModel()
    PlaidItemsScreen(viewModel, { onItemSelected(it) }) {
        navigateToPlaidLink()
    }
}


@Composable
private fun PlaidItemsScreen(
    viewModel: PlaidItemsScreenViewModel,
    onItemSelected: (PlaidItemWIthAccounts) -> Unit,
    navigateToPlaidLink: () -> Unit,
) {
    Log.d("PlaidItemsScreen", "composed")
    val state by
    rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(initial = PlaidItemsScreenState.Empty)

    PlaidItemsScreen(state = state) { action ->
        when (action) {
            is PlaidItemsScreenAction.OnItemSelected -> onItemSelected(action.item)
            is PlaidItemsScreenAction.NavigateToLinkInstitution -> navigateToPlaidLink()
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun PlaidItemsScreen(
    state: PlaidItemsScreenState,
    actioner: (PlaidItemsScreenAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.items) { plaidItem ->
                ListItem(trailing = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = "${
                                plaidItem.hiddenCount
                            } hidden"
                        )
                    }
                },
                    icon = {
                        BankLogo(logoBitmap = base64ToBitmap(plaidItem.base64Logo))

                    },
                    modifier = Modifier
                        .clickable {
                            actioner(PlaidItemsScreenAction.OnItemSelected(plaidItem))
                        }
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(text = plaidItem.name)
                    }
                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                TextButton(
                    onClick = { actioner(PlaidItemsScreenAction.NavigateToLinkInstitution) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(
                            text = "Link another institution",
                            style = MaterialTheme.typography.button, fontSize = 20.sp
                        )
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun PlaidItemsScreenPreview() {
    YabaTheme {
        PlaidItemsScreen(
            state = PlaidItemsScreenState(
                items = listOf(
                    PlaidItemStubs.itemWithAccounts,
                    PlaidItemStubs.itemWithAccounts
                )
            )
        ) {
        }
    }

}