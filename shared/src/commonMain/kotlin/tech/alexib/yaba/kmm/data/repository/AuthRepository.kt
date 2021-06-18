package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.first
import tech.alexib.yaba.kmm.data.api.ApolloResponse
import tech.alexib.yaba.kmm.data.api.AuthApi
import tech.alexib.yaba.model.response.AuthResponse
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput

interface AuthRepository {
    suspend fun login(email: String, password: String): DataResult<AuthResponse>
    suspend fun register(email: String, password: String): DataResult<AuthResponse>
}


class AuthRepositoryImpl(
    private val authApi: AuthApi,
    log: Kermit
) : AuthRepository {

    private val log = log
    init {
        ensureNeverFrozen()
    }

    override suspend fun login(email: String, password: String): DataResult<AuthResponse> {

        val input = UserLoginInput(email, password)

        return when (val result = authApi.login(input).first()) {
            is ApolloResponse.Success -> Success(result.data)
            is ApolloResponse.Error -> {
                result.errors.forEach {
                    log.e { it }
                }
                ErrorResult("User login error")
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
                ErrorResult("User registration error")
            }
        }

    }
}