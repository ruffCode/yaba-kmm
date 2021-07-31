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
package tech.alexib.yaba.data.network.apollo

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.apollographql.apollo.interceptor.ApolloRequest
import com.apollographql.apollo.interceptor.ApolloRequestInterceptor
import com.apollographql.apollo.interceptor.ApolloResponse
import com.apollographql.apollo.interceptor.BearerTokenInterceptor
import com.apollographql.apollo.interceptor.TokenProvider
import com.apollographql.apollo.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo.network.ws.ApolloWebSocketFactory
import com.apollographql.apollo.network.ws.ApolloWebSocketNetworkTransport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import tech.alexib.yaba.data.domain.AuthTokenProvider
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

interface YabaApolloClient {

    val backgroundDispatcher: CoroutineDispatcher
    fun <T : Operation.Data, R> query(
        queryData: Query<T, T, Operation.Variables>,
        mapper: (T) -> R,
    ): Flow<DataResult<R>>

    fun <T : Operation.Data, R> mutate(
        mutationData: Mutation<T, T, Operation.Variables>,
        mapper: (T) -> R,
    ): Flow<DataResult<R>>

    fun <T : Operation.Data> mutate(
        mutationData: Mutation<T, T, Operation.Variables>,
    ): Flow<Response<T>>

    class Impl(
        serverUrl: String,
        log: Kermit,
        private val authTokenProvider: AuthTokenProvider,
        override val backgroundDispatcher: CoroutineDispatcher
    ) : YabaApolloClient, TokenProvider {

        @Suppress("CanBePrimaryConstructorProperty")
        private val log = log

        init {
            ensureNeverFrozen()
        }

        private val apolloClient: ApolloClient by lazy {
            ApolloClient(
                networkTransport = ApolloHttpNetworkTransport(
                    serverUrl = serverUrl,
                    headers = mutableMapOf(
                        "Accept" to "application/json",
                        "Content-Type" to "application/json",
                    ),
                ),
                subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
                    webSocketFactory = ApolloWebSocketFactory(
                        serverUrl = serverUrl,
                        mutableMapOf(
                            "Accept" to "application/json",
                            "Content-Type" to "application/json",
                        )
                    )
                ),
                scalarTypeAdapters = customScalarTypeAdapters,
                interceptors = listOf(BearerTokenInterceptor(this), LoggingInterceptor(log))
            )
        }

        override suspend fun currentToken(): String = authTokenProvider.token().firstOrNull() ?: ""

        override suspend fun refreshToken(previousToken: String): String = ""

        override fun <T : Operation.Data, R> query(
            queryData: Query<T, T, Operation.Variables>,
            mapper: (T) -> R,
        ): Flow<DataResult<R>> = apolloClient.query(queryData).execute().map(checkResponse(mapper))

        override fun <T : Operation.Data, R> mutate(
            mutationData: Mutation<T, T, Operation.Variables>,
            mapper: (T) -> R,
        ): Flow<DataResult<R>> =
            apolloClient.mutate(mutationData).execute().map(checkResponse(mapper))

        override fun <T : Operation.Data> mutate(
            mutationData: Mutation<T, T, Operation.Variables>
        ): Flow<Response<T>> {
            return apolloClient.mutate(mutationData).execute()
        }

        private fun <T, R> checkResponse(mapper: (T) -> R): suspend (Response<T>) ->
        DataResult<R> {
            return {
                if (it.hasErrors()) {
                    ErrorResult(it.errors?.firstOrNull()?.message ?: "network error")
                } else {
                    Success(mapper(it.data!!))
                }
            }
        }
    }
}

internal class LoggingInterceptor(private val log: Kermit) : ApolloRequestInterceptor {

    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain,
    ): Flow<ApolloResponse<D>> {
        val uuid = request.requestUuid.toString()
        val operation = request.operation.name().name()
        val variables = request.operation.variables().valueMap().toString()
        val (response, elapsed) = measureTimedValue {
            chain.proceed(request)
        }
        val timeInMillis = elapsed.toDouble(DurationUnit.MILLISECONDS).toString()
        val props = mutableMapOf<String, String>()
        props["Request UUID"] = uuid
        props["Request Name"] = operation
        props["Request Variables"] = variables
        props["Response Time"] = timeInMillis
        val propsString = props.toString()
        log.d { propsString }
        return response
    }
}
