package tech.alexib.yaba.kmm.android.ui.plaid

import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.api.PlaidItemApi

class PlaidLinkResultScreenViewModel : ViewModel(), KoinComponent {

    private val plaidItemApi: PlaidItemApi by inject()

    lateinit var itemId: Uuid

    private val accountsFlow = MutableStateFlow<List<PlaidLinkScreenResult.Account>>(emptyList())

    val accounts: StateFlow<List<PlaidLinkScreenResult.Account>>
        get() = accountsFlow


    fun init(plaidLinkScreenResult: PlaidLinkScreenResult) {
        itemId = plaidLinkScreenResult.id
        accountsFlow.value = plaidLinkScreenResult.accounts
    }

    fun setAccountShown(plaidAccountId: String, show: Boolean) {
        val currentAccounts = accountsFlow.value.toMutableList()
        val currentItem = currentAccounts.first { it.plaidAccountId == plaidAccountId }
        currentAccounts[currentAccounts.indexOf(currentItem)] = currentItem.copy(show = show)
        accountsFlow.value = currentAccounts
    }


    fun submitAccountsToHide() {
        val accountsToHide = accountsFlow.value.filter { !it.show }.map { it.plaidAccountId }
        plaidItemApi.setAccountsToHide(itemId, accountsToHide)
    }

}