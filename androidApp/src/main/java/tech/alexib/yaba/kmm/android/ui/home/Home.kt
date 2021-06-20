package tech.alexib.yaba.kmm.android.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkResult
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.android.ui.plaid_items.PlaidLinkHandler
import tech.alexib.yaba.kmm.android.ui.plaid_items.PlaidLinkService


class HomeViewModel : ViewModel(), KoinComponent {

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

@Composable
fun Home() {

    val viewModel: HomeViewModel = getViewModel()

    PlaidLinkHandler(onResult = { viewModel.handleResult(it) }) { openLink ->

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home")
            Button(onClick = {
                viewModel.linkInstitution { config ->
                    Log.d("config",config.token)
                    openLink.launch(config)
                }
            }) {
                Text(text = "Link Account")
            }

        }


    }


}