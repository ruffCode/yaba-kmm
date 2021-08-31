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

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.BankLogo
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.PlaidLinkResultScreenState
import tech.alexib.yaba.data.store.PlaidLinkScreenResult

sealed class PlaidLinkResultScreenAction {
    object Submit : PlaidLinkResultScreenAction()
    data class SetAccountShown(val plaidAccountId: String, val show: Boolean) :
        PlaidLinkResultScreenAction()

    object NavigateHome : PlaidLinkResultScreenAction()
}

@Composable
fun PlaidLinkResultScreen(
    result: PlaidLinkScreenResult,
    navigateHome: () -> Unit
) {
    val viewModel: PlaidLinkResultScreenViewModel = getViewModel()
    viewModel.init(result)

    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = PlaidLinkResultScreenState.Empty)

    PlaidLinkResultScreen(result, state) { action ->
        when (action) {
            is PlaidLinkResultScreenAction.Submit -> viewModel.store.submitAccountsToHide()
            is PlaidLinkResultScreenAction.SetAccountShown -> viewModel.store.setAccountShown(
                action.plaidAccountId,
                action.show
            )
            is PlaidLinkResultScreenAction.NavigateHome -> navigateHome()
        }

    }
}

@Composable
fun PlaidLinkResultScreen(
    result: PlaidLinkScreenResult,
    state: PlaidLinkResultScreenState,
    actioner: (PlaidLinkResultScreenAction) -> Unit
) {
    when {
        state.loading && !state.shouldNavigateHome -> LoadingScreen()
        state.shouldNavigateHome -> actioner(PlaidLinkResultScreenAction.NavigateHome)
        else -> PlaidLinkResultScreen(
            logo = base64ToBitmap(result.logo),
            accounts = state.accounts,
            handleSubmit = { actioner(PlaidLinkResultScreenAction.Submit) },
        ) { plaidAccountsId, show ->
            actioner(PlaidLinkResultScreenAction.SetAccountShown(plaidAccountsId, show))
        }
    }

}


@Composable
private fun PlaidLinkResultScreen(
    logo: Bitmap,
    accounts: List<PlaidLinkScreenResult.Account>,
    handleSubmit: () -> Unit,
    setShowHide: (String, Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 100.dp, horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 50.dp)
        ) {
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = "Select the accounts you'd like to track",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                    )
                }
                AddSpace()
            }
            items(accounts) { item ->
                ListItem(
                    trailing = {
                        Checkbox(
                            checked = item.show,
                            onCheckedChange = { setShowHide(item.plaidAccountId, it) }
                        )
                    },
                    icon = {
                        BankLogo(logoBitmap = logo)
                    }
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = "${item.name}  ****${item.mask}")
                    }
                }
            }
        }
        Button(
            onClick = {
                handleSubmit()
            },
            modifier = Modifier
                .padding(top = 50.dp)

                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "Continue")
        }
    }
}


