package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AccountByIdQuery
import tech.alexib.yaba.AccountByIdWithTransactionQuery
import tech.alexib.yaba.AccountSetHiddenMutation
import tech.alexib.yaba.kmm.data.api.dto.AccountDto
import tech.alexib.yaba.kmm.data.api.dto.AccountWithTransactionsDto
import tech.alexib.yaba.kmm.data.api.dto.toAccountWithTransactions
import tech.alexib.yaba.kmm.data.api.dto.toDto
import tech.alexib.yaba.kmm.data.repository.DataResult
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.data.repository.toDataResult

internal interface AccountApi {
    suspend fun setHideAccount(hide: Boolean, accountId: Uuid)
    fun accountById(id: Uuid): Flow<DataResult<AccountDto>>
    fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>>
}

internal class AccountApiImpl : AccountApi, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("AccountApi") }

    override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
        val mutation = AccountSetHiddenMutation(id = accountId, hidden = hide)
        apolloApi.client().mutate(mutation).execute().firstOrNull()
    }

    override fun accountById(id: Uuid): Flow<DataResult<AccountDto>> {
        val query = AccountByIdQuery(id)
        val response = apolloApi.client().safeQuery(query) {
            it.accountById.fragments.account.toDto()
        }
        return response.toDataResult()
    }

    override fun accountByIdWithTransactions(id: Uuid):
        Flow<DataResult<AccountWithTransactionsDto>> {
        val query = AccountByIdWithTransactionQuery(id)

        return apolloApi.client().safeQuery(query) {
            it.accountById.fragments.accountWithTransactions.toAccountWithTransactions()
        }.map {
            when (it) {
                is ApolloResponse.Success -> Success(it.data)
                is ApolloResponse.Error -> ErrorResult(it.message)
            }
        }
    }
}
