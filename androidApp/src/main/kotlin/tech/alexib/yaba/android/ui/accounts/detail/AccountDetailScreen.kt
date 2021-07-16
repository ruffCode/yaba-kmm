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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.BankLogoSmall
import tech.alexib.yaba.android.ui.components.Money
import tech.alexib.yaba.android.ui.components.YabaRow
import tech.alexib.yaba.android.ui.transactions.TransactionList
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.model.Account

sealed class AccountDetailScreenAction {
    object NavigateBack : AccountDetailScreenAction()
    data class OnTransactionSelected(val transactionId: Uuid) : AccountDetailScreenAction()
}

@Composable
fun AccountDetailScreen(
    params: AccountDetailScreenParams,
    onBack: () -> Unit,
    onTransactionSelected: (Uuid) -> Unit
) {
    val viewModel: AccountDetailScreenViewModel = getViewModel()

    viewModel.init(params.accountId, params.itemId)

    AccountDetailScreen(viewModel, onBack, onTransactionSelected)
}

@Composable
private fun AccountDetailScreen(
    viewModel: AccountDetailScreenViewModel,
    onBack: () -> Unit,
    onTransactionSelected: (Uuid) -> Unit
) {

    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = AccountDetailScreenState.Empty)

    AccountDetailScreen(state = state) { action ->
        when (action) {
            is AccountDetailScreenAction.NavigateBack -> onBack()
            is AccountDetailScreenAction.OnTransactionSelected -> onTransactionSelected(action.transactionId)
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
                IconButton(
                    onClick = {
                        actioner(AccountDetailScreenAction.NavigateBack)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back arrow")
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
        modifier = Modifier.statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding(true)

        ) {
            AnimatedVisibility(
                visible = state.account != null,
                enter = slideInVertically(
                    initialOffsetY = { -40 }
                ) + expandVertically(
                    expandFrom = Alignment.Top
                ) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                val modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)

                state.account?.let {
                    AccountDetailHeader(account = it, modifier)
                }
            }

            AnimatedVisibility(
                visible = state.transactions.isNotEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { -40 }
                ) + expandVertically(
                    expandFrom = Alignment.Top
                ) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                TransactionList(state.transactions) {
                    actioner(AccountDetailScreenAction.OnTransactionSelected(it))
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
            Text(text = "Current balance", style = MaterialTheme.typography.body1)
            AddSpace()
            Text(
                text = "${account.name} ****${account.mask}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp)
            )
            Divider()
            account.availableBalance?.let {
                YabaRow(name = "Available balance") {
                    Money(amount = it)
                }
                Divider()
            }
            account.creditLimit?.let {
                YabaRow(name = "Total limit") {
                    Money(amount = it)
                }
                Divider()
            }


            YabaRow(name = "Institution", value = account.institutionName)
        }
    }
}
