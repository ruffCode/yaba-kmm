package tech.alexib.yaba.kmm.data

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AllUserDataQuery
import tech.alexib.yaba.data.db.ItemEntity
import tech.alexib.yaba.kmm.data.api.ApolloApi
import tech.alexib.yaba.kmm.data.api.ApolloResponse
import tech.alexib.yaba.kmm.data.api.safeQuery
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.data.db.dao.InstitutionDao
import tech.alexib.yaba.kmm.data.db.dao.ItemDao
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.model.Account
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType
import tech.alexib.yaba.kmm.model.Institution
import tech.alexib.yaba.kmm.model.PlaidInstitutionId
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionType

interface Initializer {
    suspend fun init()
}

class InitializerImpl(
    apolloApi: ApolloApi
) : Initializer, KoinComponent {

    private val log: Kermit by inject { parametersOf("Initializer") }
    private val accountDao: AccountDao by inject()
    private val institutionDao: InstitutionDao by inject()
    private val itemDao: ItemDao by inject()
    private val transactionDao: TransactionDao by inject()

    init {
        ensureNeverFrozen()
    }

    private val client by lazy { apolloApi.client() }

    override suspend fun init() {
        if (transactionDao.count().first() == 0L) {
            val response = client.safeQuery(AllUserDataQuery()) {
                val data = it.me

                val transactions = data.transactions.map { transaction ->
                    with(transaction) {
                        Transaction(
                            id = id as Uuid,
                            type = TransactionType.valueOf(type.uppercase()),
                            amount = amount,
                            date = date as LocalDate,
                            accountId = accountId as Uuid,
                            itemId = itemId as Uuid,
                            category = category,
                            subcategory = subcategory,
                            isoCurrencyCode = isoCurrencyCode,
                            pending = pending,
                            name = name
                        )
                    }
                }
                val institutions = data.items.map { item ->
                    with(item.institution) {
                        Institution(
                            institutionId = PlaidInstitutionId(institutionId),
                            name = name,
                            logo = logo,
                            primaryColor = primaryColor ?: "#095aa6"
                        )
                    }
                }
                val items = data.items.map { item ->
                    ItemEntity(
                        id = item.id as Uuid,
                        plaid_institution_id = item.plaidInstitutionId,
                    )
                }

                val accounts = data.accounts.map { account ->
                    with(account) {
                        Account(
                            id = id as Uuid,
                            name = name,
                            currentBalance = currentBalance,
                            availableBalance = availableBalance,
                            mask = mask,
                            itemId = itemId as Uuid,
                            type = AccountType.valueOf(type.name),
                            subtype = AccountSubtype.valueOf(subtype.name),
                            hidden = hidden
                        )
                    }
                }
                AllDataMappedResponse(
                    transactions, items, accounts, institutions
                )
            }.first()

            when (response) {
                is ApolloResponse.Success -> {

                    insertAllUserData(response.data)
                }
                is ApolloResponse.Error -> {

                    log.e { "Error retrieving user data: ${response.message}" }
                }
            }
        }
    }

    private suspend fun insertAllUserData(data: AllDataMappedResponse) {

        runCatching {

            data.institutions.forEach {
                institutionDao.insert(it)
            }
            data.items.forEach {
                itemDao.insert(it)
            }
            accountDao.insert(data.accounts)

            data.transactions.forEach {
                transactionDao.insert(it)
            }
        }.getOrElse {
            log.e { "Error inserting user data: ${it.message}" }
        }
    }
}

private data class AllDataMappedResponse(
    val transactions: List<Transaction>,
    val items: List<ItemEntity>,
    val accounts: List<Account>,
    val institutions: List<Institution>
)