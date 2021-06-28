package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.first
import tech.alexib.yaba.kmm.data.api.ApolloResponse
import tech.alexib.yaba.kmm.data.api.AuthApi
import tech.alexib.yaba.kmm.model.User
import tech.alexib.yaba.kmm.model.response.AuthResponse
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput


sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}


interface AuthRepository {
    suspend fun login(email: String, password: String): DataResult<AuthResponse>
    suspend fun register(email: String, password: String): DataResult<AuthResponse>
    suspend fun verifyToken(): DataResult<User>
}


class AuthRepositoryImpl(
    private val authApi: AuthApi,
    log: Kermit
) : AuthRepository {

    init {
        ensureNeverFrozen()
    }

    private val log = log


    override suspend fun login(email: String, password: String): DataResult<AuthResponse> {
        val input = UserLoginInput(email, password)
        return when (val result = authApi.login(input).first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { it }
                }
                ErrorResult(result.errors.first())
            }
        }
    }

    override suspend fun register(email: String, password: String): DataResult<AuthResponse> {
        val input = UserRegisterInput(email, password)
        return when (val result = authApi.register(input).first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { it }
                }
                ErrorResult(result.errors.first())
            }
        }
    }

    override suspend fun verifyToken(): DataResult<User> {

        return when (val result = authApi.verifyToken().first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { "verify token error $it" }
                }
                ErrorResult(result.message)
            }
        }

    }

}