package tech.alexib.yaba.kmm.android.util

import android.os.Bundle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import tech.alexib.yaba.kmm.util.serializer

inline fun <reified T> Bundle.getSerialized(key: String): T? {
    return this.getString(key)?.let {
        serializer.decodeFromString(it)
    }
}

inline fun <reified T> Bundle.putSerialized(key: String, value: T) =
    this.putString(key, serializer.encodeToString(value))
