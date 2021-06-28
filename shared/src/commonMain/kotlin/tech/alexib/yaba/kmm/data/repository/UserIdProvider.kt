package tech.alexib.yaba.kmm.data.repository

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.db.AppSettings

internal open class UserIdProvider : KoinComponent {

    private val appSettings: AppSettings by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()
    private val userIdFlow = MutableStateFlow<Uuid>(uuid4())
    val userId: StateFlow<Uuid>
        get() = userIdFlow

    init {
        ensureNeverFrozen()
        CoroutineScope(backgroundDispatcher).launch {
            appSettings.userId().collect { userId ->
                userId?.let { userIdFlow.emit(it) }
            }
        }
    }
}