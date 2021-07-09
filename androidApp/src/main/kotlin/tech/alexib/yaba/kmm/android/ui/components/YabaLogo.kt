package tech.alexib.yaba.kmm.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.kmm.android.R

@Composable
fun YabaLogo(size: Int = 500) {
    Image(
        contentScale = ContentScale.Fit,
        contentDescription = "yaba logo",
        modifier = Modifier.width(size.dp),
        bitmap = ImageBitmap.imageResource(id = R.drawable.yaba_bg_bl),
    )
}
