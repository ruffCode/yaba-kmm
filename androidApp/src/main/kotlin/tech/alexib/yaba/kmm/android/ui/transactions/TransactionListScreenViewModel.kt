package tech.alexib.yaba.kmm.android.ui.transactions

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.TransactionRepository

class TransactionListScreenViewModel : ViewModel(), KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    val state = transactionRepository.selectAll()
}
