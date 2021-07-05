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
import tech.alexib.yaba.data.db.ItemEntity
import tech.alexib.yaba.data.db.ItemEntityQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.sqldelight.transactionWithContext
import tech.alexib.yaba.kmm.model.PlaidItem


internal interface ItemDao {
    suspend fun insert(item: ItemEntity)
    suspend fun insert(items: List<ItemEntity>)
    fun selectAll(userId: Uuid): Flow<List<PlaidItem>>
    fun selectById(id: Uuid): Flow<PlaidItem>
    suspend fun deleteById(id: Uuid)
}


internal class ItemDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher,
) : ItemDao {
    private val queries: ItemEntityQueries = database.itemEntityQueries

    init {
        ensureNeverFrozen()

    }

    override suspend fun insert(items: List<ItemEntity>) {
        database.transactionWithContext(backgroundDispatcher) {
            items.forEach {
                queries.insert(it)
            }
        }
    }

    override suspend fun insert(item: ItemEntity) {
        withContext(backgroundDispatcher) {
            queries.insert(
                item
            )
        }
    }

    override fun selectAll(userId: Uuid): Flow<List<PlaidItem>> {
        return queries.selectAll(userId, itemMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override fun selectById(id: Uuid): Flow<PlaidItem> {
        return queries.selectById(id, itemMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun deleteById(id: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteById(id)
        }
    }

    private val itemMapper = {
            id: Uuid,
            plaid_institution_id: String,
            _: Uuid?,
            name: String,
            logo: String,
        ->
        PlaidItem(
            id = id,
            plaidInstitutionId = plaid_institution_id,
            name = name,
            base64Logo = logo
        )
    }
}