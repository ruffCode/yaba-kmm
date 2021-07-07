package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.dao.UserDao
import tech.alexib.yaba.kmm.model.User
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

interface UserRepository {
    suspend fun currentUser(): Flow<User?>
    suspend fun currentUser(id: Uuid): User?
    suspend fun deleteCurrentUser()
}

internal class UserRepositoryImpl(private val userIdProvider: UserIdProvider) : UserRepository,
    KoinComponent {

    private val log: Kermit by inject { parametersOf("UserRepository") }
    private val userDao: UserDao by inject()


    init {
        ensureNeverFrozen()
    }


    @ExperimentalTime
    override suspend fun currentUser(): Flow<User?> {
        return flow {
            withTimeout(5.toDuration(DurationUnit.SECONDS)) {
                userIdProvider.getCurrentUserId().collect { id ->
                    emitAll(userDao.selectById(id))
                }
            }
        }
    }

    override suspend fun currentUser(id: Uuid): User? = userDao.selectById(id).firstOrNull()

    override suspend fun deleteCurrentUser() {
        userDao.deleteById(userIdProvider.userId.value)
        log.d { "User deleted" }
    }
}