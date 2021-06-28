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
import tech.alexib.yaba.kmm.model.PlaidInstitutionId
import tech.alexib.yaba.kmm.model.PlaidItem
import tech.alexib.yaba.kmm.model.PlaidItemId

internal interface ItemDao {
    suspend fun insert(item: ItemEntity)
    suspend fun insert(items: List<ItemEntity>)
    suspend fun selectAll(userId: Uuid): Flow<List<PlaidItem>>
    suspend fun selectById(id: Uuid): Flow<PlaidItem>
}


internal class ItemDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
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

    override suspend fun selectAll(userId: Uuid): Flow<List<PlaidItem>> {
        return queries.selectAll(userId, itemMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectById(id: Uuid): Flow<PlaidItem> {
        return queries.selectById(id, itemMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)
    }

    companion object {
        private val itemMapper = {
                id: Uuid,
                plaid_institution_id: String,
                _: Uuid?,
                name: String,
                logo: String,
            ->
            PlaidItem(
                id = PlaidItemId(id),
                plaidInstitutionId = PlaidInstitutionId(plaid_institution_id),
                name = name,
                base64Logo = logo
            )
        }
    }
}