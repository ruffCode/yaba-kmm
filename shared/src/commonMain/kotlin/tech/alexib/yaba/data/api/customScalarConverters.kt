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
package tech.alexib.yaba.data.api

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters

import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.LocalDate

//val localDateAdapter = object : CustomTypeAdapter<LocalDate> {
//    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
//    override fun decode(value: CustomTypeValue<*>): LocalDate {
//        try {
//            return LocalDate.parse(value.value.toString())
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }
//
//    override fun encode(value: LocalDate): CustomTypeValue<*> {
//        return CustomTypeValue.GraphQLString(
//            value.toString()
//        )
//    }
//}
//
//val uuidAdapter = object : CustomTypeAdapter<Uuid> {
//    override fun decode(value: CustomTypeValue<*>): Uuid = try {
//        uuidFrom(value.value.toString())
//    } catch (e: Throwable) {
//        throw RuntimeException(e)
//    }
//
//    override fun encode(value: Uuid): CustomTypeValue<*> =
//        CustomTypeValue.GraphQLString(value.toString())
//}
//
//class TypeAdapterParseException(message: String) : Exception(message)
internal val uuidAdapter = object : Adapter<Uuid> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Uuid {
        return uuidFrom(reader.nextString()!!)
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: Uuid
    ) {
        writer.value(value.toString())
    }
}

internal val customScalarTypeAdapters = CustomScalarAdapters(
    mapOf(
        "UUID" to uuidAdapter,
        "ID" to uuidAdapter,

//        CustomType.LOCALDATE to localDateAdapter,
    )
)
