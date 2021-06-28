package tech.alexib.yaba.kmm.auth

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.data.db.AppSettings

class SessionManagerImpl(
    private val yabaAppSettings: AppSettings
) : SessionManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("SessionManagerImpl") }

    private val backgroundDispatcher: CoroutineDispatcher by inject()
    private val userIdFlow = MutableStateFlow<Uuid>(uuid4())

    init {
        ensureNeverFrozen()
        CoroutineScope(backgroundDispatcher).launch {
            yabaAppSettings.userId().collect { userId ->
                userId?.let { userIdFlow.emit(it) }
            }
        }
    }

    private val biometricSettings: BiometricSettings by inject()
    override fun isLoggedIn(): Flow<Boolean> = yabaAppSettings.authToken.map { !it.isNullOrEmpty() }

    override suspend fun setToken(token: String) {
        yabaAppSettings.setToken(token)
        if (isBioEnabled().first()) {
            biometricSettings.setBioToken(token)
        }
    }

    override suspend fun logout() {
        yabaAppSettings.clearAuthToken()
        yabaAppSettings.clearUserId()
    }

    override suspend fun getToken(): String? = yabaAppSettings.token().firstOrNull()

    override fun isBioEnabled(): Flow<Boolean> = yabaAppSettings.isBioEnabled()


    override suspend fun enableBio() {
        biometricSettings.enableBio()
    }

    override suspend fun bioToken() {
        biometricSettings.bioToken()
    }

    override fun isShowOnBoarding(): Flow<Boolean> = yabaAppSettings.showOnBoarding
    override fun startLogoutTimer() {
        TODO("Not yet implemented")
    }

    override suspend fun handleUnsuccessfulBioLogin() {
        biometricSettings.disableBio()
        logout()
    }

    override suspend fun setUserId(userId: Uuid) {
        yabaAppSettings.setUserId(userId)
    }

    override val userId: StateFlow<Uuid>
        get() = userIdFlow
}