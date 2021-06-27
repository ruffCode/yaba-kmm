package tech.alexib.yaba.kmm.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.sqldelight.transactionWithContext
import tech.alexib.yaba.kmm.model.Account
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType

internal interface AccountDao {
    suspend fun insert(account: Account)
    suspend fun insert(accounts: List<Account>)
    suspend fun selectAll(): Flow<List<Account>>
    suspend fun selectById(accountId: Uuid): Flow<Account?>
    suspend fun selectAllByItemId(itemId: Uuid): Flow<List<Account>>
//    suspend fun selectDetailByItemId(itemId: Uuid):Flow<Account>
}

internal class AccountDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) : AccountDao {
    private val accountQueries = database.accountsQueries

    init {
        ensureNeverFrozen()

    }

    override suspend fun insert(account: Account) {
        withContext(backgroundDispatcher) {
            accountQueries.insertAccount(
                account.toEntity()
            )
        }

    }

    override suspend fun insert(accounts: List<Account>) {

        database.transactionWithContext(backgroundDispatcher) {
            accounts.forEach {
                accountQueries.insertAccount(it.toEntity())
            }
        }

    }

    override suspend fun selectAll(): Flow<List<Account>> {
        return accountQueries.selectAll(accountMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectById(accountId: Uuid): Flow<Account?> {
        return accountQueries.selectById(accountId, accountMapper).asFlow()
            .mapToOneOrNull()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectAllByItemId(itemId: Uuid): Flow<List<Account>> {
        return accountQueries.selectAllByItemId(itemId, accountMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    companion object {
        private val accountMapper = {
                id: Uuid,
                name: String,
                mask: String,
                available_balance: Double,
                current_balance: Double,
                item_id: Uuid,
                type: AccountType,
                subtype: AccountSubtype,
                hidden: Boolean,

            ->
            Account(
                id = id,
                name = name,
                currentBalance = current_balance,
                availableBalance = available_balance,
                mask = mask,
                itemId = item_id,
                type = type,
                subtype = subtype,
                hidden = hidden
            )
        }
    }

    private fun Account.toEntity(): AccountEntity = AccountEntity(
        id = id,
        name = name,
        current_balance = currentBalance,
        available_balance = availableBalance,
        mask = mask,
        item_id = itemId,
        type = type,
        subtype = subtype,
        hidden = hidden
    )
}