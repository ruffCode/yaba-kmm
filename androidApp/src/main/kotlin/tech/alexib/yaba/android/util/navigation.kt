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
package tech.alexib.yaba.android.util

import android.os.Bundle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import tech.alexib.yaba.util.serializer

inline fun <reified T> Bundle.getSerialized(key: String): T? {
    return this.getString(key)?.let {
        serializer.decodeFromString(it)
    }
}

inline fun <reified T> Bundle.putSerialized(key: String, value: T) =
    this.putString(key, serializer.encodeToString(value))
