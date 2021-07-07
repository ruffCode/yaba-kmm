package tech.alexib.yaba.kmm.android.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.TransactionRepository
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.util.ObservableLoadingCounter
import tech.alexib.yaba.kmm.util.collectInto

//!!TODO Placeholder/early prototype - hoping to extract this type of logic
private class HomeDataLoader(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository
) {
    private val recentTransactionsParam = MutableStateFlow<Unit?>(null)
    private val currentBalanceParam = MutableStateFlow<Unit?>(null)

    private val recentTransactionFlow: Flow<List<Transaction>> =
        recentTransactionsParam.flatMapLatest {
            when (it) {
                null -> emptyFlow()
                else -> transactionRepository.recentTransactions()
            }
        }

    private val currentBalanceFlow: Flow<Double> = currentBalanceParam.flatMapLatest {
        when (it) {
            null -> emptyFlow()
            else -> accountRepository.currentCashBalance()
        }
    }

    fun observeRecentTransactions(): Flow<List<Transaction>> {

        return recentTransactionFlow
    }

    fun observeCurrentBalance(): Flow<Double> {

        return currentBalanceFlow
    }

    operator fun invoke() {
        recentTransactionsParam.value = Unit
        currentBalanceParam.value = Unit
    }
}

class HomeViewModel(
    private val initializer: Initializer
) : ViewModel(), KoinComponent {

    private val homeDataLoaderState = ObservableLoadingCounter()
    private val accountRepository: AccountRepository by inject()
    private val transactionRepository: TransactionRepository by inject()
    private val log: Kermit by inject { parametersOf("HomeViewModel") }
    private val scope = viewModelScope
    private val homeDataLoader = HomeDataLoader(accountRepository, transactionRepository)


    val state: Flow<HomeScreenState> =
        combine(
            homeDataLoaderState.observable,
            homeDataLoader.observeCurrentBalance().distinctUntilChanged(),
            homeDataLoader.observeRecentTransactions().distinctUntilChanged()
        ) { loadingState, cashBalance, recentTransactions ->
            HomeScreenState(loadingState, cashBalance, recentTransactions)
        }

    init {
        homeDataLoader()
        scope.launch {
            initializer.init().collectInto(homeDataLoaderState)
        }
    }
}