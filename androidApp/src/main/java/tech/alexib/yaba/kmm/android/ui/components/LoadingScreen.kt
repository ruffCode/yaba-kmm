package tech.alexib.yaba.kmm.android.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.android.ui.AddSpace
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme

@Composable
fun LoadingScreenWithCrossFade(
    loadingState: Boolean,
    animationSpec: FiniteAnimationSpec<Float>? = null,
    content: @Composable () -> Unit
) {
    Crossfade(
        targetState = loadingState,
        animationSpec = animationSpec ?: tween(durationMillis = 500, easing = FastOutLinearInEasing)
    ) { loading ->
        when (loading) {
            true -> LoadingScreen()
            false -> content()
        }
    }
}

@Composable
fun LoadingScreen(message: String? = null, @StringRes resId: Int? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            val style = MaterialTheme.typography.h6.copy(color = BlueSlate)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                when {
                    message != null -> Text(
                        text = message,
                        style = style,
                        textAlign = TextAlign.Center
                    )
                    else -> Text(
                        stringResource(id = resId ?: R.string.loading_data),
                        style = style,
                        textAlign = TextAlign.Center
                    )
                }
            }
            AddSpace(40.dp)
            CircularProgressIndicator()
        }
    }

}


@Preview
@Composable
private fun LoadingScreenPreview() {
    YabaTheme {
        LoadingScreen()
    }
}