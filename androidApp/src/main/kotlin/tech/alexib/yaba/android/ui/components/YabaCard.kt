package tech.alexib.yaba.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.android.ui.AddSpace

@Composable
fun YabaCard(
    modifier: Modifier = Modifier,
    background: Color? = null,
    content: @Composable () -> Unit
) {
    AddSpace()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(4.dp),
        elevation = 3.dp,
        backgroundColor = background ?: MaterialTheme.colors.surface
    ) {
        content()
    }
}
