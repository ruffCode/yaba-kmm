package tech.alexib.yaba.kmm.data.auth

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun setToken(token: String)
    suspend fun logout()
    fun isBioEnabled(): Flow<Boolean>
    suspend fun enableBio()
    fun startLogoutTimer()
    fun isShowOnBoarding(): Flow<Boolean>
    suspend fun bioToken()
    suspend fun handleUnsuccessfulBioLogin()
    suspend fun setUserId(userId: Uuid)
}


