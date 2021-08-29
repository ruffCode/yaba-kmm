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
package tech.alexib.yaba.android.ui.auth.biometric

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsPadding
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.android.ui.theme.YabaTheme
import tech.alexib.yaba.android.util.rememberFlowWithLifecycle

@Composable
fun BiometricSetupScreen(
    navigateHome: () -> Unit
) {
    val viewModel: BiometricSetupScreenViewModel = getViewModel()
    BiometricSetupScreen(viewModel = viewModel) {
        navigateHome()
    }
}

@Composable
private fun BiometricSetupScreen(
    viewModel: BiometricSetupScreenViewModel,
    navigateHome: () -> Unit
) {
    val state by
    rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = BiometricSetupScreenState.Empty)

    BiometricSetupScreen(state) { action ->
        when (action) {
            is BiometricSetupScreenAction.NavigateHome -> navigateHome()
            is BiometricSetupScreenAction.PromptSetup -> viewModel.setupBiometrics()
            is BiometricSetupScreenAction.Decline -> navigateHome()
        }
    }
}

@Composable
private fun BiometricSetupScreen(
    state: BiometricSetupScreenState,
    actioner: (BiometricSetupScreenAction) -> Unit
) {
    val context = LocalContext.current
    if (state.setupSuccessful || state.declined) {
        actioner(BiometricSetupScreenAction.NavigateHome)
    }

    if (state.errorMessage != null) {
        Toast
            .makeText(
                context,
                state.errorMessage,
                Toast.LENGTH_SHORT
            )
            .show()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Would you like to enable biometric login?",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 1.5.em
            ),

            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 50.dp, top = 150.dp)
        )

        Icon(
            Icons.Outlined.Fingerprint,
            "fingerprint",
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.Center),
            tint = MaterialTheme.colors.onPrimary
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnableBiometricsButton {
                actioner(BiometricSetupScreenAction.PromptSetup)
            }
            NotNowButton {
                actioner(BiometricSetupScreenAction.Decline)
            }
        }
    }
}

@Composable
private fun EnableBiometricsButton(
    handleEnable: () -> Unit,
) {
    OutlinedButton(
        onClick = handleEnable,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        border = BorderStroke(2.dp, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 100.dp)
    ) {
        Text(
            text = "Enable",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun NotNowButton(
    handleDecline: () -> Unit,
) {
    TextButton(onClick = handleDecline) {
        Text(
            text = "Not now",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun BiometricSetupScreenPreview() {
    YabaTheme {
        BiometricSetupScreen(BiometricSetupScreenState.Empty) {
        }
    }
}
