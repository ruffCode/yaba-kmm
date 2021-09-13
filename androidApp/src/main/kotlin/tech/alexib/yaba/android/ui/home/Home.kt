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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.R
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.LoadingScreenWithCrossFade
import tech.alexib.yaba.android.ui.components.SlideInContent
import tech.alexib.yaba.android.ui.components.SpendingWidget
import tech.alexib.yaba.android.ui.components.TotalCashBalanceRow
import tech.alexib.yaba.android.ui.components.TransactionItem
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.HomeScreenAction
import tech.alexib.yaba.data.store.HomeScreenState
import tech.alexib.yaba.model.RangeOption
import tech.alexib.yaba.model.Transaction

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
            is HomeScreenAction.SetSpendingWidgetDateRange -> viewModel.submit(action)
        }
    }
}

@Composable
private fun Home(
    state: HomeScreenState,
    actioner: (HomeScreenAction) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {

            val modifier = Modifier
                .verticalScroll(rememberScrollState())

            DateRangeBottomSheet(modifier) { rangeOption ->
                coroutineScope.launch {
                    sheetState.hide()
                }
                actioner(HomeScreenAction.SetSpendingWidgetDateRange(rangeOption))

            }
        }
    ) {
        LoadingScreenWithCrossFade(state.loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AddSpace()

                SlideInContent(visible = state.userItemCount == 0L) {
                    Button(
                        onClick = {
                            actioner(HomeScreenAction.NavigateToPlaidLinkScreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.link_first_institution),
                            style = MaterialTheme.typography.button
                        )
                    }
                }

                SlideInContent(
                    visible = state.currentCashBalance != null &&
                        state.userItemCount != 0L
                ) {
                    TotalCashBalanceRow(state.currentCashBalance)
                    AddSpace()
                }

                SlideInContent(visible = state.recentTransactions.isNotEmpty()) {
                    RecentTransactions(transactions = state.recentTransactions) {
                        actioner(HomeScreenAction.NavigateToTransactionsScreen)
                    }
                }

                SlideInContent(visible = state.spendingByCategory?.spend?.isNotEmpty() ?: false) {
                    if (!state.spendingByCategory?.spend.isNullOrEmpty()) {
                        SpendingWidget(state.spendingByCategory!!) {
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        }
                    }
                }
            }
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


@Composable
private fun DateRangeBottomSheet(
    modifier: Modifier = Modifier,
    handleSelect: (RangeOption) -> Unit
) {
    val today = remember { Clock.System.todayAt(TimeZone.currentSystemDefault()) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val buttonModifier = Modifier
            .fillMaxWidth()

        val months = today.monthNumber

        fun Int.monthString() = when (this) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> throw IllegalArgumentException("month cannot be $this")
        }

        (1..months).forEach {
            val buttonColors = if (it == months) {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            } else ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)

            TextButton(
                onClick = { handleSelect(RangeOption.values()[it - 1]) },
                colors = buttonColors,
                modifier = buttonModifier
            ) {
                Text(text = it.monthString())
            }
            Divider()

        }
        val whiteButtons =
            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
        TextButton(
            onClick = { handleSelect(RangeOption.ThisYear) },
            colors = whiteButtons,
            modifier = buttonModifier
        ) {
            Text(text = "This year")
        }
        Divider()
        TextButton(
            onClick = { handleSelect(RangeOption.LastYear) },
            colors = whiteButtons,
            modifier = buttonModifier
        ) {
            Text(text = "Last year")
        }
        Divider()
        TextButton(
            onClick = { handleSelect(RangeOption.AllTime) },
            colors = whiteButtons,
            modifier = buttonModifier
        ) {
            Text(text = "All time")
        }
    }

}


