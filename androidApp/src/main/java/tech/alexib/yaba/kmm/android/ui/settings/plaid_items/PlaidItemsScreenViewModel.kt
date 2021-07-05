package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.ItemRepository
import tech.alexib.yaba.kmm.model.PlaidItemWithAccounts

class PlaidItemsScreenViewModel : ViewModel(), KoinComponent {

    private val itemRepository: ItemRepository by inject()
    private val plaidItemsFlow = MutableSharedFlow<List<PlaidItemWithAccounts>>(replay = 1)
    private val loading = MutableStateFlow(false)
    val state: Flow<PlaidItemsScreenState> = combine(plaidItemsFlow, loading) { items, loading ->
        PlaidItemsScreenState(items, loading)
    }

    init {
        viewModelScope.launch {
            itemRepository.getAllWithAccounts().collect { items ->
                loading.emit(true)
                plaidItemsFlow.emit(items)
                loading.emit(false)
            }
        }
    }

}