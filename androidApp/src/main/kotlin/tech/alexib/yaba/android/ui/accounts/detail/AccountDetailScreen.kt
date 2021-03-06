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
package tech.alexib.yaba.android.ui.accounts.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.benasher44.uuid.Uuid
import tech.alexib.yaba.android.R
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.BackArrowButton
import tech.alexib.yaba.android.ui.components.BankLogoSmall
import tech.alexib.yaba.android.ui.components.Money
import tech.alexib.yaba.android.ui.components.TransactionItem
import tech.alexib.yaba.android.ui.components.YabaRow
import tech.alexib.yaba.android.ui.transactions.TransactionDateHeader
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.AccountDetailScreenState
import tech.alexib.yaba.model.Account

@Composable
fun AccountDetailScreen(
    viewModel: AccountDetailScreenViewModel,
    onBack: () -> Unit,
    onTransactionSelected: (Uuid) -> Unit
) {

    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = AccountDetailScreenState.Empty)

    AccountDetailScreen(state = state) { action ->
        when (action) {
            is AccountDetailScreenAction.NavigateBack -> onBack()
            is AccountDetailScreenAction.OnTransactionSelected ->
                onTransactionSelected(action.transactionId)
        }
    }
}

@Composable
private fun AccountDetailScreen(
    state: AccountDetailScreenState,
    actioner: (AccountDetailScreenAction) -> Unit
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
                    actioner(AccountDetailScreenAction.NavigateBack)
                }
                state.plaidItem?.let {
                    BankLogoSmall(
                        logoBitmap = base64ToBitmap(it.base64Logo),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp)
                    )
                }
            }
        },
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
        ) {

            item {
                val modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                state.account?.let {
                    AccountDetailHeader(account = it, modifier)
                }
            }
            if (state.transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .heightIn(50.dp)
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                text = stringResource(R.string.no_transactions_for_account),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.paddingFromBaseline(top = 30.dp)
                            )
                        }
                    }
                }
            } else {
                state.transactions.groupBy { it.date }.forEach { (date, transactions) ->
                    stickyHeader {
                        TransactionDateHeader(date)
                    }
                    itemsIndexed(transactions) { index, transaction ->
                        val needsDivider = index != transactions.lastIndex
                        TransactionItem(
                            transaction = transaction,
                            needsDivider,
                        ) {
                            actioner(AccountDetailScreenAction.OnTransactionSelected(it))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountDetailHeader(account: Account, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(4.dp),
        elevation = 3.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 8.dp)
        ) {
            Money(
                amount = account.currentBalance,
                textStyle = TextStyle(fontSize = 20.sp)
            )
            AddSpace(8.dp)
            Text(text = stringResource(R.string.current_balance), style = MaterialTheme.typography.body1)
            AddSpace()
            Text(
                text = "${account.name} ****${account.mask}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp)
            )
            Divider()
            account.availableBalance?.let {
                YabaRow(name = stringResource(R.string.available_balance)) {
                    Money(amount = it)
                }
                Divider()
            }
            account.creditLimit?.let {
                YabaRow(name = stringResource(R.string.total_limit)) {
                    Money(amount = it)
                }
                Divider()
            }

            YabaRow(name = stringResource(R.string.institution), value = account.institutionName)
        }
    }
}
