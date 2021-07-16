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
package tech.alexib.yaba.android.ui.accounts

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.accounts.detail.AccountDetailScreenParams
import tech.alexib.yaba.android.ui.components.BankLogoSmall
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.ui.components.Money
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.model.AccountType
import tech.alexib.yaba.model.PlaidItemStubs
import tech.alexib.yaba.model.PlaidItemWithAccounts

@Composable
fun AccountsScreen(onSelected: (AccountDetailScreenParams) -> Unit) {
    val viewModel: AccountsScreenViewModel = getViewModel()
    AccountsScreen(viewModel, onSelected)
}

@Composable
private fun AccountsScreen(
    viewModel: AccountsScreenViewModel,
    onSelected: (AccountDetailScreenParams) -> Unit
) {
    val state by
    rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = AccountsScreenState.Empty)

    AccountsScreen(state = state) { action ->
        when (action) {
            is AccountsScreenAction.OnSelected -> onSelected(
                AccountDetailScreenParams(
                    action.accountId,
                    action.itemId
                )
            )
        }
    }
}

@Composable
fun AccountsScreen(
    state: AccountsScreenState,
    actioner: (AccountsScreenAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (state.loading) {
            true -> LoadingScreen()
            false -> AccountsList(state.items) { accountId, itemId ->
                actioner(AccountsScreenAction.OnSelected(accountId, itemId))
            }
        }
    }
}

@Composable
fun AccountsList(itemsWithAccounts: List<PlaidItemWithAccounts>, onSelected: (Uuid, Uuid) -> Unit) {
    Column(Modifier.padding(bottom = 8.dp, top = 20.dp)) {
        Card(
            modifier = Modifier
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(vertical = 8.dp)
                .height(60.dp),
            elevation = 3.dp
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = "Cash balance",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
                Money(
                    amount = itemsWithAccounts.flatMap {
                        it.accounts.filter { account ->
                            account.type == AccountType.DEPOSITORY
                        }
                    }.sumOf { it.currentBalance }
                )
                AddSpace()
            }
        }
        Card(
            modifier = Modifier
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(horizontal = 8.dp),
            elevation = 3.dp,

        ) {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)

            ) {

                itemsWithAccounts.forEach { item ->
                    val logoBitmap = base64ToBitmap(item.base64Logo)
                    items(item.accounts) { account ->

                        Surface(onClick = { onSelected(account.id, account.itemId) }) {
                            AccountItem(
                                balance = account.currentBalance,
                                logo = logoBitmap,
                                label = "${account.name} - ${account.mask}"
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun AccountItem(balance: Double, logo: Bitmap, label: String) {
    ListItem(
        icon = {
            BankLogoSmall(logoBitmap = logo)
        },
        singleLineSecondaryText = true,
        trailing = {
            Money(balance)
        },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2.copy(MaterialTheme.colors.onSurface)
        )
    }
}

@Preview
@Composable
fun PlaidAccountsScreenPreview() {
    val items = PlaidItemStubs.itemsWithAccounts
    AccountsScreen(state = AccountsScreenState(items)) {}
}
