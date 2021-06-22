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
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.getSync
import tech.alexib.yaba.type.CustomType


class ApolloApi(
    private val serverUrl: ApolloUrl,
    private val sessionManager: SessionManager,
    log: Kermit
) {

    @Suppress("CanBePrimaryConstructorProperty")
    private val log = log

    init {
        ensureNeverFrozen()
    }

    fun token(): String? = getSync { sessionManager.getToken() }

    fun client(): ApolloClient {

        val token = token()
        log.d { "TOKEN $token" }
        val headers = mutableMapOf(
            "Accept" to "application/json",
            "Content-Type" to "application/json",

            )

        if(token!=null){
            headers["Authorization"] = "Bearer $token"
        }

        return ApolloClient(
            networkTransport = ApolloHttpNetworkTransport(
                serverUrl = serverUrl.value,
                headers = headers,
            ),

            subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
                webSocketFactory = ApolloWebSocketFactory(
                    serverUrl = serverUrl.value,
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