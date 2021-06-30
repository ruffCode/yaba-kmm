package tech.alexib.yaba.kmm.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import tech.alexib.yaba.kmm.android.ui.components.defaultLogo
import java.util.*

fun base64ToBitmap(b64: String): Bitmap {
    return try {
        val imageAsBytes: ByteArray = Base64.getDecoder().decode(b64.toByteArray())
        BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    } catch (e: Throwable) {
        Log.e("base64ToBitmap", b64)
        defaultLogo
    }
}