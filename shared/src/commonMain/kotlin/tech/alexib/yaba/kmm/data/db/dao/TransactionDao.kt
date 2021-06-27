package tech.alexib.yaba.kmm.data.db.dao

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
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.data.db.TransactionsQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionType

internal interface TransactionDao {
    suspend fun insert(transaction: Transaction)
    fun selectAll(): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<Transaction>
    fun selectByAccountId(accountId: Uuid): Flow<List<Transaction>>
    fun selectByItemId(itemId: Uuid): Flow<List<Transaction>>
    suspend fun deleteByItemId(itemId: Uuid)
    fun count(): Flow<Long>
    fun selectRecent(): Flow<List<Transaction>>
}

internal class TransactionDaoImpl(
    database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) : TransactionDao {
    private val queries: TransactionsQueries = database.transactionsQueries

    init {
        ensureNeverFrozen()

    }

    override suspend fun insert(transaction: Transaction) {
        withContext(backgroundDispatcher) {
            queries.insert(
                TransactionEntity(
                    id = transaction.id,
                    name = transaction.name,
                    type = transaction.type,
                    amount = transaction.amount,
                    date = transaction.date,
                    account_id = transaction.accountId,
                    item_id = transaction.itemId,
                    category = transaction.category,
                    subcategory = transaction.subcategory,
                    iso_currency_code = transaction.isoCurrencyCode,
                    pending = transaction.pending
                )
            )
        }
    }

    override fun selectAll(): Flow<List<Transaction>> {
        return queries.selectAll(transactionMapper).asFlow().mapToList()
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

    override fun selectRecent(): Flow<List<Transaction>> {
        return queries.selectRecent(transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun count(): Flow<Long> {
        return queries.count().asFlow().mapToOne().flowOn(backgroundDispatcher)
    }

    companion object {
        private val transactionMapper = {
                id: Uuid,
                name: String,
                type: TransactionType,
                amount: Double,
                date: LocalDate,
                account_id: Uuid,
                item_id: Uuid,
                category: String?,
                subcategory: String?,
                iso_currency_code: String?,
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
}