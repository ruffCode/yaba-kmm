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
import tech.alexib.yaba.kmm.di.ApolloUrl
import tech.alexib.yaba.type.CustomType
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


internal class Apollo : TokenProvider, KoinComponent {

    override suspend fun currentToken(): String {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(previousToken: String): String = ""
}

internal class ApolloApi(
    private val serverUrl: ApolloUrl,
    log: Kermit,
) : KoinComponent, TokenProvider {

    private val appSettings: AppSettings by inject()
//    private val ioDispatcher: CoroutineDispatcher by inject()
//    private val authToken = MutableStateFlow<String?>(null)

    @Suppress("CanBePrimaryConstructorProperty")
    private val log = log

    init {
        ensureNeverFrozen()
    }

    private val apolloClient: ApolloClient = ApolloClient(
        networkTransport = ApolloHttpNetworkTransport(
            serverUrl = serverUrl.value,
            headers = mutableMapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json",
            ),
        ),

        subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
            webSocketFactory = ApolloWebSocketFactory(
                serverUrl = serverUrl.value,
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
//    fun client(): ApolloClient {
////        val token = authToken.value
////        log.d { "TOKEN is $token" }
//        val headers = mutableMapOf(
//            "Accept" to "application/json",
//            "Content-Type" to "application/json",
//        )
//
//        return ApolloClient(
//            networkTransport = ApolloHttpNetworkTransport(
//                serverUrl = serverUrl.value,
//                headers = mutableMapOf(
//                    "Accept" to "application/json",
//                    "Content-Type" to "application/json",
//                ),
//            ),
//
//            subscriptionNetworkTransport = ApolloWebSocketNetworkTransport(
//                webSocketFactory = ApolloWebSocketFactory(
//                    serverUrl = serverUrl.value,
//                    mutableMapOf(
//                        "Accept" to "application/json",
//                        "Content-Type" to "application/json",
//                    )
//                )
//            ),
//            scalarTypeAdapters = ScalarTypeAdapters(
//                mapOf(
//                    CustomType.UUID to uuidAdapter,
//                    CustomType.ID to uuidAdapter,
//                    CustomType.LOCALDATE to localDateAdapter
//                )
//            ),
//            interceptors = listOf(BearerTokenInterceptor(this), LoggingInterceptor(log))
//        )
//    }

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
//private val catchApolloError: suspend FlowCollector<ApolloResponse.Error>.(cause: Throwable) -> Unit =
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