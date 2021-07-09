package tech.alexib.yaba.kmm.android.ui.auth.biometric

sealed class BiometricSetupScreenAction {
    object PromptSetup : BiometricSetupScreenAction()
    object Decline : BiometricSetupScreenAction()
    object NavigateHome : BiometricSetupScreenAction()
}
