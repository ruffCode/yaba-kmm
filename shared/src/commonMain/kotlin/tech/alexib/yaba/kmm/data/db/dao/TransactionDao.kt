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
import tech.alexib.yaba.data.db.TransactionQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.sqldelight.transactionWithContext
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionDetail
import tech.alexib.yaba.kmm.model.TransactionType


internal interface TransactionDao {
    suspend fun insert(transaction: TransactionEntity)
    suspend fun insert(transactions: List<TransactionEntity>)
    fun selectAll(userId: Uuid): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<TransactionDetail>
    fun selectByAccountId(accountId: Uuid): Flow<List<TransactionDetail>>
    fun selectByItemId(itemId: Uuid): Flow<List<TransactionDetail>>
    suspend fun deleteByItemId(itemId: Uuid)
    suspend fun deleteByAccountId(accountId: Uuid)
    fun count(userId: Uuid): Flow<Long>
    fun selectRecent(userId: Uuid): Flow<List<Transaction>>
}

internal class TransactionDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher,
) : TransactionDao, KoinComponent {
    private val queries: TransactionQueries = database.transactionQueries

    private val log: Kermit by inject { parametersOf("TransactionDao") }

    init {
        ensureNeverFrozen()

    }

    override suspend fun insert(transactions: List<TransactionEntity>) {
        database.transactionWithContext(backgroundDispatcher) {
            transactions.forEach {
                queries.insert(it)
            }
        }
    }

    override suspend fun insert(transaction: TransactionEntity) {
        withContext(backgroundDispatcher) {
            queries.insert(
                transaction
            )
        }
    }

    override fun selectAll(userId: Uuid): Flow<List<Transaction>> {
        return queries.selectAll(userId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun selectById(id: Uuid): Flow<TransactionDetail> {
        return queries.selectById(id, transactionDetailMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)
    }

    override fun selectByAccountId(accountId: Uuid): Flow<List<TransactionDetail>> {
        return queries.selectByAccontId(accountId, transactionDetailMapper).asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun selectByItemId(itemId: Uuid): Flow<List<TransactionDetail>> {
        return queries.selectByItemId(itemId, transactionDetailMapper).asFlow().mapToList()
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


    private val transactionMapper = {
            id: Uuid,
            account_id: Uuid,
            item_id: Uuid,
            _: Uuid?,
            category: String?,
            subcategory: String?,
            type: TransactionType,
            name: String,
            merchant_name: String?,
            date: LocalDate,
            amount: Double,
            iso_currency_code: String?,
            pending: Boolean?,
        ->
        Transaction(
            id = id,
            accountId = account_id,
            name = name,
            type = type,
            amount = amount,
            date = date,
            category = category,
            subcategory = subcategory,
            isoCurrencyCode = iso_currency_code,
            pending = pending ?: false,
            merchantName = merchant_name,
        )
    }

    //  t.id,
//    t.account_id,
//    t.item_id,
//    a.user_id,
//    t.category,
//    t.subcategory,
//    t.type,
//    t.name,
//    t.iso_currency_code,
//    t.date,
//    t.amount,
//    t.pending,
//    t.merchant_name,
//    a.name AS accountName,
//    a.mask,
//    a.institutionName
    private val transactionDetailMapper =
        { id: Uuid,
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
          merchant_name: String?,
          accountName: String?,
          mask: String?,
          institutionName: String?
            ->
            TransactionDetail(
                id = id,
                accountId = account_id,
                name = name,
                type = type,
                amount = amount,
                date = date,
                category = category,
                subcategory = subcategory,
                isoCurrencyCode = iso_currency_code,
                pending = pending,
                merchantName = merchant_name,
                accountName = accountName ?: "Unknown",
                accountMask = mask ?: "0000",
                institutionName = institutionName ?: "unknown"
            )
        }

}