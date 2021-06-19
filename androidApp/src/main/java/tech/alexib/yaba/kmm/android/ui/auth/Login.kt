package tech.alexib.yaba.kmm.android.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult

internal sealed class LoginScreenAction {
    data class Login(val email: String, val password: String) : LoginScreenAction()
    object Register : LoginScreenAction()
}

class LoginScreenViewModel(
    private val authRepository: AndroidAuthRepository
) : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("LoginScreenViewModel") }

    private val loginResultFlow = MutableStateFlow<AuthResult?>(null)
    val loginResult: StateFlow<AuthResult?> = loginResultFlow

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            log.d { result.toString() }
            loginResultFlow.emit(result)
        }
    }
}

@Immutable
data class LoginScreenState(
    val loggedIn: Boolean = false,
    val errorMessage: String? = null
)

@Composable
fun Login(
    navigateToRegister: () -> Unit,
    navigateHome: () -> Unit
) {
    val viewModel: LoginScreenViewModel = getViewModel()

    val loginState = viewModel.loginResult.collectAsState()

    val showError = remember { mutableStateOf(false) }
    when (val result = loginState.value) {
        null -> {
            Log.e("LOGIN", "null")
        }
        is AuthResult.Success -> {
            Log.d("LOGIN", "navigating home")
            navigateHome()
        }
        is AuthResult.Error -> {
            showError.value = true
        }

    }
    LoginScreen(
        login = { email, password -> viewModel.login(email, password) },
        register = navigateToRegister
    )
}


@Composable
fun UsernamePasswordForm(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onSubmit: (email: String, password: String) -> Unit
) {
    val username = remember { mutableStateOf(TextFieldValue("alexi2@aol.com")) }
    val password = remember { mutableStateOf(TextFieldValue("password")) }
    val focusRequester = remember { FocusRequester() }

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

    Username(usernameState = username, onImeAction = { focusRequester.requestFocus() })
    AddSpace()
    Password(
        label = "Password",
        passwordState = password,
        modifier = Modifier.focusRequester(focusRequester),
        onImeAction = { onSubmit(username.value.text, password.value.text) }
    )
    AddSpace()
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    login: (email: String, password: String) -> Unit,
    register: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()

            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        val username = remember { mutableStateOf(TextFieldValue("alexi2@aol.com")) }
        val password = remember { mutableStateOf(TextFieldValue("password")) }
        val focusRequester = remember { FocusRequester() }

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

        Username(usernameState = username, onImeAction = { focusRequester.requestFocus() })
        AddSpace()
        Password(
            label = "Password",
            passwordState = password,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { login(username.value.text, password.value.text) }
        )
        AddSpace()
        Button(
            onClick = { login(username.value.text, password.value.text) },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        errorMessage?.let {
            Text(text = errorMessage, style = TextStyle(color = MaterialTheme.colors.error))

        }
        AddSpace()
        Button(
            onClick = register,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Register")
        }
        AddSpace(100.dp)
    }
}

@Composable
fun AddSpace(spaceHeight: Dp = 16.dp) {
    Spacer(modifier = Modifier.height(spaceHeight))
}


@Composable
fun Password(
    label: String,
    passwordState: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {
            passwordState.value = it
        },
        modifier = modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "hide password"
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = "show password"
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "hide password"
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = "show password"
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

}

@Composable
fun Username(
    usernameState: MutableState<TextFieldValue>,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    OutlinedTextField(
        value = usernameState.value,
        onValueChange = {
            usernameState.value = it
        },

        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

}

@Composable
fun Username(
    usernameState: TextFieldValue,
    onValueChange:(TextFieldValue) ->Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {

    OutlinedTextField(
        value = usernameState,
        onValueChange = onValueChange,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

}