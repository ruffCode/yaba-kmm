package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.api.AccountApi
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.model.Account

interface AccountRepository {
    fun getAll(): Flow<List<Account>>
    fun availableCashBalance(): Flow<Double>
    fun currentCashBalance(): Flow<Double>
    suspend fun hide(accountId: Uuid)
    suspend fun show(accountId: Uuid)
}


internal class AccountRepositoryImpl : UserIdProvider(), AccountRepository, KoinComponent {

    private val accountDao: AccountDao by inject()
    private val accountApi: AccountApi by inject()
    private val transactionDao: TransactionDao by inject()

    private val log: Kermit by inject { parametersOf("AccountRepository") }

    init {
        ensureNeverFrozen()
    }

    override fun getAll(): Flow<List<Account>> {
        return accountDao.selectAll(userId.value)
    }

    override fun availableCashBalance(): Flow<Double> {
        return accountDao.availableBalance(userId.value)
    }

    override fun currentCashBalance(): Flow<Double> {
        return accountDao.currentBalance(userId.value)
    }

    override suspend fun hide(accountId: Uuid) {
        accountApi.setHideAccount(true, accountId)
        accountDao.setHidden(accountId, true)
        transactionDao.deleteByAccountId(accountId)
    }

    override suspend fun show(accountId: Uuid) {
        accountApi.setHideAccount(false, accountId)

        when (val result = accountApi.accountByIdWithTransactions(accountId).firstOrNull()) {
            is Success -> {
                accountDao.setHidden(accountId, false)
                accountDao.insert(result.data.account)
                transactionDao.insert(result.data.transactions)
            }
            is ErrorResult -> {
                log.e { "error retrieving account and transactions ${result.error}" }
            }
            null -> log.e { "result was null $accountId" }
        }
    }
}