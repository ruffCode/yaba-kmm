package tech.alexib.yaba.kmm.data.repository

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.auth.SessionManager

open class UserIdProvider : KoinComponent {
    private val sessionManager: SessionManager by inject()
    val userId: StateFlow<Uuid> = sessionManager.userId

    init {
        ensureNeverFrozen()
    }
}