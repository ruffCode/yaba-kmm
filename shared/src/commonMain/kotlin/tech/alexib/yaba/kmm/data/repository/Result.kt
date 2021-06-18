package tech.alexib.yaba.kmm.data.repository

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


//internal fun <T> ApolloResponse<T>.toDataResult(): DataResult<T> {
//    when (this) {
//        is ApolloResponse.Success -> Success(this.data)
//        is ApolloResponse.Error -> ErrorResult(this.errors.first().message)
//    }
//}