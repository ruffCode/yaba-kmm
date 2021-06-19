package tech.alexib.yaba.kmm.android.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import tech.alexib.yaba.kmm.android.R

import tech.alexib.yaba.kmm.android.ui.home.SplashScreenViewModel
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

        Image(
            modifier = Modifier
                .padding(16.dp)
                .height(200.dp),
            painter = rememberCoilPainter(
                R.drawable.yaba_nbg,
                previewPlaceholder = R.drawable.yaba_nbg
            ),
            contentScale = ContentScale.Fit,
            contentDescription = "$",
        )
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

