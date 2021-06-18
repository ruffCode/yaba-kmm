package tech.alexib.yaba.kmm.data.api

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.Error as ApolloError

sealed class ApolloResponse<out T> {

    data class Success<T>(val data: T) : ApolloResponse<T>()
    data class Error(val errors: List<String>) :
        ApolloResponse<Nothing>() {
        val message = errors.first()
    }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(errors: List<String>) = Error(errors)

        operator fun <T> invoke(response: Response<T>): ApolloResponse<T> {
            return if (response.hasErrors()) {
                error(response.errors!!)
            } else {
                success(response.data!!)
            }
        }

        operator fun <T, R> invoke(
            response: Response<T>,
            mapper: (T) -> R
        ): ApolloResponse<R> {
            return if (response.hasErrors()) {
                error(response.errorMessages())
            } else {
                success(mapper(response.data!!))
            }
        }
    }


}

private fun List<ApolloError>.messages(): List<String> = this.map { it.message }
fun <T> Response<T>.errorMessages(): List<String> =
    this.errors?.map { it.message } ?: emptyList()