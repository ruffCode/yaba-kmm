package tech.alexib.yaba.kmm.data.api

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AccountByIdQuery
import tech.alexib.yaba.AccountByIdWithTransactionQuery
import tech.alexib.yaba.AccountSetHiddenMutation
import tech.alexib.yaba.kmm.data.repository.DataResult
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success
import tech.alexib.yaba.kmm.data.repository.toDataResult
import tech.alexib.yaba.kmm.model.Account
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType
import tech.alexib.yaba.kmm.model.AccountWithTransactions
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionType

interface AccountApi {
    suspend fun setHideAccount(hide: Boolean, accountId: Uuid)
    fun accountById(id: Uuid): Flow<DataResult<Account>>
    fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactions>>
}


class AccountApiImpl : AccountApi, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("AccountApi") }

    override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
        val mutation = AccountSetHiddenMutation(id = accountId, hidden = hide)
        apolloApi.client().mutate(mutation).execute().firstOrNull()
    }

    override fun accountById(id: Uuid): Flow<DataResult<Account>> {
        val query = AccountByIdQuery(id)
        val response = apolloApi.client().safeQuery(query) {
            it.accountById.fragments.account.toAccount()
        }
        return response.toDataResult()
    }

    override fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactions>> {
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

private fun tech.alexib.yaba.fragment.Account.toAccount(): Account = Account(
    id = id as Uuid,
    name = name,
    mask = mask,
    availableBalance = availableBalance,
    currentBalance = currentBalance,
    itemId = itemId as Uuid,
    type = AccountType.valueOf(type.name),
    subtype = AccountSubtype.valueOf(subtype.name),
    hidden = hidden
)

fun tech.alexib.yaba.fragment.Transaction.toTransaction() = Transaction(
    name = name,
    id = id as Uuid,
    type = TransactionType.valueOf(type.uppercase()),
    amount = amount,
    date = date as LocalDate,
    accountId = accountId as Uuid,
    itemId = itemId as Uuid,
    category = category,
    pending = pending,
    subcategory = subcategory,
    isoCurrencyCode = subcategory
)

fun tech.alexib.yaba.fragment.AccountWithTransactions.toAccountWithTransactions() =
    AccountWithTransactions(
        account = Account(id = id as Uuid,
            name = name,
            mask = mask,
            availableBalance = availableBalance,
            currentBalance = currentBalance,
            itemId = itemId as Uuid,
            type = AccountType.valueOf(type.name),
            subtype = AccountSubtype.valueOf(subtype.name),
            hidden = hidden),
        transactions = transactions.map { it.fragments.transaction.toTransaction() }
    )