package tech.alexib.yaba.kmm.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.*

fun base64ToBitmap(b64: String): Bitmap {
    val imageAsBytes: ByteArray = Base64.getDecoder().decode(b64.toByteArray())
    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
}