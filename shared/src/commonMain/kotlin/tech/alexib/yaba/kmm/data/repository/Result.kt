package tech.alexib.yaba.kmm.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import tech.alexib.yaba.kmm.data.api.ApolloResponse

sealed class DataResult<T> {
    open fun get(): T? = null

    fun getOrThrow(): T = when (this) {
        is Success -> get()
        is ErrorResult -> throw RuntimeException(this.error)
    }
}

data class Success<T>(val data: T) : DataResult<T>() {
    override fun get(): T = data
}

data class ErrorResult<T>(val error: String) : DataResult<T>()


inline fun <reified T, reified R : DataResult<T>> Flow<ApolloResponse<T>>.toDataResult(): Flow<R> =
    this.transform {
        when (it) {
            is ApolloResponse.Success<T> -> Success(it.data)
            is ApolloResponse.Error -> ErrorResult(it.message)
        }
    }
