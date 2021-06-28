package tech.alexib.yaba.kmm.android.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.android.ui.AddSpace
import tech.alexib.yaba.kmm.android.ui.auth.components.Password
import tech.alexib.yaba.kmm.android.ui.auth.components.Username
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle

internal sealed class LoginScreenAction {
    object Login : LoginScreenAction()
    object Register : LoginScreenAction()
    object NavigateHome : LoginScreenAction()
    data class SetEmail(val email: String) : LoginScreenAction()
    data class SetPassword(val password: String) : LoginScreenAction()
    object PromptForBiometrics : LoginScreenAction()
}

@Immutable
data class LoginScreenState(
    val loggedIn: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = ""
) {
    companion object {
        val Empty = LoginScreenState()
    }
}

@Composable
fun Login(
    navigateToRegister: () -> Unit,
    navigateHome: () -> Unit
) {
    val viewModel: LoginScreenViewModel = getViewModel()

    LoginScreen(
        navigateToRegister = navigateToRegister,
        navigateHome = navigateHome,
        viewModel = viewModel
    )
}

@Composable
private fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateHome: () -> Unit,
    viewModel: LoginScreenViewModel
) {

    val viewState by rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(initial = LoginScreenState.Empty)

    LoginScreen(state = viewState) { action ->
        when (action) {
            is LoginScreenAction.Login -> viewModel.login()
            is LoginScreenAction.Register -> navigateToRegister()
            is LoginScreenAction.SetEmail -> viewModel.setEmail(action.email)
            is LoginScreenAction.SetPassword -> viewModel.setPassword(action.password)
            is LoginScreenAction.NavigateHome -> navigateHome()
            is LoginScreenAction.PromptForBiometrics -> viewModel.loginBio()
        }
    }
}

@Composable
private fun LoginScreen(
    state: LoginScreenState,
    actioner: (LoginScreenAction) -> Unit
) {

    if (state.loggedIn) {
        actioner(LoginScreenAction.NavigateHome)
    }

    var email by remember { mutableStateOf(TextFieldValue(state.email)) }
    var password by remember { mutableStateOf(TextFieldValue(state.password)) }
    val focusRequester = remember { FocusRequester() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        item {
            Text(
                text = "Welcome To",
                style = MaterialTheme.typography.h5,
                color = BlueSlate
            )
        }
        item {
            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp),
                painter = rememberCoilPainter(
                    R.drawable.yaba_y_bl,
                    previewPlaceholder = R.drawable.yaba_y_bl
                ),
                contentScale = ContentScale.Fit,
                contentDescription = "$",
            )
        }


        item {
            Username(usernameState = email, onValueChange = { value ->
                email = value
                actioner(LoginScreenAction.SetEmail(value.text))
            }, onImeAction = { focusRequester.requestFocus() })
        }
        item { AddSpace() }
        item {
            Password(
                label = "Password",
                passwordState = password,
                onValueChange = { value ->
                    password = value
                    actioner(LoginScreenAction.SetPassword(value.text))
                },
                modifier = Modifier.focusRequester(focusRequester),
                onImeAction = { actioner(LoginScreenAction.Login) }
            )
        }


        item { AddSpace() }
        item {
            Button(
                onClick = { actioner(LoginScreenAction.Login) },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }
        state.errorMessage?.let {
            item {
                Text(
                    text = state.errorMessage,
                    style = TextStyle(color = MaterialTheme.colors.error)
                )
            }
        }
        item { AddSpace() }
        item {
            TextButton(
                onClick = { actioner(LoginScreenAction.Register) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Don't have an account?")
            }
        }
        item {
            IconButton(onClick = { actioner(LoginScreenAction.PromptForBiometrics) }) {
                Icon(Icons.Outlined.Fingerprint, "fingerprint")
            }
        }
        item { AddSpace(100.dp) }
    }
}


//@Composable
//private fun LoginScreen(
//    modifier: Modifier = Modifier,
//    errorMessage: String? = null,
//    login: (email: String, password: String) -> Unit,
//    register: () -> Unit
//) {
//    val username = remember { mutableStateOf(TextFieldValue("alexi2@aol.com")) }
//    val password = remember { mutableStateOf(TextFieldValue("password1234")) }
//    val focusRequester = remember { FocusRequester() }
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//
//            .background(color = MaterialTheme.colors.surface)
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Bottom
//    ) {
//
//        item {
//            Text(
//                text = "Welcome To",
//                style = MaterialTheme.typography.h5,
//                color = BlueSlate
//            )
//        }
//        item {
//            Image(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .height(200.dp),
//                painter = rememberCoilPainter(
//                    R.drawable.yaba_y_bl,
//                    previewPlaceholder = R.drawable.yaba_y_bl
//                ),
//                contentScale = ContentScale.Fit,
//                contentDescription = "$",
//            )
//        }
//
//
//        item { Username(usernameState = username, onImeAction = { focusRequester.requestFocus() }) }
//        item { AddSpace() }
//        item {
//            Password(
//                label = "Password",
//                passwordState = password.value,
//                onValueChange = {}
//                modifier = Modifier.focusRequester(focusRequester),
//                onImeAction = { login(username.value.text, password.value.text) }
//            )
//        }
//
//
//        item { AddSpace() }
//        item {
//            Button(
//                onClick = { login(username.value.text, password.value.text) },
//                modifier = Modifier
//                    .height(50.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Login")
//            }
//        }
//        errorMessage?.let {
//            item {
//                Text(text = errorMessage, style = TextStyle(color = MaterialTheme.colors.error))
//            }
//        }
//        item { AddSpace() }
//        item {
//            TextButton(
//                onClick = register,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Don't have an account?")
//            }
//        }
//        item { AddSpace(100.dp) }
//
//
//    }
//}
