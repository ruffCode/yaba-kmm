package tech.alexib.yaba.kmm.util

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

val serializer = Json {
    serializersModule = serializerModule
    isLenient = true
    ignoreUnknownKeys = true
}

internal object UuidSerializer : KSerializer<Uuid> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uuid = uuidFrom(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: Uuid) = encoder.encodeString(value.toString())
}

internal val serializerModule = SerializersModule {
    contextual(UuidSerializer)
}
