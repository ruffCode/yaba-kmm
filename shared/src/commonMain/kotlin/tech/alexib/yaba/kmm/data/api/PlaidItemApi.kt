package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.CreateItemMutation
import tech.alexib.yaba.CreateLinkTokenMutation
import tech.alexib.yaba.kmm.data.repository.DataResult
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.kmm.model.request.PlaidLinkEventCreateRequest
import tech.alexib.yaba.kmm.model.response.CreateLinkTokenResponse
import tech.alexib.yaba.kmm.model.response.CreatePlaidItemResponse

interface PlaidItemApi {
    fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>>
    fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<CreatePlaidItemResponse>>
    fun sendLinkEvent(request: PlaidLinkEventCreateRequest)
}

class PlaidItemApiImpl(
    apolloApi: ApolloApi,
) : PlaidItemApi, KoinComponent {


    private val log: Kermit by inject { parametersOf("PlaidItemApi") }
    private val client: ApolloClient by lazy {
        apolloApi.client()
    }


    init {
        ensureNeverFrozen()

    }

    override fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>> {
        val mutation = CreateLinkTokenMutation()
//        sendLinkEvent(mutation)

        return runCatching {

            flow<DataResult<CreateLinkTokenResponse>> {

                when (val result =
                    client.safeMutation(mutation) { result -> CreateLinkTokenResponse(result.createLinkToken.linkToken) }
                        .first()) {
                    is ApolloResponse.Success -> emit(Success(result.data))

                    is ApolloResponse.Error -> emit(ErrorResult(result.message))
                }
            }


        }.getOrElse {
            log.e { "create token error ${it.message}" }

            throw  it
        }

    }

    override fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<CreatePlaidItemResponse>> {
        val mutation = CreateItemMutation(request.institutionId, request.publicToken)

        return runCatching {

            flow<DataResult<CreatePlaidItemResponse>> {
                val result = client.safeMutation(mutation) { result ->
                    result.itemCreate.let {
                        CreatePlaidItemResponse(
                            name = it.name,
                            accounts = it.accounts.map { account ->
                                CreatePlaidItemResponse.Account(
                                    mask = account.mask,
                                    plaidAccountId = account.plaidAccountId,
                                    name = account.name
                                )
                            }
                        )
                    }
                }.first()

                when (result) {
                    is ApolloResponse.Success -> emit(Success(result.data))

                    is ApolloResponse.Error -> emit(ErrorResult(result.message))
                }
            }


        }.getOrElse {
            log.e { "Plaid item create error ${it.message}" }
            throw it
        }
    }

    override fun sendLinkEvent(request: PlaidLinkEventCreateRequest) {

        CoroutineScope(Dispatchers.Default).launch {
            runCatching {
                client.mutate(request.toMutation()).execute().firstOrNull()
            }.getOrElse {
                log.e { "error sending link event ${it.message}" }
            }
        }

    }
}


