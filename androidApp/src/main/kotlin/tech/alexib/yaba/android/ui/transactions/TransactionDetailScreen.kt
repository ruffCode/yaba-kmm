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
package tech.alexib.yaba.android.ui.transactions

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getStateViewModel
import tech.alexib.yaba.android.ui.components.BackArrowButton
import tech.alexib.yaba.android.ui.components.LoadingScreenWithCrossFade
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.moneyFormat
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.android.util.shortFormat
import tech.alexib.yaba.data.store.TransactionDetailScreenState
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.stubs.TransactionStubs

@Composable
fun TransactionDetailScreen(bundle: Bundle?, onBackPressed: () -> Unit) {

    val viewModel: TransactionDetailScreenViewModel =
        getStateViewModel(state = { bundle ?: Bundle() })

    TransactionDetailScreen(viewModel, onBackPressed)
}

@Composable
private fun TransactionDetailScreen(
    viewModel: TransactionDetailScreenViewModel,
    onBackPressed: () -> Unit
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = TransactionDetailScreenState.Empty)

    TransactionDetailScreen(state) { action ->
        when (action) {
            is TransactionDetailScreenAction.NavigateBack -> onBackPressed()
        }
    }
}

@Composable
private fun TransactionDetailScreen(
    state: TransactionDetailScreenState,
    actioner: (TransactionDetailScreenAction) -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically),
            ) {

                BackArrowButton(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    actioner(TransactionDetailScreenAction.NavigateBack)
                }

                Text(
                    text = "Transaction detail",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                )
            }
        },
    ) {
        LoadingScreenWithCrossFade(loadingState = state.loading) {
            state.transaction?.let {
                TransactionDetailScreenContent(it)
            } ?: Text(text = "Nothing here")
        }
    }
}

@Composable
private fun TransactionDetailScreenContent(transaction: TransactionDetail) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(vertical = 30.dp, horizontal = 4.dp),

        elevation = 3.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())

        ) {
            Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = "receipt",
                tint = MoneyGreen,
                modifier = Modifier
                    .size(150.dp)
                    .padding(4.dp)
            )
            TransactionRow(
                name = transaction.date.shortFormat(),
                value = "$${moneyFormat.format(transaction.amount)}"
            )
            Divider()
            transaction.merchantName?.let { merchantName ->
                TransactionRow(name = "Name", value = merchantName)
                Divider()
            }

            if (transaction.merchantName != transaction.name) {
                TransactionRow(
                    name = transaction.merchantName?.let { "Details" } ?: "Name",
                    value = transaction.name
                )
                Divider()
            }

            TransactionRow(
                name = "Status",
                value = if (transaction.pending == true) "Pending" else "Posted"
            )
            Divider()
            TransactionRow(
                name = "Account",
                value = transaction.label
            )
            Divider()
            transaction.category?.let {
                TransactionRow(name = "Category", value = it)
                Divider()
            }
            transaction.subcategory?.let {
                TransactionRow(name = "Subcategory", value = it)
            }
        }
    }
}

@Composable
fun TransactionRow(name: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = name)
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                maxLines = 5,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 25.dp)
            )
        }
    }
}

@Preview
@Composable
fun TransactionDetailScreenPreview() {
    YabaTheme {
        TransactionDetailScreen(
            state = TransactionDetailScreenState(
                false,
                TransactionStubs.transactionDetail.first()
            )
        ) {
        }
    }
}
