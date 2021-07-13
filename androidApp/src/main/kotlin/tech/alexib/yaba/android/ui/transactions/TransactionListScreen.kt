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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.components.TransactionItem
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.format
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionStubs

@Composable
fun TransactionListScreen(onBack: () -> Unit, onSelected: (Uuid) -> Unit) {
    val viewModel: TransactionListScreenViewModel = getViewModel()
    TransactionListScreen(viewModel, onBack, onSelected)
}

@Composable
private fun TransactionListScreen(
    viewModel: TransactionListScreenViewModel,
    onBack: () -> Unit,
    onSelected: (Uuid) -> Unit,
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(
        initial = emptyList()
    )

    TransactionListScreen(state, onBack, onSelected)
}

@Composable
private fun TransactionListScreen(
    transactions: List<Transaction>,
    handleBack: () -> Unit,
    onSelected: (Uuid) -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = {
                        handleBack()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back arrow")
                }
                Text(
                    text = "Transactions",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                )
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        TransactionList(transactions = transactions) {
            onSelected(it)
        }
    }
}

@Composable
fun TransactionList(transactions: List<Transaction>, onSelected: (Uuid) -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = 3.dp,

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            transactions.groupBy { it.date }.forEach { (date, transactions) ->
                stickyHeader {
                    TransactionDateHeader(date)
                }
                itemsIndexed(transactions) { index, transaction ->
                    val needsDivider = index != transactions.lastIndex
                    TransactionItem(transaction = transaction, needsDivider, onSelected)
                }
            }
        }
    }
}

@Composable
fun TransactionDateHeader(date: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary.copy(alpha = 0.9f))
    ) {
        Text(
            text = date.format(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@Preview
@Composable
private fun TransactionItemPreview() {
    YabaTheme(darkTheme = true) {
        TransactionItem(transaction = TransactionStubs.transactionStub) {}
    }
}

@Preview
@Composable
private fun TransactionListScreenPreview() {
    YabaTheme {
        TransactionListScreen(
            transactions = TransactionStubs.transactions,
            onSelected = {},
            handleBack = {}
        )
    }
}
