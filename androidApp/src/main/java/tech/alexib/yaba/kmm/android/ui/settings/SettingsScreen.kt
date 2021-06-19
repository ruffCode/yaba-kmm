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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository

sealed class SettingsScreenAction {
    object Logout : SettingsScreenAction()
    data class Navigate(val destination: NavDestination) : SettingsScreenAction()


    sealed class NavDestination {
        object Auth : NavDestination()
    }
}

class SettingsScreenViewModel : ViewModel(), KoinComponent {

    private val authRepository: AndroidAuthRepository by inject()

    fun logout() {
        viewModelScope.launch {
            authRepository.sessionManager.logout()
        }
    }
}


@Composable
fun SettingsScreen(
    navigateTo: (SettingsScreenAction.NavDestination) -> Unit
) {
    val viewModel: SettingsScreenViewModel = getViewModel()

    SettingsScreen(viewModel, navigateTo)
}

@Composable
private fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navigateTo: (SettingsScreenAction.NavDestination) -> Unit
) {

    Settings { action ->
        when (action) {
            is SettingsScreenAction.Logout -> viewModel.logout()
            is SettingsScreenAction.Navigate -> navigateTo(action.destination)
        }
    }
}

@Composable
private fun Settings(
    actioner: (SettingsScreenAction) -> Unit
) {
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
                onClick = { },
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
        Button(
            onClick = { actioner(SettingsScreenAction.Logout) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}