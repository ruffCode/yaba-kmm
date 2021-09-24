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
package tech.alexib.yaba.android.ui.plaid

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkResult
import logcat.logcat
import org.koin.androidx.compose.get
import tech.alexib.yaba.android.LocalIsSandBoxProvider
import tech.alexib.yaba.android.R
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.components.LoadingScreen
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.store.PlaidLinkScreenResult
import tech.alexib.yaba.model.response.PlaidItemCreateResponse
import tech.alexib.yaba.model.response.PlaidLinkResult

@Composable
fun PlaidLinkScreen(
    handleResult: (PlaidLinkScreenResult) -> Unit,
    navigateHome: () -> Unit,
) {
    PlaidLinkScreen(navigateHome, handleResult)
}

sealed class PlaidLinkScreenAction {
    object NavigateHome : PlaidLinkScreenAction()
    data class ShowError(val error: String) : PlaidLinkScreenAction()
    data class HandleSuccess(val data: PlaidLinkScreenResult) : PlaidLinkScreenAction()
    data class HandleLinkResult(val data: LinkResult) : PlaidLinkScreenAction()
}


@Composable
private fun PlaidLinkScreen(
    navigateHome: () -> Unit,
    handleResult: (PlaidLinkScreenResult) -> Unit,
    viewModel: PlaidLinkViewModel = get(),
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.result)
        .collectAsState(initial = PlaidLinkResult.Empty)

    PlaidLinkScreen(state = state, viewModel::linkInstitution) { action ->
        when (action) {
            PlaidLinkScreenAction.NavigateHome -> navigateHome()
            is PlaidLinkScreenAction.ShowError -> {
                logcat("PlaidLinkScreenAction") { action.error }
                navigateHome()
            }

            is PlaidLinkScreenAction.HandleLinkResult -> {
                viewModel.handleResult(action.data)
            }
            is PlaidLinkScreenAction.HandleSuccess -> {
                handleResult(action.data)
            }
        }
    }
}

@Composable
private fun PlaidLinkScreen(
    state: PlaidLinkResult,
    linkInstitution: ((LinkTokenConfiguration) -> Unit) -> Unit,
    actioner: (PlaidLinkScreenAction) -> Unit
) {

    val isSandbox = LocalIsSandBoxProvider.current
    Box {
        when (state) {
            PlaidLinkResult.Empty -> PlaidLinkHandler(
                onResult = { linkResult ->
                    actioner(
                        PlaidLinkScreenAction.HandleLinkResult(
                            linkResult
                        )
                    )
                },
                content = { linkLauncher ->
                    if (isSandbox.value) {
                        SandboxInstructions(modifier = Modifier.align(Alignment.Center)) {
                            linkInstitution { config ->
                                linkLauncher.launch(config)
                            }
                        }
                    } else {
                        linkInstitution { config ->
                            linkLauncher.launch(config)
                        }
                    }
                }
            )

            is PlaidLinkResult.Success -> actioner(
                PlaidLinkScreenAction.HandleSuccess(
                    state.data.toPlaidItem()
                )
            )
            is PlaidLinkResult.AwaitingResult -> {
                Log.d("PLAID", "AwaitingResult")

                LoadingScreen()
            }
            is PlaidLinkResult.Error -> actioner(PlaidLinkScreenAction.ShowError(state.message))
            is PlaidLinkResult.Abandoned -> actioner(PlaidLinkScreenAction.NavigateHome)
        }
    }
}

private fun PlaidItemCreateResponse.toPlaidItem(): PlaidLinkScreenResult =
    PlaidLinkScreenResult(
        id = id,
        name = name,
        logo = logo,
        accounts = accounts.map { account ->
            PlaidLinkScreenResult.Account(
                mask = account.mask,
                name = account.name,
                plaidAccountId = account.plaidAccountId,
                show = true
            )
        }
    )

@Composable
fun SandboxInstructions(modifier: Modifier = Modifier, onProceed: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight()
                .align(Alignment.Center)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = stringResource(R.string.this_is_sandbox),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                        .copy(color = MaterialTheme.colors.primary),
                    modifier = Modifier.padding(16.dp)
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = stringResource(R.string.sandbox_instructions_heading),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                        .copy(color = MaterialTheme.colors.primary),
                    modifier = Modifier.padding(16.dp)
                )
            }
            AddSpace(30.dp)

            Surface(elevation = 3.dp, modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(
                            text = "usernames:\ncustom_user1\ncustom_user2\nuser_good",
                            style = MaterialTheme.typography.body1.copy(fontSize = 20.sp)
                        )
                    }

                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(
                            text = "password: pass_good",
                            style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
            }

            AddSpace()
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = stringResource(R.string.multifactor_instructions),
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary).merge(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 40.dp)
            ) {
                Text(text = "OK")
            }
        }
    }
}

@Preview
@Composable
private fun SandboxInstructionsPreview() {
    YabaTheme {
        SandboxInstructions {}
    }
}
