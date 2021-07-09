package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.repository.ItemRepository
import tech.alexib.yaba.kmm.model.PlaidItemWithAccounts

class PlaidItemsScreenViewModel : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("PlaidItemsScreenViewModel") }
    private val itemRepository: ItemRepository by inject()
    private val plaidItemsFlow = MutableStateFlow<List<PlaidItemWithAccounts>>(emptyList())
    private val loadingFlow = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = loadingFlow

    val state: Flow<PlaidItemsScreenState> =
        combine(plaidItemsFlow, loadingFlow) { items, loading ->
            PlaidItemsScreenState(items, loading)
        }

    init {
        viewModelScope.launch {
            loadingFlow.emit(true)
            itemRepository.getAllWithAccounts().collect { items ->
                plaidItemsFlow.emit(items)
                loadingFlow.emit(false)
            }
        }
    }
}
