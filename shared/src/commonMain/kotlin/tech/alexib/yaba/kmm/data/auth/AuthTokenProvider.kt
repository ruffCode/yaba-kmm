package tech.alexib.yaba.kmm.data.auth

import co.touchlab.stately.ensureNeverFrozen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.db.AppSettings

internal open class AuthTokenProvider : KoinComponent {
    private val appSettings: AppSettings by inject()

    val authToken = appSettings.authToken

    init {
        ensureNeverFrozen()
    }
}