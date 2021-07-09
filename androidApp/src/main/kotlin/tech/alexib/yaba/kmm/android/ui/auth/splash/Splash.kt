package tech.alexib.yaba.kmm.android.ui.auth.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tech.alexib.yaba.kmm.android.ui.components.YabaLogo
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate

@Composable
fun Splash(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlueSlate),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YabaLogo()
    }
}

@Composable
fun Splash(
    splashScreenViewModel: SplashScreenViewModel,
    modifier: Modifier = Modifier
) {
    SideEffect { splashScreenViewModel.splashScreenNavigation() }

    Splash(modifier)
}
