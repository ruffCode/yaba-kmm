package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
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
import tech.alexib.yaba.SetAccountsToHideMutation
import tech.alexib.yaba.UnlinkItemMutation
import tech.alexib.yaba.kmm.data.repository.DataResult
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.kmm.model.request.PlaidLinkEventCreateRequest
import tech.alexib.yaba.kmm.model.response.CreateLinkTokenResponse
import tech.alexib.yaba.kmm.model.response.PlaidItemCreateResponse

interface PlaidItemApi {
    fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>>
    fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<PlaidItemCreateResponse>>
    fun sendLinkEvent(request: PlaidLinkEventCreateRequest)
    fun setAccountsToHide(itemId: Uuid, plaidAccountIds: List<String>)
    suspend fun unlink(itemId: Uuid)
}

class PlaidItemApiImpl : PlaidItemApi, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("PlaidItemApi") }

    init {
        ensureNeverFrozen()
    }

    override fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>> {
        val mutation = CreateLinkTokenMutation()
        return runCatching {

            flow<DataResult<CreateLinkTokenResponse>> {

                when (val result =
                    apolloApi.client()
                        .safeMutation(mutation) { result -> CreateLinkTokenResponse(result.createLinkToken.linkToken) }
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

    override fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<PlaidItemCreateResponse>> {
        val mutation = CreateItemMutation(request.institutionId, request.publicToken)

        return runCatching {

            flow<DataResult<PlaidItemCreateResponse>> {
                val result = apolloApi.client().safeMutation(mutation) { result ->
                    result.itemCreate.let {
                        PlaidItemCreateResponse(
                            id = it.itemId as Uuid,
                            name = it.name,
                            logo = it.logo,
                            accounts = it.accounts.map { account ->
                                PlaidItemCreateResponse.Account(
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
                apolloApi.client().mutate(request.toMutation()).execute().firstOrNull()
            }.getOrElse {
                log.e { "error sending link event ${it.message}" }
            }
        }
    }

    override fun setAccountsToHide(itemId: Uuid, plaidAccountIds: List<String>) {
        val mutation = SetAccountsToHideMutation(itemId, plaidAccountIds)

        CoroutineScope(Dispatchers.Default).launch {
            runCatching {
                apolloApi.client().mutate(mutation).execute().firstOrNull()
            }.getOrElse {
                log.e { "error setting accounts to hide ${it.message}" }
            }
        }
    }

    override suspend fun unlink(itemId: Uuid) {
        val mutation = UnlinkItemMutation(itemId)
        CoroutineScope(Dispatchers.Default).launch {
            runCatching {
                apolloApi.client().mutate(mutation).execute().firstOrNull()
            }.getOrElse {
                log.e { "error unlinking item ${it.message}" }
            }
        }
    }
}


