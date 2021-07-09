package tech.alexib.yaba.kmm.android.ui.auth.biometric

import androidx.compose.runtime.Immutable

@Immutable
data class BiometricSetupScreenState(
    val setupSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val declined: Boolean = false
) {
    companion object {
        val Empty = BiometricSetupScreenState()
    }
}
