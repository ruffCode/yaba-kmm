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
package tech.alexib.yaba.android.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.components.GenericDialog
import tech.alexib.yaba.android.ui.components.NegativeAction
import tech.alexib.yaba.android.ui.components.PositiveAction
import tech.alexib.yaba.android.ui.theme.YabaTheme

@Composable
fun SettingsScreen(
    navigateTo: (SettingsScreenAction.NavDestination) -> Unit,
) {
    val viewModel: SettingsScreenViewModel = getViewModel()

    SettingsScreen(viewModel, navigateTo)
}

@Composable
private fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navigateTo: (SettingsScreenAction.NavDestination) -> Unit,
) {
    Settings { action ->
        when (action) {
            is SettingsScreenAction.Logout -> {
                viewModel.logout()
                navigateTo(SettingsScreenAction.NavDestination.Auth)
            }
            is SettingsScreenAction.Navigate -> navigateTo(action.destination)
            is SettingsScreenAction.ClearAppData -> {
                viewModel.clearAppData()
                navigateTo(SettingsScreenAction.NavDestination.Auth)
            }
        }
    }
}

@Composable
private fun Settings(
    actioner: (SettingsScreenAction) -> Unit,
) {
    var clearAppDataRequested by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .wrapContentHeight(Alignment.CenterVertically)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            SettingsScreenButton(label = "Linked accounts") {
                actioner(
                    SettingsScreenAction.Navigate(
                        SettingsScreenAction.NavDestination.LinkedInstitutions
                    )
                )
            }
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            LogoutButton {
                actioner(SettingsScreenAction.Logout)
            }
            ClearAppDataButton {
                clearAppDataRequested = true
            }
        }
        if (clearAppDataRequested) {
            GenericDialog(
                title = "Are you sure?",
                positiveAction = PositiveAction("Confirm") {
                    actioner(SettingsScreenAction.ClearAppData)
                    clearAppDataRequested = false
                },
                negativeAction = NegativeAction("Cancel") {
                    clearAppDataRequested = false
                },
                warnConfirm = true,
                description = "This action will clear all app data and log you out"
            )
        }
    }
}

@Composable
private fun SettingsScreenButton(
    label: String,
    action: () -> Unit
) {
    TextButton(
        onClick = action,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.h5)
    }
    Divider()
}

@Composable
private fun LogoutButton(
    handleLogout: () -> Unit
) {
    Button(
        onClick = handleLogout,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Logout")
    }
}

@Composable
private fun ClearAppDataButton(
    handleClearData: () -> Unit
) {
    TextButton(
        onClick = handleClearData,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),

    ) {
        Text(
            text = "Clear app data",
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.error
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    YabaTheme {
        Settings {
        }
    }
}
