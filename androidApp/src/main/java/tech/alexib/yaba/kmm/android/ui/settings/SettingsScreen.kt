package tech.alexib.yaba.kmm.android.ui.settings

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
import tech.alexib.yaba.kmm.android.ui.components.GenericDialog
import tech.alexib.yaba.kmm.android.ui.components.NegativeAction
import tech.alexib.yaba.kmm.android.ui.components.PositiveAction
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme

sealed class SettingsScreenAction {
    object Logout : SettingsScreenAction()
    data class Navigate(val destination: NavDestination) : SettingsScreenAction()
    object ClearAppData : SettingsScreenAction()

    sealed class NavDestination {
        object Auth : NavDestination()
        object LinkedInstitutions : NavDestination()
    }
}


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

            TextButton(
                onClick = {
                    actioner(
                        SettingsScreenAction.Navigate(SettingsScreenAction.NavDestination.LinkedInstitutions)
                    )
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Linked accounts", style = MaterialTheme.typography.h5)
            }
            Divider()

            TextButton(
                onClick = { },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Notifications", style = MaterialTheme.typography.h5)
            }
            Divider()
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {

            Button(
                onClick = { actioner(SettingsScreenAction.Logout) },
                modifier = Modifier

                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Logout")
            }
            TextButton(
                onClick = { clearAppDataRequested = true },
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
        if (clearAppDataRequested) {
            GenericDialog(
                onDismiss = { clearAppDataRequested = false },
                title = "Are you sure?",
                positiveAction = PositiveAction("Confirm") {
                    actioner(SettingsScreenAction.ClearAppData)
                    clearAppDataRequested = false
                },
                negativeAction = NegativeAction("Cancel") {
                    clearAppDataRequested = false
                })
        }
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