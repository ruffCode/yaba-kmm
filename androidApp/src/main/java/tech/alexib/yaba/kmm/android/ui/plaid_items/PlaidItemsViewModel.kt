package tech.alexib.yaba.kmm.android.ui.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkResult
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaidItemsViewModel : ViewModel(), KoinComponent {


    private val service: PlaidLinkService by inject()

    fun linkInstitution(handleToken: (LinkTokenConfiguration) -> Unit) {
        viewModelScope.launch {
            service.linkInstitution(handleToken)
        }
    }


    fun handleResult(linkResult: LinkResult) {
        viewModelScope.launch {
            service.handleResult(linkResult)
        }
    }
}