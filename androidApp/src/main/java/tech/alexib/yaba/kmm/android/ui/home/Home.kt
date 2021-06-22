package tech.alexib.yaba.kmm.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.plaid.link.PlaidActivityResultContract
import org.koin.androidx.compose.getViewModel

import tech.alexib.yaba.kmm.android.ui.plaid_items.PlaidLinkHandler
import tech.alexib.yaba.kmm.android.ui.plaid.PlaidLinkViewModel
import tech.alexib.yaba.kmm.data.repository.ErrorResult
import tech.alexib.yaba.kmm.data.repository.Success



@Composable
fun Home(
    navigateToPlaidLinkScreen: () -> Unit
) {

//    val viewModel: HomeViewModel = getViewModel()
//
//    Home(viewModel = viewModel)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home")
        Button(onClick = {
//            viewModel.linkInstitution { config ->
//                Log.d("config", config.token)
//                openLink.launch(config)
//            }
            navigateToPlaidLinkScreen()
        }) {
            Text(text = "Link Account")
        }

    }


}



