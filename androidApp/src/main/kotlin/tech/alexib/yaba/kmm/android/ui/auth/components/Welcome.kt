package tech.alexib.yaba.kmm.android.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate

@Composable
fun Welcome() {
    Text(
        text = "Welcome To",
        style = MaterialTheme.typography.h5,
        color = BlueSlate
    )
    YabaLogo()
}
