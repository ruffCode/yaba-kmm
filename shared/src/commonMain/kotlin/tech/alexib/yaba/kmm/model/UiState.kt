package tech.alexib.yaba.kmm.model

import tech.alexib.yaba.kmm.data.repository.DataResult

data class UiState<T>(
    val loading: Boolean = false,
    val exception: Exception? = null,
    val data: T? = null
) {
    val hasError: Boolean
        get() = exception != null

    val initialLoad:Boolean
        get() = data == null && loading && !hasError
}

//fun <T> UiState<T>.copyWithResult(value: DataResult<T>): UiState<T>{
//    return when(value){
//        is DataResult ->  copy(loading = false, exception = null, data = value.data)
//        is DataResult.Error -> copy(loading = false, exception = value.exception)
//    }
//}