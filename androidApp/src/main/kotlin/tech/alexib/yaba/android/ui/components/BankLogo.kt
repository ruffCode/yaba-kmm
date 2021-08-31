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
package tech.alexib.yaba.android.ui.components

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
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import tech.alexib.yaba.android.R
import tech.alexib.yaba.android.util.base64ToBitmap
import tech.alexib.yaba.model.defaultLogoBase64

@Composable
fun BankLogo(modifier: Modifier = Modifier, logoUrl: String? = null) {
    Box(modifier = modifier) {
        logoUrl?.let {
            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .size(20.dp),
                painter = rememberImagePainter(
                    data = it,
                    imageLoader = LocalImageLoader.current,
                    builder = {
                        placeholder(
                            drawableResId = R.drawable.default_bank
                        )
                    }
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
                .size(25.dp),
        )
    }
}

@Composable
fun BankLogoSmall(modifier: Modifier = Modifier, logoBitmap: Bitmap) {
    Box(modifier = modifier) {
        Image(
            logoBitmap.asImageBitmap(),
            contentDescription = "Bank Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(25.dp),
        )
    }
}

val defaultLogo = base64ToBitmap(defaultLogoBase64)
