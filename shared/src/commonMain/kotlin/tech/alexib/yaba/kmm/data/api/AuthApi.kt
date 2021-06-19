package tech.alexib.yaba.kmm.data.api

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.LoginMutation
import tech.alexib.yaba.RegisterMutation
import tech.alexib.yaba.kmm.model.response.AuthResponse
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.request.UserRegisterInput

interface AuthApi {
    suspend fun login(userLoginInput: UserLoginInput): Flow<ApolloResponse<AuthResponse>>
    suspend fun register(userRegisterInput: UserRegisterInput): Flow<ApolloResponse<AuthResponse>>
}

class AuthApiImpl(
    private val apolloApi: ApolloApi
) : AuthApi {
    private val client by lazy { apolloApi.client() }


    init {
        ensureNeverFrozen()
    }

    override suspend fun login(userLoginInput: UserLoginInput): Flow<ApolloResponse<AuthResponse>> {
        val mutation = LoginMutation(userLoginInput.email, userLoginInput.password)

        return runCatching {
            client.safeMutation(mutation) {
                it.login.toAuthResponse()
            }
        }.getOrThrow()

    }

    override suspend fun register(userRegisterInput: UserRegisterInput): Flow<ApolloResponse<AuthResponse>> {
        val mutation = RegisterMutation(userRegisterInput.email, userRegisterInput.password)
        return runCatching {
            client.safeMutation(mutation) {
                it.register.toAuthResponse()
            }
        }.getOrThrow()
    }


    private fun LoginMutation.Login.toAuthResponse() = AuthResponse(
        id = id as Uuid,
        email = email,
        token = token
    )

    private fun RegisterMutation.Register.toAuthResponse() = AuthResponse(
        id = id as Uuid,
        email = email,
        token = token
    )
}