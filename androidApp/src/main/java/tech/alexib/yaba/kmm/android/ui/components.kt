package tech.alexib.yaba.kmm.android.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.android.util.base64ToBitmap

@Composable
fun AddSpace(spaceHeight: Dp = 16.dp) {
    Spacer(modifier = Modifier.height(spaceHeight))
}

