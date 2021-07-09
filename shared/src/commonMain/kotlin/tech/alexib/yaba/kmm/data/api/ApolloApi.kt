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
package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.ScalarTypeAdapters
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.apollographql.apollo.interceptor.ApolloRequest
import com.apollographql.apollo.interceptor.ApolloRequestInterceptor
import com.apollographql.apollo.interceptor.BearerTokenInterceptor
import com.apollographql.apollo.interceptor.TokenProvider
import com.apollographql.apollo.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo.network.ws.ApolloWebSocketFactory
import com.apollographql.apollo.network.ws.ApolloWebSocketNetworkTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.type.CustomType
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

internal class ApolloApi(
    serverUrl: String,
    log: Kermit,
) : KoinComponent, TokenProvider {

    private val appSettings: AppSettings by inject()

    @Suppress("CanBePrimaryConstructorProperty")
    private val log = log

    init {
        ensureNeverFrozen()
    }

    private val apolloClient: ApolloClient = ApolloClient(
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
        scalarTypeAdapters = ScalarTypeAdapters(
            mapOf(
                CustomType.UUID to uuidAdapter,
                CustomType.ID to uuidAdapter,
                CustomType.LOCALDATE to localDateAdapter
            )
        ),
        interceptors = listOf(BearerTokenInterceptor(this), LoggingInterceptor(log))
    )

    fun client() = apolloClient

    override suspend fun currentToken(): String = appSettings.token().firstOrNull() ?: ""

    override suspend fun refreshToken(previousToken: String): String = ""
}

internal class LoggingInterceptor(private val log: Kermit) : ApolloRequestInterceptor {
    @ExperimentalTime
    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain,
    ): Flow<com.apollographql.apollo.interceptor.ApolloResponse<D>> {
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
// private val catchApolloError: suspend FlowCollector<ApolloResponse.Error>.(cause: Throwable) -> Unit =
//    { exception ->
//        exception.message?.let {
//            emit(ApolloResponse.Error(listOf(it)))
//        }
//        emit(ApolloResponse.Error(listOf()))
//    }

fun <T : Operation.Data, R> ApolloClient.safeQuery(
    queryData: Query<T, T, Operation.Variables>,
    mapper: (T) -> R,
): Flow<ApolloResponse<R>> = this.query(queryData).execute().map(checkResponse(mapper))

//    .catch(
//    catchApolloError
// )

fun <T : Operation.Data> ApolloClient.safeQuery(
    queryData: Query<T, T, Operation.Variables>,
): Flow<ApolloResponse<T>> = this.query(queryData).execute().map(checkResponse())

//    .catch(
//    catchApolloError
// )

fun <T : Operation.Data, R> ApolloClient.safeMutation(
    mutationData: Mutation<T, T, Operation.Variables>,
    mapper: (T) -> R,
): Flow<ApolloResponse<R>> =
    this.mutate(mutationData).execute().map(checkResponse(mapper))

//        .catch(catchApolloError)

fun <T : Operation.Data> ApolloClient.safeMutation(
    mutationData: Mutation<T, T, Operation.Variables>,
): Flow<ApolloResponse<T>> =
    this.mutate(mutationData).execute().map(checkResponse())
//        .catch(catchApolloError)

private fun <T, R> checkResponse(mapper: (T) -> R): suspend (Response<T>) ->
ApolloResponse<R> {
    return {
        ApolloResponse(it, mapper)
    }
}

private fun <T> checkResponse(): suspend (Response<T>) -> ApolloResponse<T> {
    return { ApolloResponse(it) }
}
