package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionDetail

interface TransactionRepository {
    fun recentTransactions(): Flow<List<Transaction>>
    fun count(): Flow<Long>
    fun selectAll(): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<TransactionDetail>
}

internal class TransactionRepositoryImpl : TransactionRepository, KoinComponent {
    private val log: Kermit by inject { parametersOf("TransactionRepository") }
    private val userIdProvider: UserIdProvider by inject()
    private val dao: TransactionDao by inject()

    init {
        ensureNeverFrozen()
    }

    override fun recentTransactions(): Flow<List<Transaction>> = flow {
        emitAll(dao.selectRecent(userIdProvider.userId.value))
    }


    override fun count(): Flow<Long> = flow {
        emitAll(dao.count(userIdProvider.userId.value))
    }

    override fun selectAll(): Flow<List<Transaction>> =
        flow { emitAll(dao.selectAll(userIdProvider.userId.value)) }

    override fun selectById(id: Uuid): Flow<TransactionDetail> = flow {
        emitAll(dao.selectById(id))
    }
}