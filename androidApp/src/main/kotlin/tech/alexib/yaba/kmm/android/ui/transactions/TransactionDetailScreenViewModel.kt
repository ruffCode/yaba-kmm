package tech.alexib.yaba.kmm.android.ui.transactions

import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.TransactionRepository

class TransactionDetailScreenViewModel : ViewModel(), KoinComponent {
    private val repository: TransactionRepository by inject()

    private val transactionId = MutableStateFlow<Uuid?>(null)

    private val loadingFLow = MutableStateFlow(false)
    private val transactionDetailFlow = transactionId.flatMapLatest {
        loadingFLow.emit(true)
        if (it == null) emptyFlow() else repository.selectById(it).also {
            loadingFLow.emit(false)
        }
    }

    val state: Flow<TransactionDetailScreenState> =
        combine(loadingFLow, transactionDetailFlow) { loading, transaction ->
            TransactionDetailScreenState(
                loading,
                transaction
            )
        }

    fun getDetail(id: Uuid) {
        transactionId.value = id
    }
}
