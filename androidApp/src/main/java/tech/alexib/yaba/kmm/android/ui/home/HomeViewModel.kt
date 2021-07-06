package tech.alexib.yaba.kmm.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.TransactionRepository

class HomeViewModel(
    private val initializer: Initializer
) : ViewModel(), KoinComponent {

    private val accountRepository: AccountRepository by inject()
    private val transactionRepository: TransactionRepository by inject()
    private val log: Kermit by inject { parametersOf("HomeViewModel") }
    private val loading = MutableStateFlow(false)
    private val error: MutableStateFlow<String?> = MutableStateFlow(null)

    val state: Flow<HomeScreenState> =
        combine(
            loading,
            error,
            accountRepository.availableCashBalance(),
            transactionRepository.recentTransactions()
        ) { loading, error, cashBalance, recentTransactions ->
            HomeScreenState(loading, error, cashBalance, recentTransactions)
        }

    init {
        viewModelScope.launch {
            loading.value = true
            initializer.init()
            loading.value = false

        }
    }
}