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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.android.ui.components.BankLogo
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemsScreenState
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.moneyFormat
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.model.Account
import tech.alexib.yaba.model.PlaidItemWithAccounts

class AccountsScreenViewModel : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("PlaidItemsScreenViewModel") }
    private val itemRepository: ItemRepository by inject()
    private val plaidItemsFlow = MutableStateFlow<List<PlaidItemWithAccounts>>(emptyList())
    private val loadingFlow = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = loadingFlow

    val state: Flow<PlaidItemsScreenState> =
        combine(plaidItemsFlow, loadingFlow) { items, loading ->
            PlaidItemsScreenState(items, loading)
        }.stateIn(
            viewModelScope, started = WhileSubscribed(5000),
            initialValue = PlaidItemsScreenState.Empty,
        )

    init {
        viewModelScope.launch {
            loadingFlow.emit(true)
            itemRepository.getAllWithAccounts().collect { items ->
                plaidItemsFlow.emit(items)
                loadingFlow.emit(false)
            }
        }
    }
}

@Composable
fun AccountsScreen() {
    val viewModel: AccountsScreenViewModel = getViewModel()

    val state by
    rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = PlaidItemsScreenState.Empty)

    AccountsScreen(state = state)
}

@Composable
fun AccountsScreen(
    state: PlaidItemsScreenState
) {
    Box(modifier = Modifier.fillMaxSize()) {

        state.items.forEach {
            AccountsList(
                accounts = it.accounts,
                logo = it.base64Logo
            )
        }
    }
}

@Composable
fun AccountsList(accounts: List<Account>, logo: String) {
    val logoBitmap = base64ToBitmap(logo)
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .padding(4.dp)
    ) {
        item {
            val cashAccounts =
                accounts.filter { it.type == tech.alexib.yaba.model.AccountType.DEPOSITORY }
            val totalCash = cashAccounts.sumOf { it.currentBalance }
            val isPositive = totalCash > 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {

                    Text(
                        text = "$${moneyFormat.format(totalCash)}",
                        color = if (isPositive) MoneyGreen else Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        items(accounts) { account ->

            ListItem(
                icon = {
                    BankLogo(logoBitmap = logoBitmap)
                },
                singleLineSecondaryText = true,
                trailing = {
                    Money(account.availableBalance)
                }
            ) {
                Text(
                    text = "${account.name} - ${account.mask}",
                    style = MaterialTheme.typography.body2.copy(Color.Black)
                )
            }
        }
    }
}

@Composable
fun Money(amount: Double, modifier: Modifier = Modifier) {
    val formatted = if (amount == 0.0) "$0.00" else "$${moneyFormat.format(amount)}"
    val color = if (amount < 0) Color.Red else MoneyGreen
    Text(text = formatted, color = color, modifier = modifier)
}
