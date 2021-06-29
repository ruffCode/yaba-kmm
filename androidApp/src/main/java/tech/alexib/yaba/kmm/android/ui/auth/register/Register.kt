package tech.alexib.yaba.kmm.android.ui.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.android.ui.AddSpace
import tech.alexib.yaba.kmm.android.ui.auth.components.Password
import tech.alexib.yaba.kmm.android.ui.auth.components.Username
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle


@Immutable
data class RegistrationScreenState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val registrationSuccess: Boolean = false
) {
    companion object {
        val Empty = RegistrationScreenState()
    }
}


sealed class RegisterScreenAction {
    object RegisterAction : RegisterScreenAction()
    object NavigateHomeAction : RegisterScreenAction()
    object NavigateToLoginAction : RegisterScreenAction()
    data class SetEmail(val email: String) : RegisterScreenAction()
    data class SetPassword(val password: String) : RegisterScreenAction()
}


@Composable
fun RegistrationScreen(
    navigateToLogin: () -> Unit,
    navigateHome: () -> Unit
) {
    val viewModel: RegisterScreenViewModel = getViewModel()
    RegisterScreen(viewModel, navigateToLogin, navigateHome)
}

@Composable
private fun RegisterScreen(
    viewModel: RegisterScreenViewModel,
    navigateToLogin: () -> Unit,
    navigateHome: () -> Unit

) {
    val viewState by rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(initial = RegistrationScreenState.Empty)

    RegisterScreen(state = viewState) { action ->
        when (action) {
            is RegisterScreenAction.SetEmail -> viewModel.setEmail(action.email)
            is RegisterScreenAction.SetPassword -> viewModel.setPassword(action.password)
            is RegisterScreenAction.RegisterAction -> viewModel.register()
            is RegisterScreenAction.NavigateHomeAction -> navigateHome()
            is RegisterScreenAction.NavigateToLoginAction -> navigateToLogin()
        }

    }
}

@Composable
private fun RegisterScreen(
    state: RegistrationScreenState,
    actioner: (RegisterScreenAction) -> Unit
) {
    if (state.registrationSuccess) {
        actioner(RegisterScreenAction.NavigateHomeAction)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalWindowInsets.current.systemBars.toPaddingValues())
    ) {
        var email by remember { mutableStateOf(TextFieldValue(state.email)) }
        var password by remember { mutableStateOf(TextFieldValue(state.password)) }
        val focusRequester = remember { FocusRequester() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                text = "Welcome To",
                style = MaterialTheme.typography.h5,
                color = BlueSlate
            )

            Image(
                modifier = Modifier
                    .height(200.dp),
                painter = rememberCoilPainter(
                    R.drawable.yaba_y_bl,
                    previewPlaceholder = R.drawable.yaba_y_bl
                ),
                contentScale = ContentScale.Fit,
                contentDescription = "YABA logo",
            )

            Username(usernameState = email, onValueChange = { value ->
                email = value
                actioner(RegisterScreenAction.SetEmail(value.text))
            }, onImeAction = { focusRequester.requestFocus() })

            AddSpace(8.dp)
            Password(
                label = "Password",
                passwordState = password,
                onValueChange = { value ->
                    password = value
                    actioner(RegisterScreenAction.SetPassword(value.text))
                },
                modifier = Modifier.focusRequester(focusRequester),
                onImeAction = { actioner(RegisterScreenAction.RegisterAction) }
            )
            AddSpace()

            state.errorMessage?.let {
                Text(text = it, style = TextStyle(color = MaterialTheme.colors.error))
            }


            Button(
                onClick = { actioner(RegisterScreenAction.RegisterAction) },
                modifier = Modifier
                    .height(50.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(text = "Register")
            }

            TextButton(
                onClick = { actioner(RegisterScreenAction.NavigateToLoginAction) },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(text = "Already have an account?")
            }


        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    YabaTheme {
        RegisterScreen(state = RegistrationScreenState.Empty) {

        }
    }

}