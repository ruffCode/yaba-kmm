package tech.alexib.yaba.kmm.data

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AllUserDataQuery
import tech.alexib.yaba.kmm.data.api.ApolloApi
import tech.alexib.yaba.kmm.data.api.ApolloResponse
import tech.alexib.yaba.kmm.data.api.dto.AccountDto
import tech.alexib.yaba.kmm.data.api.dto.ItemDto
import tech.alexib.yaba.kmm.data.api.dto.TransactionDto
import tech.alexib.yaba.kmm.data.api.dto.toDto
import tech.alexib.yaba.kmm.data.api.dto.toEntities
import tech.alexib.yaba.kmm.data.api.safeQuery
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.data.db.dao.InstitutionDao
import tech.alexib.yaba.kmm.data.db.dao.ItemDao
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.data.db.dao.UserDao
import tech.alexib.yaba.kmm.data.repository.UserRepository
import tech.alexib.yaba.kmm.model.Institution
import tech.alexib.yaba.kmm.model.User

interface Initializer {
    suspend fun init()
}

class InitializerImpl : Initializer, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("Initializer") }
    private val accountDao: AccountDao by inject()
    private val institutionDao: InstitutionDao by inject()
    private val itemDao: ItemDao by inject()
    private val transactionDao: TransactionDao by inject()
    private val userDao: UserDao by inject()
    private val userRepository: UserRepository by inject()

    init {
        ensureNeverFrozen()
    }

    override suspend fun init() {
        if (userRepository.currentUser().firstOrNull() == null) {
            val response = apolloApi.client().safeQuery(AllUserDataQuery()) {
                val data = it.me
                val userId = data.id as Uuid
                val user = User(userId, data.email)

                val transactions =
                    data.transactions.map { transaction -> transaction.fragments.transaction.toDto() }

                val institutions = data.items.map { item ->
                    with(item.institution) {
                        Institution(
                            institutionId = institutionId,
                            name = name,
                            logo = logo,
                            primaryColor = primaryColor ?: "#095aa6"
                        )
                    }
                }
                val items = data.items.map { item ->
                    ItemDto(
                        id = item.id as Uuid,
                        plaidInstitutionId = item.plaidInstitutionId,
                        userId = userId
                    )
                }

                val accounts = data.accounts.map { account -> account.fragments.account.toDto() }

                AllDataMappedResponse(
                    user, transactions, items, accounts, institutions
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

            userDao.insert(data.user)
            data.institutions.forEach {
                institutionDao.insert(it)
            }

            itemDao.insert(data.items.toEntities())

            accountDao.insert(data.accounts.toEntities())

            transactionDao.insert(data.transactions.toEntities())

        }.fold({
            log.d { "User data inserted" }
        }, {
            log.e(it) {
                "Error inserting user data: ${it.message}"
            }
        })
    }
}

private data class AllDataMappedResponse(
    val user: User,
    val transactions: List<TransactionDto>,
    val items: List<ItemDto>,
    val accounts: List<AccountDto>,
    val institutions: List<Institution>
)