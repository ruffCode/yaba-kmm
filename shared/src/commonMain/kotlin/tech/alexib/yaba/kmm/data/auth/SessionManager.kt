package tech.alexib.yaba.kmm.data.auth

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.kmm.data.repository.AuthResult

interface SessionManager {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun setToken(token: String)
    suspend fun logout()
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun startLogoutTimer()
    fun isShowOnBoarding(): Flow<Boolean>
    suspend fun setUserId(userId: Uuid)
}


