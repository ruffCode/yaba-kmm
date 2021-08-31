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
package tech.alexib.yaba.android.ui.settings.plaid_items

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.components.BankLogo
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.PlaidItemsScreenState
import tech.alexib.yaba.model.PlaidItemStubs.itemsWithAccounts
import tech.alexib.yaba.model.PlaidItemWithAccounts

sealed class PlaidItemsScreenAction {
    data class OnItemSelected(val item: PlaidItemWithAccounts) : PlaidItemsScreenAction()
    object NavigateToLinkInstitution : PlaidItemsScreenAction()
}



@Composable
fun PlaidItemsScreen(
    onItemSelected: (PlaidItemWithAccounts) -> Unit,
    navigateToPlaidLink: () -> Unit,
) {
    val viewModel: PlaidItemsScreenViewModel = getViewModel()
    PlaidItemsScreen(viewModel, { onItemSelected(it) }) {
        navigateToPlaidLink()
    }
}

@Composable
private fun PlaidItemsScreen(
    viewModel: PlaidItemsScreenViewModel,
    onItemSelected: (PlaidItemWithAccounts) -> Unit,
    navigateToPlaidLink: () -> Unit,
) {
    val state by
    rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = PlaidItemsScreenState.Empty)
    Crossfade(targetState = state.loading) { loading ->
        when (loading) {
            true -> LoadingScreen()
            false -> PlaidItemsScreen(state = state) { action ->
                when (action) {
                    is PlaidItemsScreenAction.OnItemSelected -> onItemSelected(action.item)
                    is PlaidItemsScreenAction.NavigateToLinkInstitution -> navigateToPlaidLink()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun PlaidItemsScreen(
    state: PlaidItemsScreenState,
    actioner: (PlaidItemsScreenAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        when (state.items.isEmpty()) {
            true -> Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = "You don't have any institutions linked",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
                LinkInstitutionButton(
                    text = "Link your first institution",
                ) {
                    actioner(PlaidItemsScreenAction.NavigateToLinkInstitution)
                }
            }
            false -> {
                PlaidItemsList(modifier = Modifier.align(Alignment.Center), state.items) {
                    actioner(PlaidItemsScreenAction.OnItemSelected(it))
                }

                LinkInstitutionButton(
                    text = "Link another institution",
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    actioner(PlaidItemsScreenAction.NavigateToLinkInstitution)
                }
            }
        }
    }
}

@Composable
private fun PlaidItemsList(
    modifier: Modifier = Modifier,
    items: List<PlaidItemWithAccounts>,
    onItemSelected: (PlaidItemWithAccounts) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items) { plaidItem ->
            ListItem(
                trailing = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = "${plaidItem.hiddenCount} hidden")
                    }
                },
                icon = {
                    BankLogo(logoBitmap = base64ToBitmap(plaidItem.base64Logo))
                },
                modifier = Modifier
                    .clickable {
                        onItemSelected(plaidItem)
                    }
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(text = plaidItem.name)
                }
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun LinkInstitutionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClicked: () -> Unit
) {
    TextButton(
        onClick = onClicked,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun PlaidItemsScreenPreview() {
    YabaTheme {
        PlaidItemsScreen(
            state = PlaidItemsScreenState(
                items = itemsWithAccounts
            )
        ) {
        }
    }
}

@Preview
@Composable
private fun PlaidItemsScreenEmptyPreview() {
    YabaTheme {
        PlaidItemsScreen(state = PlaidItemsScreenState()) {
        }
    }
}
