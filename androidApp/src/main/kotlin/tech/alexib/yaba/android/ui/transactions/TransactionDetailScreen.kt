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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.moneyFormat
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.android.util.shortFormat
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.model.TransactionStubs

@Immutable
data class TransactionDetailScreenState(
    val loading: Boolean = false,
    val transaction: TransactionDetail? = null
) {
    companion object {
        val Empty = TransactionDetailScreenState()
    }
}

sealed class TransactionDetailScreenAction {
    object NavigateBack : TransactionDetailScreenAction()
}

@Composable
fun TransactionDetailScreen(id: Uuid, onBackPressed: () -> Unit) {
    val viewModel: TransactionDetailScreenViewModel = getViewModel()
    viewModel.getDetail(id)

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
                IconButton(
                    onClick = {
                        actioner(TransactionDetailScreenAction.NavigateBack)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)

                ) {
                    Icon(Icons.Filled.ArrowBack, "Back arrow")
                }
                Text(
                    text = "Transaction detail",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                )
            }
        },
        modifier = Modifier.statusBarsPadding()
    ) {
        when {
            state.loading -> CircularProgressIndicator()
            state.transaction != null -> TransactionDetailScreenContent(state.transaction)
            else -> Text(text = "Nothing here")
        }
    }
}

@Composable
private fun TransactionDetailScreenContent(transaction: TransactionDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = "receipt",
                tint = MoneyGreen,
                modifier = Modifier
                    .size(150.dp)
                    .padding(4.dp)
            )
        }
        item {
            TransactionRow(
                name = transaction.date.shortFormat(),
                value = "$${moneyFormat.format(transaction.amount)}"
            )
        }
        transaction.merchantName?.let { merchantName ->
            item { TransactionRow(name = "Name", value = merchantName) }
        }

        if (transaction.merchantName != transaction.name) {
            item {
                TransactionRow(
                    name = transaction.merchantName?.let { "Details" } ?: "Name",
                    value = transaction.name
                )
            }
        }

        item {
            TransactionRow(
                name = "Status",
                value = if (transaction.pending == true) "Pending" else "Posted"
            )
        }
        item {
            TransactionRow(
                name = "Account",
                value = transaction.label
            )
        }
        transaction.category?.let {
            item {
                TransactionRow(name = "Category", value = it)
            }
        }
        transaction.subcategory?.let {
            item {
                TransactionRow(name = "Subcategory", value = it)
            }
        }
    }
}

@Composable
private fun TransactionRow(name: String, value: String) {
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
    Divider()
}

@Preview
@Composable
fun TransactionDetailScreenPreview() {
    YabaTheme {
        TransactionDetailScreen(
            state = TransactionDetailScreenState(
                false,
                TransactionStubs.transactionDetail
            )
        ) {
        }
    }
}
