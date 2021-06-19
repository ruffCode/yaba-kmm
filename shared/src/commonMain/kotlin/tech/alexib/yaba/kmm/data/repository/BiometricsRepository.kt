package tech.alexib.yaba.kmm.data.repository

interface BiometricsRepository {
    fun setupBiometrics()
    fun promptForBiometrics()
}