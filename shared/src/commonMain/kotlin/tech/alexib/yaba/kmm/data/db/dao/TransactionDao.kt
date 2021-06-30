package tech.alexib.yaba.kmm.data.db.dao

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.data.db.TransactionsQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.sqldelight.transactionWithContext
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionType

internal interface TransactionDao {
    suspend fun insert(transaction: Transaction)
    suspend fun insert(transactions: List<Transaction>)
    fun selectAll(userId: Uuid): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<Transaction>
    fun selectByAccountId(accountId: Uuid): Flow<List<Transaction>>
    fun selectByItemId(itemId: Uuid): Flow<List<Transaction>>
    suspend fun deleteByItemId(itemId: Uuid)
    suspend fun deleteByAccountId(accountId: Uuid)
    fun count(userId: Uuid): Flow<Long>
    fun selectRecent(userId: Uuid): Flow<List<Transaction>>
}

internal class TransactionDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher,
) : TransactionDao, KoinComponent {
    private val queries: TransactionsQueries = database.transactionsQueries

    private val log: Kermit by inject { parametersOf("TransactionDao") }

    init {
        ensureNeverFrozen()

    }

    override suspend fun insert(transactions: List<Transaction>) {
        database.transactionWithContext(backgroundDispatcher) {
            transactions.forEach {
                queries.insert(it.toEntity())
            }
        }
    }

    override suspend fun insert(transaction: Transaction) {
        withContext(backgroundDispatcher) {
            queries.insert(
                transaction.toEntity()
            )
        }
    }

    override fun selectAll(userId: Uuid): Flow<List<Transaction>> {
        return queries.selectAll(userId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun selectById(id: Uuid): Flow<Transaction> {
        return queries.selectById(id, transactionMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)
    }

    override fun selectByAccountId(accountId: Uuid): Flow<List<Transaction>> {
        return queries.selectByAccontId(accountId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun selectByItemId(itemId: Uuid): Flow<List<Transaction>> {
        return queries.selectByItemId(itemId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun deleteByItemId(itemId: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteByItemId(itemId)
        }
    }

    override fun selectRecent(userId: Uuid): Flow<List<Transaction>> {
        return queries.selectRecent(userId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun count(userId: Uuid): Flow<Long> {
        return queries.count(userId).asFlow().mapToOne().flowOn(backgroundDispatcher)
    }

    override suspend fun deleteByAccountId(accountId: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteByAccontId(accountId)
        }
    }

    companion object {
        private val transactionMapper = {
                id: Uuid,
                account_id: Uuid,
                item_id: Uuid,
                _: Uuid?,
                category: String?,
                subcategory: String?,
                type: TransactionType,
                name: String,
                iso_currency_code: String?,
                date: LocalDate,
                amount: Double,
                pending: Boolean?,
            ->
            Transaction(
                id = id,
                name = name,
                type = type,
                amount = amount,
                date = date,
                accountId = account_id,
                itemId = item_id,
                category = category,
                subcategory = subcategory,
                isoCurrencyCode = iso_currency_code,
                pending = pending
            )
        }
    }

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        name = name,
        type = type,
        amount = amount,
        date = date,
        account_id = accountId,
        item_id = itemId,
        category = category,
        subcategory = subcategory,
        iso_currency_code = isoCurrencyCode,
        pending = pending
    )
}