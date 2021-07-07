package tech.alexib.yaba.kmm.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.UserEntity
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.model.User

internal interface UserDao {
    suspend fun insert(user: User)
    suspend fun selectById(userId: Uuid): Flow<User?>
    suspend fun deleteById(userId: Uuid)
}


internal class UserDaoImpl(
    database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) : UserDao {
    private val queries = database.userEntityQueries

    init {
        ensureNeverFrozen()
    }

    override suspend fun insert(user: User) {
        withContext(backgroundDispatcher) {
            queries.insert(UserEntity(user.id, user.email))
        }
    }

    override suspend fun selectById(userId: Uuid): Flow<User?> =
        queries.selectById(userId) { id: Uuid, email: String -> User(id, email) }.asFlow()
            .mapToOneOrNull().flowOn(backgroundDispatcher)

    override suspend fun deleteById(userId: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteById(userId)
        }
    }
}