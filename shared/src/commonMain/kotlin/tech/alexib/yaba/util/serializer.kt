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
package tech.alexib.yaba.util

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

object UuidSerializer : KSerializer<Uuid> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uuid = uuidFrom(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: Uuid) = encoder.encodeString(value.toString())
}

val serializerModule = SerializersModule {
    contextual(UuidSerializer)
}
val jSerializer = Json {
    serializersModule = serializerModule
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}
