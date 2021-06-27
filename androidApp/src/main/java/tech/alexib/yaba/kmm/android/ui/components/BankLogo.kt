package tech.alexib.yaba.kmm.android.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import tech.alexib.yaba.kmm.android.R
import tech.alexib.yaba.kmm.android.util.base64ToBitmap

@Composable
fun BankLogo(modifier: Modifier = Modifier, logoUrl: String? = null) {

    Box(modifier = modifier) {

        logoUrl?.let {

            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .size(20.dp),
                painter = rememberCoilPainter(
                    it,
                    previewPlaceholder = R.drawable.default_bank
                ),
                contentScale = ContentScale.Fit,
                contentDescription = "bank logo",
            )
        } ?: Image(
            defaultLogo.asImageBitmap(),
            contentDescription = "Bank Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
                .size(20.dp),
        )
    }
}

@Composable
fun BankLogo(modifier: Modifier = Modifier, logoBitmap: Bitmap) {

    Box(modifier = modifier) {

        Image(
            logoBitmap.asImageBitmap(),
            contentDescription = "Bank Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
                .size(20.dp),
        )
    }
}

const val defaultLogoBase64 =
    "iVBORw0KGgoAAAANSUhEUgAAAJgAAACYCAMAAAAvHNATAAAFFmlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNi4wLWMwMDIgNzkuMTY0NDYwLCAyMDIwLzA1LzEyLTE2OjA0OjE3ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RFdnQ9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZUV2ZW50IyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgMjEuMiAoV2luZG93cykiIHhtcDpDcmVhdGVEYXRlPSIyMDIwLTExLTE5VDEzOjM5OjUxLTA1OjAwIiB4bXA6TW9kaWZ5RGF0ZT0iMjAyMC0xMS0xOVQxMzo0NToxMC0wNTowMCIgeG1wOk1ldGFkYXRhRGF0ZT0iMjAyMC0xMS0xOVQxMzo0NToxMC0wNTowMCIgZGM6Zm9ybWF0PSJpbWFnZS9wbmciIHBob3Rvc2hvcDpDb2xvck1vZGU9IjMiIHBob3Rvc2hvcDpJQ0NQcm9maWxlPSJzUkdCIElFQzYxOTY2LTIuMSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpjYTZiYzgzYi0xZWMyLTVmNGQtYjNmNS1mNmNlYTAxNDVlOTUi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"

val defaultLogo = base64ToBitmap(defaultLogoBase64)