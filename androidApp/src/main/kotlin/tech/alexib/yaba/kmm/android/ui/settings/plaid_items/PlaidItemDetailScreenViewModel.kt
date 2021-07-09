package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.ItemRepository

class PlaidItemDetailScreenViewModel : ViewModel(), KoinComponent {

    private val accountRepository: AccountRepository by inject()
    private val itemRepository: ItemRepository by inject()
    fun unlinkItem(itemId: Uuid) {
        viewModelScope.launch {
            itemRepository.unlinkItem(itemId)
        }
    }

    fun setAccountHidden(hidden: Boolean, accountId: Uuid) {
        viewModelScope.launch {
            if (hidden) {
                accountRepository.hide(accountId)
            } else {
                accountRepository.show(accountId)
            }
        }
    }
}
