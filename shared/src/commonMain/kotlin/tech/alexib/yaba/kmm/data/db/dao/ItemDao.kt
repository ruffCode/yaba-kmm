package tech.alexib.yaba.kmm.data.db.dao

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
import tech.alexib.yaba.kmm.model.PlaidInstitutionId
import tech.alexib.yaba.kmm.model.PlaidItem
import tech.alexib.yaba.kmm.model.PlaidItemId

internal interface ItemDao {
    suspend fun insert(item: PlaidItem)
    suspend fun selectAll(): Flow<List<PlaidItem>>
    suspend fun selectById(id: Uuid): Flow<PlaidItem>
}


internal class ItemDaoImpl(
    database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) : ItemDao {
    private val queries: ItemEntityQueries = database.itemEntityQueries

    override suspend fun insert(item: PlaidItem) {
        withContext(backgroundDispatcher) {
            queries.insert(
                ItemEntity(
                    id = item.id.value,
                    plaid_institution_id = item.plaidInstitutionId.value,
                    status = item.status
                )
            )
        }
    }

    override suspend fun selectAll(): Flow<List<PlaidItem>> {
        return queries.selectAllWithInstitution(itemMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectById(id: Uuid): Flow<PlaidItem> {
        return queries.selectByIdWithInstitution(id, itemMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)
    }

    companion object {
        private val itemMapper = { id: Uuid,
                                   plaid_institution_id: String,
                                   status: String,
                                   name: String,
                                   _: String,
                                   _: String,
                                   _: String
            ->
            PlaidItem(
                id = PlaidItemId(id),
                plaidInstitutionId = PlaidInstitutionId(plaid_institution_id),
                status = status,
                name = name
            )

        }
    }
}