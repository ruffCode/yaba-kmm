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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.components.BackArrowButton
import tech.alexib.yaba.android.ui.components.LoadingScreenWithCrossFade
import tech.alexib.yaba.android.ui.components.TransactionItem
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.format
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.TransactionsStore
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.stubs.TransactionStubs


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
    val state by rememberFlowWithLifecycle(
        flow = viewModel.state
    ).collectAsState(
        initial = TransactionsStore.State.Empty
    )

    val loading = remember(state.transactions) { mutableStateOf(state.transactions.isEmpty()) }

    LaunchedEffect(key1 = loading) {
        if (loading.value) {
            delay(400)
            loading.value = false
        }
    }
    LoadingScreenWithCrossFade(loadingState = loading.value) {
        TransactionListScreen(state, onBack, onSelected) {
            viewModel.store.submit(it)
        }
    }

}

@Composable
private fun TransactionListScreen(
    state: TransactionsStore.State,
    handleBack: () -> Unit,
    onSelected: (Uuid) -> Unit,
    actioner: (TransactionsStore.Action) -> Unit
) {

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {

                BackArrowButton(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp)
                ) {
                    handleBack()
                }

                AnimatedVisibility(
                    visible = state.searching,
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    val query = remember { mutableStateOf(TextFieldValue(state.query)) }

                    val focusRequester = remember { FocusRequester() }
                    TextField(
                        value = query.value,
                        onValueChange = {
                            query.value = it
                            actioner(TransactionsStore.Action.SetQuery(it.text))
                        },
                        maxLines = 1,
                        singleLine = true,
                        modifier = Modifier
                            .wrapContentHeight(),
                        colors = TextFieldDefaults
                            .textFieldColors(
                                backgroundColor = MaterialTheme.colors.surface.copy(
                                    alpha = 0.4f
                                )
                            ),
                        keyboardActions = KeyboardActions {
                            focusRequester.requestFocus()
                        },
                        leadingIcon = {
                            AnimatedVisibility(visible = state.query.isEmpty()) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "search"
                                )
                            }
                        },
                        trailingIcon = {
                            AnimatedVisibility(visible = state.query.isNotEmpty()) {
                                IconButton(onClick = {
                                    query.value = TextFieldValue("")
                                    actioner(
                                        TransactionsStore.Action.SetQuery(
                                            ""
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Clear input"
                                    )
                                }
                            }

                        }
                    )
                }
                AnimatedVisibility(
                    visible = !state.searching, modifier = Modifier.align(
                        Alignment.TopEnd
                    )
                ) {
                    IconButton(
                        onClick = { actioner(TransactionsStore.Action.SetSearching) },
                    ) {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "search")
                    }
                }
            }
        },
    ) {
        if (state.transactions.isNotEmpty()) {
            TransactionList(transactions = state.transactions) {
                onSelected(it)
            }
        } else {
            Box(
                modifier = Modifier
                    .heightIn(50.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = "We couldn't find any transactions",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.paddingFromBaseline(top = 30.dp)
                    )
                }
            }
        }

    }
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    onSelected: (Uuid) -> Unit
) {
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
        TransactionItem(transaction = TransactionStubs.transactionsWellsFargo1.first()) {}
    }
}

@Preview
@Composable
private fun TransactionListScreenPreview() {
    YabaTheme {
        TransactionListScreen(
            TransactionsStore.State(transactions = TransactionStubs.transactionsWellsFargo1),
            onSelected = {},
            handleBack = {}
        ) {}
    }
}
