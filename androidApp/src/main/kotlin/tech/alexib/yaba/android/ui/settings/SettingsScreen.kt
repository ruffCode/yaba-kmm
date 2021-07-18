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

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.components.ExpandableContent
import tech.alexib.yaba.android.ui.components.GenericDialog
import tech.alexib.yaba.android.ui.components.NegativeAction
import tech.alexib.yaba.android.ui.components.PositiveAction
import tech.alexib.yaba.android.ui.components.YabaLogo
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.data.settings.Theme

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
    val state by rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = SettingsScreenState.Empty)
    Settings(state) { action ->
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
            is SettingsScreenAction.ChangeTheme -> viewModel.setTheme(action.theme)
        }
    }
}

@Immutable
data class SettingsScreenState(
    val theme: Theme = Theme.SYSTEM
) {
    companion object {
        val Empty = SettingsScreenState()
    }
}

@Composable
private fun Settings(
    state: SettingsScreenState,
    actioner: (SettingsScreenAction) -> Unit,
) {
    var clearAppDataRequested by remember {
        mutableStateOf(false)
    }
    val configuration = LocalConfiguration.current

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.paddingFromBaseline(bottom = 40.dp)) {
                LogoutButton {
                    actioner(SettingsScreenAction.Logout)
                }
                ClearAppDataButton {
                    clearAppDataRequested = true
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState()),

            ) {

                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> Box(modifier = Modifier.size(50.dp))
                    else -> Row(
                        modifier = Modifier
                            .fillMaxWidth(),

                        horizontalArrangement = Arrangement.Center
                    ) {

                        YabaLogo(size = 250, modifier = Modifier.padding(top = 40.dp))
                    }
                }

                SettingsScreenButton(label = "Linked accounts") {
                    actioner(
                        SettingsScreenAction.Navigate(
                            SettingsScreenAction.NavDestination.LinkedInstitutions
                        )
                    )
                }
                Divider()

                ThemeSelector(currentTheme = state.theme) {
                    actioner(SettingsScreenAction.ChangeTheme(it))
                }
                Divider()
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                action()
            }
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier
                .padding(12.dp)
                .wrapContentWidth(),
        ) {
            Text(text = label, style = MaterialTheme.typography.h6, textAlign = TextAlign.Start)
        }
    }
}

@Composable
private fun LogoutButton(
    handleLogout: () -> Unit
) {
    Button(
        onClick = handleLogout,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(40.dp)
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
            .padding(8.dp)
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
        Settings(SettingsScreenState.Empty) {
        }
    }
}

@Composable
private fun ThemeButtonRadioGroup(selectedTheme: Theme, setTheme: (Theme) -> Unit) {
    val radioOptions = listOf(Theme.SYSTEM, Theme.DARK, Theme.LIGHT)
    Column(
        modifier = Modifier
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        radioOptions.forEach { theme ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { setTheme(theme) }
            ) {
                Row(
                    Modifier
                        .width(250.dp)
                        .padding(vertical = 4.dp)
                        .selectable(
                            selected = (theme == selectedTheme),
                            onClick = {
                                setTheme(theme)
                            }
                        ),

                ) {
                    RadioButton(
                        selected = (theme == selectedTheme),
                        onClick = {
                            setTheme(theme)
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.button.merge()

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelector(currentTheme: Theme, onSelectTheme: (Theme) -> Unit) {
    val expanded = remember {
        mutableStateOf(false)
    }

    Column {
        SettingsScreenButton(label = "Theme") {
            expanded.value = !expanded.value
        }
        ExpandableContent(visible = expanded) {
            ThemeButtonRadioGroup(selectedTheme = currentTheme) {
                onSelectTheme(it)
            }
        }
    }
}

@Preview
@Composable
fun ThemeSelectorPreview() {
    YabaTheme {

        ThemeButtonRadioGroup(selectedTheme = Theme.SYSTEM) {
        }
    }
}
