package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.ScalarTypeAdapters
import com.apollographql.apollo.network.http.ApolloHttpNetworkTransport
import com.apollographql.apollo.network.ws.ApolloWebSocketFactory
import com.apollographql.apollo.network.ws.ApolloWebSocketNetworkTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.alexib.yaba.type.CustomType


class ApolloApi(
    private val serverUrl: String,
    log:Kermit
) {

    @Suppress("CanBePrimaryConstructorProperty")
    private val log = log
    init {
        ensureNeverFrozen()
    }


    fun client(authToken: String? = null): ApolloClient {

        val headers = mapOf(
            "Accept" to "application/json",
            "Content-Type" to "application/json",
        ).apply {
            authToken?.let {
                "Authentication" to "Bearer $it"
            }
        }
        return ApolloClient(
            networkTransport = ApolloHttpNetworkTransport(
                serverUrl = serverUrl,
                headers = headers,
            ),

            subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
                webSocketFactory = ApolloWebSocketFactory(
                    serverUrl = serverUrl,
                    headers
                )
            ),
            scalarTypeAdapters = ScalarTypeAdapters(
                mapOf(
                    CustomType.UUID to uuidAdapter,
                    CustomType.ID to uuidAdapter,
//                    CustomType.LOCALDATE to localDateAdapter
                )
            )
        )
    }

    private fun webSocketUrl(): String {
        val isHttps = serverUrl.startsWith("https", ignoreCase = true)
        val url = serverUrl.substringAfter("://")
        return if (isHttps) "wss://$url" else "ws://$url"
    }
}

//private val catchApolloError: suspend FlowCollector<ApolloResponse.Error>.(cause: Throwable) -> Unit =
//    { exception ->
//        exception.message?.let {
//            emit(ApolloResponse.Error(listOf(it)))
//        }
//        emit(ApolloResponse.Error(listOf()))
//    }


fun <T : Operation.Data, R> ApolloClient.safeQuery(
    queryData: Query<T, T, Operation.Variables>,
    mapper: (T) -> R
): Flow<ApolloResponse<R>> = this.query(queryData).execute().map(checkResponse(mapper))

//    .catch(
//    catchApolloError
//)

fun <T : Operation.Data> ApolloClient.safeQuery(
    queryData: Query<T, T, Operation.Variables>,
): Flow<ApolloResponse<T>> = this.query(queryData).execute().map(checkResponse())

//    .catch(
//    catchApolloError
//)

fun <T : Operation.Data, R> ApolloClient.safeMutation(
    mutationData: Mutation<T, T, Operation.Variables>,
    mapper: (T) -> R,
): Flow<ApolloResponse<R>> =
    this.mutate(mutationData).execute().map(checkResponse(mapper))

//        .catch(catchApolloError)


fun <T : Operation.Data> ApolloClient.safeMutation(
    mutationData: Mutation<T, T, Operation.Variables>
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