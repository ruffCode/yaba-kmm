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

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.api.variables
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.http.BearerTokenInterceptor
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.http.TokenProvider
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.data.settings.AuthSettings
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

internal class ApolloApi(
    serverUrl: String,
    log: Kermit,
) : KoinComponent, TokenProvider {

    private val authSettings: AuthSettings by inject()

    @Suppress("CanBePrimaryConstructorProperty")
    private val log = log

    init {
        ensureNeverFrozen()
    }

    private val apolloClient: ApolloClient = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = serverUrl,
            headers = mutableMapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json",
            ),
            interceptors = listOf(BearerTokenInterceptor(this))
        ),
        subscriptionNetworkTransport = WebSocketNetworkTransport(
            serverUrl = serverUrl,

        ),
        customScalarAdapters = customScalarTypeAdapters,
        interceptors = listOf(
            MyLoggingInterceptor(log)
        )
    )

    fun client() = apolloClient

    override suspend fun currentToken(): String = authSettings.token().firstOrNull() ?: ""

    override suspend fun refreshToken(previousToken: String): String = ""
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

// private val catchApolloError: suspend FlowCollector<ApolloResponse.Error>.(cause: Throwable) -> Unit =
//    { exception ->
//        exception.message?.let {
//            emit(ApolloResponse.Error(listOf(it)))
//        }
//        emit(ApolloResponse.Error(listOf()))
//    }

fun <T : Query.Data, R> ApolloClient.safeQuery(
    queryData: Query<T>,
    mapper: (T) -> R,
): Flow<YabaApolloResponse<R>> = this.queryAsFlow(ApolloRequest(queryData)).map(checkResponse(mapper))

//    .catch(
//    catchApolloError
// )

fun <T : Query.Data> ApolloClient.safeQuery(
    queryData: Query<T>,
): Flow<YabaApolloResponse<T>> = this.queryAsFlow(ApolloRequest(queryData)).map(checkResponse())

//    .catch(
//    catchApolloError
// )

fun <T : Mutation.Data, R> ApolloClient.safeMutation(
    mutationData: Mutation<T>,
    mapper: (T) -> R,
): Flow<YabaApolloResponse<R>> =
    this.mutateAsFlow(ApolloRequest(mutationData)).map(checkResponse(mapper))

//        .catch(catchApolloError)

fun <T : Mutation.Data> ApolloClient.safeMutation(
    mutationData: Mutation<T>,
): Flow<YabaApolloResponse<T>> =
    this.mutateAsFlow(ApolloRequest(mutationData)).map(checkResponse())
//        .catch(catchApolloError)

private fun <T : Operation.Data, R> checkResponse(mapper: (T) -> R): suspend (ApolloResponse<T>) ->
YabaApolloResponse<R> {
    return {
        YabaApolloResponse(it, mapper)
    }
}

private fun <T : Operation.Data> checkResponse(): suspend (ApolloResponse<T>) -> YabaApolloResponse<T> {
    return { YabaApolloResponse(it) }
}
