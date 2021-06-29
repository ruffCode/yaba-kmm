package tech.alexib.yaba.kmm.auth

import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.kmm.data.repository.AuthResult

interface BiometricsManager {
    fun promptForBiometrics(): Flow<BiometricAuthResult>
    suspend fun handleBioLogin(): AuthResult
    fun shouldPromptSetupBiometrics(): Flow<Boolean>
    val bioToken: Flow<String?>
    val isBioEnabled: Flow<Boolean>
    suspend fun setBioEnabled(enabled:Boolean)
    suspend fun handleBiometricAuthResult(
        result: BiometricAuthResult,
        onSuccess: suspend () -> Unit,
        onError: () -> Unit,
        onCancel: () -> Unit
    )
}