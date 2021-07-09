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
package tech.alexib.yaba.kmm.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import tech.alexib.yaba.kmm.android.ui.components.defaultLogo
import java.util.Base64

@Suppress("FunctionParameterNaming", "FunctionNaming")
fun base64ToBitmap(b64: String): Bitmap {
    return try {
        val imageAsBytes: ByteArray = Base64.getDecoder().decode(b64.toByteArray())
        BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    } catch (e: Throwable) {
        Log.e("base64ToBitmap", b64)
        defaultLogo
    }
}
