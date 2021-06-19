package tech.alexib.yaba.kmm.data.auth

import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun setToken(token: String)
    suspend fun logout()
    suspend fun getToken(): String?
    fun isBioEnabled(): Flow<Boolean>
    suspend fun enableBio()
    fun startLogoutTimer()
    fun isShowOnBoarding():Flow<Boolean>
}


