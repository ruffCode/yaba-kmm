package tech.alexib.yaba.kmm.data.api

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.LocalDate

val localDateAdapter = object : CustomTypeAdapter<LocalDate> {
    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    override fun decode(value: CustomTypeValue<*>): LocalDate {
        try {
            return LocalDate.parse(value.value.toString())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun encode(value: LocalDate): CustomTypeValue<*> {
        return CustomTypeValue.GraphQLString(
            value.toString()
        )
    }
}

val uuidAdapter = object : CustomTypeAdapter<Uuid> {
    override fun decode(value: CustomTypeValue<*>): Uuid = try {
        uuidFrom(value.value.toString())
    } catch (e: Throwable) {
        throw RuntimeException(e)
    }

    override fun encode(value: Uuid): CustomTypeValue<*> =
        CustomTypeValue.GraphQLString(value.toString())
}

class TypeAdapterParseException(message: String) : Exception(message)
