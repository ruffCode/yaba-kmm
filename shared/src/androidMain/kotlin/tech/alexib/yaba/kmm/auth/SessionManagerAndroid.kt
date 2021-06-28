package tech.alexib.yaba.kmm.auth

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.data.db.AppSettings

class SessionManagerAndroid(
    private val yabaAppSettings: AppSettings
) : SessionManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("SessionManagerImpl") }


    init {
        ensureNeverFrozen()

    }

    private val biometricSettings: BiometricSettings by inject()
    override fun isLoggedIn(): Flow<Boolean> = yabaAppSettings.token().map { !it.isNullOrEmpty() }

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

    override fun isBioEnabled(): Flow<Boolean> = yabaAppSettings.isBioEnabled()


    override suspend fun enableBio() {
        biometricSettings.enableBio()
    }

    override suspend fun bioToken() {
        biometricSettings.bioToken()
    }

    override fun isShowOnBoarding(): Flow<Boolean> = yabaAppSettings.showOnboarding()
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

}