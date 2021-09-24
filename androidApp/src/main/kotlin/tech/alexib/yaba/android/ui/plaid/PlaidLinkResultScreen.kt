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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.BankLogo
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.ui.components.LoadingScreenWithCrossFade
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.PlaidLinkResultScreenState
import tech.alexib.yaba.data.store.PlaidLinkResultStore.Action
import tech.alexib.yaba.data.store.PlaidLinkScreenResult


@Composable
fun PlaidLinkResultScreen(
    plaidLinkResult: PlaidLinkScreenResult,
    viewModel: PlaidLinkResultScreenViewModel = getViewModel(),
    navigateHome: () -> Unit
) {

    val logo = remember { base64ToBitmap(plaidLinkResult.logo) }

    LaunchedEffect(plaidLinkResult.id) {
        viewModel.init(plaidLinkResult.accounts, plaidLinkResult.id)
    }

    PlaidLinkResultScreen(
        logo = logo,
        viewModel = viewModel
    ) {
        navigateHome()
    }
}

@Composable
private fun PlaidLinkResultScreen(
    logo: Bitmap,
    viewModel: PlaidLinkResultScreenViewModel,
    navigateHome: () -> Unit
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = PlaidLinkResultScreenState.Empty)

    SideEffect {
        if (state.shouldNavigateHome) {
            navigateHome()
        }
    }
    PlaidLinkResultScreen(logo, state) { action ->
        viewModel.submit(action)
    }
}

@Composable
private fun PlaidLinkResultScreen(
    logo: Bitmap,
    state: PlaidLinkResultScreenState,
    actioner: (Action) -> Unit
) {

    LoadingScreenWithCrossFade(loadingState = state.loading) {
        PlaidLinkResultScreen(
            logo = logo,
            accounts = state.accounts,
            handleSubmit = { actioner(Action.Submit) },
        ) { plaidAccountsId, show ->
            actioner(Action.SetAccountShown(plaidAccountsId, show))
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
                AccountItem(logo, item, setShowHide)
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


@Composable
private fun AccountItem(
    logo: Bitmap,
    item: PlaidLinkScreenResult.Account,
    setShowHide: (String, Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(item.show) }
    ListItem(
        trailing = {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    setShowHide(item.plaidAccountId, it)
                }
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
