package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.repository.ItemRepository
import tech.alexib.yaba.kmm.model.PlaidItemWIthAccounts

class PlaidItemsScreenViewModel : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("PlaidItemsScreenViewModel") }
    private val itemRepository: ItemRepository by inject()
    private val plaidItemsFlow = MutableStateFlow<List<PlaidItemWIthAccounts>>(emptyList())
    val state: Flow<PlaidItemsScreenState> = plaidItemsFlow.mapLatest {
        PlaidItemsScreenState(it)
    }

    init {
        log.d { "INIT CALLED" }
        viewModelScope.launch {
            itemRepository.getAllWithAccounts().collect {
                plaidItemsFlow.value = it
            }
        }
    }

}