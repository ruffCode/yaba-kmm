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
package tech.alexib.yaba.android.ui.auth.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.auth.components.Password
import tech.alexib.yaba.android.ui.auth.components.Username
import tech.alexib.yaba.android.ui.components.ErrorText
import tech.alexib.yaba.android.ui.components.Welcome
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle

internal sealed class LoginScreenAction {
    object Login : LoginScreenAction()
    object Register : LoginScreenAction()
    object NavigateHome : LoginScreenAction()
    object NavigateBiometricSetup : LoginScreenAction()
    data class SetEmail(val email: String) : LoginScreenAction()
    data class SetPassword(val password: String) : LoginScreenAction()
    object PromptForBiometrics : LoginScreenAction()
}

@Immutable
data class LoginScreenState(
    val loggedIn: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val isBiometricAuthEnabled: Boolean = false,
    val shouldPromptForBiometrics: Boolean = false,
    val shouldSetupBiometrics: Boolean = false
) {
    companion object {
        val Empty = LoginScreenState()
    }
}

@Composable
fun Login(
    navigateToRegister: () -> Unit,
    navigateHome: () -> Unit,
    navigateToBiometricSetup: () -> Unit
) {
    val viewModel: LoginScreenViewModel = getViewModel()

    LoginScreen(
        navigateToRegister = navigateToRegister,
        navigateHome = navigateHome,
        navigateToBiometricSetup = navigateToBiometricSetup,
        viewModel = viewModel
    )
}

@Composable
private fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateHome: () -> Unit,
    navigateToBiometricSetup: () -> Unit,
    viewModel: LoginScreenViewModel
) {
    val viewState by rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = LoginScreenState.Empty)

    LoginScreen(state = viewState) { action ->
        when (action) {
            is LoginScreenAction.Login -> viewModel.login()
            is LoginScreenAction.Register -> navigateToRegister()
            is LoginScreenAction.SetEmail -> viewModel.setEmail(action.email)
            is LoginScreenAction.SetPassword -> viewModel.setPassword(action.password)
            is LoginScreenAction.NavigateHome -> navigateHome()
            is LoginScreenAction.PromptForBiometrics -> viewModel.loginBio()
            is LoginScreenAction.NavigateBiometricSetup -> navigateToBiometricSetup()
        }
    }
}

@Composable
private fun LoginScreen(
    state: LoginScreenState,
    actioner: (LoginScreenAction) -> Unit
) {
    if (state.loggedIn) {
        when (state.shouldSetupBiometrics) {
            true -> actioner(LoginScreenAction.NavigateBiometricSetup)
            false -> actioner(LoginScreenAction.NavigateHome)
        }
    }
    if (state.shouldPromptForBiometrics && !state.loggedIn) {
        actioner(LoginScreenAction.PromptForBiometrics)
    }

    var email by remember { mutableStateOf(TextFieldValue(state.email)) }
    var password by remember { mutableStateOf(TextFieldValue(state.password)) }
    val focusRequester = remember { FocusRequester() }

    val imePadding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.ime
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.surface)
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = imePadding
                    .calculateBottomPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Welcome()
        Username(
            usernameState = email,
            onValueChange = { value ->
                email = value
                actioner(LoginScreenAction.SetEmail(value.text))
            },
            onImeAction = { focusRequester.requestFocus() },
            modifier = Modifier.autofill(
                autofillTypes = listOf(AutofillType.EmailAddress, AutofillType.Username),
                onFill = { value ->
                    email = TextFieldValue(value)
                    actioner(LoginScreenAction.SetEmail(value))
                }
            )
        )
        AddSpace(8.dp)

        Password(
            label = "Password",
            passwordState = password,
            onValueChange = { value ->
                password = value
                actioner(LoginScreenAction.SetPassword(value.text))
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .autofill(
                    autofillTypes = listOf(AutofillType.Password),
                    onFill = { value ->
                        password = TextFieldValue(value)
                        actioner(LoginScreenAction.SetPassword(value))
                    }
                ),
            onImeAction = { actioner(LoginScreenAction.Login) }
        )
        AddSpace()
        state.errorMessage?.let {
            ErrorText(errorMessage = it)
        }
        if (state.isBiometricAuthEnabled) {
            FingerprintButton {
                actioner(LoginScreenAction.PromptForBiometrics)
            }
        }
        Button(
            onClick = { actioner(LoginScreenAction.Login) },
            modifier = Modifier
                .height(50.dp)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        TextButton(
            onClick = { actioner(LoginScreenAction.Register) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Don't have an account?")
        }
    }
}

@Composable
private fun FingerprintButton(
    onPressed: () -> Unit
) {
    IconButton(onClick = onPressed) {
        Icon(
            Icons.Outlined.Fingerprint,
            "fingerprint",
            modifier = Modifier
                .size(30.dp)
                .padding(bottom = 4.dp),

            tint = MaterialTheme.colors.primary
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    YabaTheme {
        LoginScreen(state = LoginScreenState.Empty) {
        }
    }
}
//Credit to Bryan Herbst https://bryanherbst.com/2021/04/13/compose-autofill/
@SuppressLint("UnnecessaryComposedModifier")
@ExperimentalComposeUiApi
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}
