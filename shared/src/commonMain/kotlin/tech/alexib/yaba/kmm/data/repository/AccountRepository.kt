package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.model.Account

interface AccountRepository {
    fun getAll(): Flow<List<Account>>
    fun cashBalance(): Flow<Double>
}


internal class AccountRepositoryImpl : AccountRepository, KoinComponent {

    private val accountDao: AccountDao by inject()

    private val log: Kermit by inject { parametersOf("AccountRepository") }

    init {
        ensureNeverFrozen()
    }

    override fun getAll(): Flow<List<Account>> {
        return accountDao.selectAll()
    }

    override fun cashBalance(): Flow<Double> {
        return accountDao.availableBalance()
    }
}