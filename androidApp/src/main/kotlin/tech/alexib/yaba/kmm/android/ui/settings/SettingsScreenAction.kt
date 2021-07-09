package tech.alexib.yaba.kmm.android.ui.settings

sealed class SettingsScreenAction {
    object Logout : SettingsScreenAction()
    data class Navigate(val destination: NavDestination) : SettingsScreenAction()
    object ClearAppData : SettingsScreenAction()

    sealed class NavDestination {
        object Auth : NavDestination()
        object LinkedInstitutions : NavDestination()
    }
}
