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
package tech.alexib.yaba.kmm.android.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.ui.components.TransactionItem
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.android.util.longFormat
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionStubs

@Composable
fun TransactionListScreen(onSelected: (Uuid) -> Unit) {
    val viewModel: TransactionListScreenViewModel = getViewModel()
    TransactionListScreen(viewModel, onSelected)
}

@Composable
private fun TransactionListScreen(
    viewModel: TransactionListScreenViewModel,
    onSelected: (Uuid) -> Unit,
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(
        initial = emptyList()
    )

    TransactionListScreen(state, onSelected)
}

@Composable
private fun TransactionListScreen(
    transactions: List<Transaction>,
    onSelected: (Uuid) -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
//            IconButton(
//                onClick = {
// //                handleBack()
//                }, modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .padding(top = 4.dp)
//            ) {
//                Icon(Icons.Filled.ArrowBack, "Back arrow")
//            }
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            transactions.groupBy { it.date }.forEach { (date, transactions) ->
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Text(text = date.longFormat(), modifier = Modifier.padding(4.dp))
                    }
                }
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction, onSelected)
                }
            }
        }
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
            transactions = TransactionStubs.transactions
        ) {}
    }
}
