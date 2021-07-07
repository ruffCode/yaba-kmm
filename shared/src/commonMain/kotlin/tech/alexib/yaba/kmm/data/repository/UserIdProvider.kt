package tech.alexib.yaba.kmm.data.repository

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.AppSettings

internal class UserIdProvider : KoinComponent {

    private val appSettings: AppSettings by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()
    private val userIdFlow = MutableStateFlow<Uuid>(defaultUserId)
    private val log: Kermit by inject { parametersOf("UserIdProvider") }
    val userId: StateFlow<Uuid>
        get() = userIdFlow

    init {
        ensureNeverFrozen()

        CoroutineScope(backgroundDispatcher).launch {
            appSettings.userId().distinctUntilChanged().collectLatest { userId ->
                log.d { "userId set $userId" }
                userId?.let { userIdFlow.emit(it) }
            }
        }
    }

    fun getCurrentUserId(): Flow<Uuid> {
        return channelFlow <Uuid> {
            userIdFlow.dropWhile { it == defaultUserId }.collectLatest {
                launch(backgroundDispatcher){
                    send(it)
                }
            }
        }
    }

    companion object {
        val defaultUserId = uuidFrom("aa1e60bd-0a48-4e8d-800e-237f42d4a793")
    }
}