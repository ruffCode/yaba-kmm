package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.model.Transaction

interface TransactionRepository {
    fun recentTransactions(): Flow<List<Transaction>>
    fun count(): Flow<Long>
    fun selectAll():Flow<List<Transaction>>
}

internal class TransactionRepositoryImpl : UserIdProvider(), TransactionRepository, KoinComponent {
    private val log: Kermit by inject { parametersOf("TransactionRepository") }

    private val dao: TransactionDao by inject()

    init {
        ensureNeverFrozen()
    }

    override fun recentTransactions(): Flow<List<Transaction>> {
        return dao.selectRecent(userId.value)
    }

    override fun count(): Flow<Long> {
        return dao.count(userId.value)
    }

    override fun selectAll(): Flow<List<Transaction>> {
        return dao.selectAll(userId.value)
    }
}