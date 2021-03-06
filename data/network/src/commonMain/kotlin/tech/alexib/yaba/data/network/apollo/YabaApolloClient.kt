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
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.LocalDateAdapter
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.api.Subscription
import com.apollographql.apollo3.api.http.HttpHeader
import com.apollographql.apollo3.api.http.withHttpHeader
import com.apollographql.apollo3.api.http.withHttpHeaders
import com.apollographql.apollo3.api.variables
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.NetworkTransport
import com.apollographql.apollo3.network.http.BearerTokenInterceptor
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.http.TokenProvider
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.alexib.yaba.data.domain.AuthTokenProvider
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import yaba.schema.type.LocalDate
import yaba.schema.type.UUID
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

interface YabaApolloClient {

    val backgroundDispatcher: CoroutineDispatcher
    fun <T : Query.Data, R> query(
        queryData: Query<T>,
        mapper: (T) -> R,
    ): Flow<DataResult<R>>

    fun <T : Mutation.Data, R> mutate(
        mutationData: Mutation<T>,
        mapper: (T) -> R,
    ): Flow<DataResult<R>>

    fun <T : Mutation.Data> mutate(
        mutationData: Mutation<T>,
    ): Flow<ApolloResponse<T>>

    fun <T : Subscription.Data> subscribe(
        subscriptionData: Subscription<T>
    ): Flow<ApolloResponse<T>>

    fun <T : Subscription.Data, R> subscribe(
        subscriptionData: Subscription<T>,
        mapper: (T) -> R
    ): Flow<DataResult<R>>

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

//            val netTransport = HttpNetworkTransport(
//                serverUrl = serverUrl,
//
//                interceptors = listOf(BearerTokenInterceptor(this))
//            )
//            val wsTransport = WebSocketNetworkTransport(
//                serverUrl = serverUrl.replace("graphql", "subscriptions").replace("http", "ws"),
//
//                )
//            val netInterceptor = NetworkInterceptor(netTransport, wsTransport)
            ApolloClient(
                networkTransport = HttpNetworkTransport(
                    serverUrl = serverUrl,

                    interceptors = listOf(BearerTokenInterceptor(this))
                ),
                subscriptionNetworkTransport = WebSocketNetworkTransport(
                    serverUrl = serverUrl.replace("graphql", "subscriptions").replace("http", "ws"),

                ),
                customScalarAdapters = customScalarTypeAdapters,
                interceptors = listOf(
                    MyLoggingInterceptor(log)
                )
            )
                .withCustomScalarAdapter(UUID.type, uuidAdapter)
                .withCustomScalarAdapter(LocalDate.type, LocalDateAdapter)
                .withHttpHeaders(
                    listOf(
                        HttpHeader("Accept", "application/json"),
                        HttpHeader("Content-Type", "application/json")
                    )
                )
        }

        override suspend fun currentToken(): String = authTokenProvider.token().firstOrNull() ?: ""

        override suspend fun refreshToken(previousToken: String): String = ""

        override fun <T : Query.Data, R> query(
            queryData: Query<T>,
            mapper: (T) -> R,
        ): Flow<DataResult<R>> =
            apolloClient.queryAsFlow(ApolloRequest(queryData)).map(checkResponse(mapper))

        override fun <T : Mutation.Data, R> mutate(
            mutationData: Mutation<T>,
            mapper: (T) -> R,
        ): Flow<DataResult<R>> =
            apolloClient.mutateAsFlow(ApolloRequest(mutationData)).map(checkResponse(mapper))

        override fun <T : Mutation.Data> mutate(
            mutationData: Mutation<T>
        ): Flow<ApolloResponse<T>> {
            return apolloClient.mutateAsFlow(ApolloRequest(mutationData))
        }

        override fun <T : Subscription.Data> subscribe(
            subscriptionData: Subscription<T>
        ): Flow<ApolloResponse<T>> = apolloClient.subscribe(
            ApolloRequest(subscriptionData)
        )

        override fun <T : Subscription.Data, R> subscribe(
            subscriptionData: Subscription<T>,
            mapper: (T) -> R
        ): Flow<DataResult<R>> = apolloClient.subscribe(
            ApolloRequest(subscriptionData)
        ).map(checkResponse(mapper))

        private fun <T : Operation.Data, R> checkResponse(mapper: (T) -> R): suspend (
            ApolloResponse<T>
        ) ->
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

internal class MyLoggingInterceptor(private val log: Kermit) : ApolloInterceptor {

    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain,
    ): Flow<ApolloResponse<D>> {
        val uuid = request.requestUuid.toString()
        val operation = request.operation.name()
        val variables = request.operation.variables(customScalarTypeAdapters).valueMap.toString()
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

class NInt(
    private val networkTransport: NetworkTransport,
    private val subscriptionNetworkTransport: NetworkTransport,
    private val tokenProvider: TokenProvider
) : ApolloInterceptor {
    private val mutex = Mutex()
    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain
    ): Flow<ApolloResponse<D>> {

        return flow {
            val token = mutex.withLock { tokenProvider.currentToken() }
            when (request.operation) {
                is Query<*> -> networkTransport.execute(request = request)
                is Mutation<*> -> networkTransport.execute(request = request)
                is Subscription<*> -> subscriptionNetworkTransport.execute(
                    request = request.withHttpHeader(
                        HttpHeader("Authorization", "Bearer $token")
                    )
                )
                else -> error("")
            }
        }
    }
}
