package tech.alexib.yaba.kmm.android.ui.home

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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.data.Initializer

class HomeViewModel(
    private val initializer: Initializer
) : ViewModel() {

    fun init() {
        viewModelScope.launch {
            initializer.init()
        }
    }
}



@Composable
fun Home(
    navigateToPlaidLinkScreen: () -> Unit
) {

    val viewModel: HomeViewModel = getViewModel()


    viewModel.init()
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



