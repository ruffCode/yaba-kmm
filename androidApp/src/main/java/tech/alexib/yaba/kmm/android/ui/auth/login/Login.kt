package tech.alexib.yaba.kmm.android.ui.auth.login

import android.util.Log
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import tech.alexib.yaba.kmm.data.repository.AuthResult

internal sealed class LoginScreenAction {
    data class Login(val email: String, val password: String) : LoginScreenAction()
    object Register : LoginScreenAction()
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
    when (loginState.value) {
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
        val password = remember { mutableStateOf(TextFieldValue("password1234")) }
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






