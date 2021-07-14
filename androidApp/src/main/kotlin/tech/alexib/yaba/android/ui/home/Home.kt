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
package tech.alexib.yaba.android.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.R
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.LoadingScreenWithCrossFade
import tech.alexib.yaba.android.ui.components.TransactionItem
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.util.moneyFormat
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.model.Transaction

@Immutable
data class HomeScreenState(
    val loading: Boolean = false,
    val currentCashBalance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList()
) {
    companion object {
        val Empty = HomeScreenState()
    }
}

sealed class HomeScreenAction {
    object NavigateToPlaidLinkScreen : HomeScreenAction()
    object NavigateToTransactionsScreen : HomeScreenAction()
}

@Composable
fun Home(
    navigateToPlaidLinkScreen: () -> Unit,
    navigateToTransactionsScreen: () -> Unit
) {
    val viewModel: HomeViewModel = getViewModel()

    Home(
        viewModel = viewModel,
        navigateToPlaidLinkScreen = navigateToPlaidLinkScreen,
        navigateToTransactionsScreen = navigateToTransactionsScreen
    )
}

@Composable
private fun Home(
    viewModel: HomeViewModel,
    navigateToPlaidLinkScreen: () -> Unit,
    navigateToTransactionsScreen: () -> Unit
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = HomeScreenState.Empty)

    Home(state) { action ->
        when (action) {
            is HomeScreenAction.NavigateToPlaidLinkScreen -> navigateToPlaidLinkScreen()
            is HomeScreenAction.NavigateToTransactionsScreen -> navigateToTransactionsScreen()
        }
    }
}

@Composable
private fun Home(
    state: HomeScreenState,
    actioner: (HomeScreenAction) -> Unit
) {
    LoadingScreenWithCrossFade(state.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AddSpace()

            if (state.currentCashBalance == 0.0) {
                Button(
                    onClick = {
                        actioner(HomeScreenAction.NavigateToPlaidLinkScreen)
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.link_first_institution),
                        style = MaterialTheme.typography.button
                    )
                }
            } else {
                TotalCashBalanceRow(state.currentCashBalance)
                AddSpace()
                RecentTransactions(transactions = state.recentTransactions) {
                    actioner(HomeScreenAction.NavigateToTransactionsScreen)
                }
            }
        }
    }
}

@Composable
fun TotalCashBalanceRow(
    balance: Double
) {
    Card(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically),
        elevation = 3.dp
    ) {
        BalanceRow(
            balance = balance,
            description = stringResource(id = R.string.current_cash_balance),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun BalanceRow(
    balance: Double,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(text = description)
            Text(
                text = "$${moneyFormat.format(balance)}",
                color = if (balance > 0) MoneyGreen else Color.Red,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun RecentTransactions(
    transactions: List<Transaction>,
    onSelectAllTransactions: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(4.dp)
            .clickable {
                onSelectAllTransactions()
            },
        elevation = 3.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
// only pulls 5 transactions
            transactions.forEach {
                Row {
                    TransactionItem(transaction = it) {
                        onSelectAllTransactions()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { onSelectAllTransactions() }) {
                    Text(text = stringResource(id = R.string.view_all_transactions))
                }
            }
        }
    }
}
