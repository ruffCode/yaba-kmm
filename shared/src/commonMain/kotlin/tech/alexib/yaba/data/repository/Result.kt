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
package tech.alexib.yaba.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import tech.alexib.yaba.data.api.YabaApolloResponse

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

inline fun <reified T, reified R : DataResult<T>> Flow<YabaApolloResponse<T>>.toDataResult(): Flow<R> =
    this.transform {
        when (it) {
            is YabaApolloResponse.Success<T> -> Success(it.data)
            is YabaApolloResponse.Error -> ErrorResult(it.message)
        }
    }
