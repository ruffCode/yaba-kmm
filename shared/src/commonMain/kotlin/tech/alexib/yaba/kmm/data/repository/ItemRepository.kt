package tech.alexib.yaba.kmm.data.repository

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.data.db.dao.ItemDao
import tech.alexib.yaba.kmm.model.PlaidItem
import tech.alexib.yaba.kmm.model.PlaidItemWIthAccounts

interface ItemRepository {
    fun getAll(): Flow<List<PlaidItem>>
    fun getById(id: Uuid): Flow<PlaidItem>
    fun getAllWithAccounts(): Flow<List<PlaidItemWIthAccounts>>
    suspend fun unlinkItem(id: Uuid)

}


internal class ItemRepositoryImpl : UserIdProvider(), ItemRepository, KoinComponent {
    private val itemDao: ItemDao by inject()
    private val accountDao: AccountDao by inject()
    private val plaidItemApi: PlaidItemApi by inject()

    init {
        ensureNeverFrozen()
    }

    override fun getAll(): Flow<List<PlaidItem>> = itemDao.selectAll(userId.value)

    override fun getById(id: Uuid): Flow<PlaidItem> = itemDao.selectById(id)

    override fun getAllWithAccounts(): Flow<List<PlaidItemWIthAccounts>> {
        return combine(getAll(), accountDao.selectAll(userId.value)) { items, accounts ->
            items.map {
                PlaidItemWIthAccounts(
                    it,
                    accounts.filter { account -> account.itemId == it.id }
                )
            }
        }
    }

    override suspend fun unlinkItem(id: Uuid) {
        plaidItemApi.unlink(id)
        itemDao.deleteById(id)
    }
}