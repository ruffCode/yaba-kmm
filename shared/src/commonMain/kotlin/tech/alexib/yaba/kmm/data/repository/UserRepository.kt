package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.dao.UserDao
import tech.alexib.yaba.kmm.model.User

interface UserRepository {
    fun currentUser(): Flow<User?>
    suspend fun deleteCurrentUser()
}

internal class UserRepositoryImpl : UserIdProvider(), UserRepository, KoinComponent {

    private val log: Kermit by inject { parametersOf("UserRepository") }
    private val userDao: UserDao by inject()
    override fun currentUser(): Flow<User?> {
        return userDao.selectById(userId.value)
    }

    override suspend fun deleteCurrentUser() {
        userDao.deleteById(userId.value)
        log.d { "User deleted" }
    }
}