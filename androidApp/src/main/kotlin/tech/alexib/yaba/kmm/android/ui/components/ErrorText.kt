package tech.alexib.yaba.kmm.android.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@Composable
fun ErrorText(errorMessage: String) {
    Text(
        text = errorMessage,
        style = TextStyle(color = MaterialTheme.colors.error)
    )
}
