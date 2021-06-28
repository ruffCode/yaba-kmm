package tech.alexib.yaba.kmm.data.auth

import tech.alexib.yaba.kmm.data.repository.AuthResult

interface BiometricsManager {
    fun setupBiometrics()
    fun promptForBiometrics()
    suspend fun handleBioLogin(): AuthResult
}