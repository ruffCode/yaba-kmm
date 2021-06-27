package tech.alexib.yaba.kmm.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.android.ui.AddSpace
import tech.alexib.yaba.kmm.android.ui.theme.MoneyGreen
import tech.alexib.yaba.kmm.android.util.rememberFlowWithLifecycle
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import java.text.DecimalFormat

class HomeViewModel(
    private val initializer: Initializer
) : ViewModel(), KoinComponent {

    private val accountRepository: AccountRepository by inject()
    private val log: Kermit by inject { parametersOf("HomeViewModel") }
    private val loading = MutableStateFlow(false)
    private val error: MutableStateFlow<String?> = MutableStateFlow(null)

    val state: Flow<HomeScreenState> =
        combine(loading, error, accountRepository.cashBalance()) { loading, error, cashBalance ->
            HomeScreenState(loading, error, cashBalance)
        }

    fun init() {
        viewModelScope.launch {
            loading.value = true
            initializer.init()
            loading.value = false
        }
    }
}

@Immutable
data class HomeScreenState(
    val loading: Boolean = false,
    val error: String? = null,
    val cashBalance: Double = 0.0
) {
    companion object {
        val Empty = HomeScreenState()
    }
}

sealed class HomeScreenAction {
    object NavigateToPlaidLinkScreen : HomeScreenAction()
}

@Composable
fun Home(
    navigateToPlaidLinkScreen: () -> Unit
) {

    val viewModel: HomeViewModel = getViewModel()

    Home(viewModel = viewModel) {
        navigateToPlaidLinkScreen()
    }

}


@Composable
private fun Home(
    viewModel: HomeViewModel,
    navigateToPlaidLinkScreen: () -> Unit
) {
    viewModel.init()
    val state by rememberFlowWithLifecycle(flow = viewModel.state).collectAsState(initial = HomeScreenState.Empty)

    Home(state) { action ->
        when (action) {
            is HomeScreenAction.NavigateToPlaidLinkScreen -> navigateToPlaidLinkScreen()
        }
    }
}

@Composable
private fun Home(
    state: HomeScreenState,
    actioner: (HomeScreenAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AddSpace()
        
        if(state.cashBalance == 0.0){
            Button(onClick = {
                actioner(HomeScreenAction.NavigateToPlaidLinkScreen)
            }) {
                Text(text = "Link Account")
            }
        }else{
            TotalCashBalanceRow(state.cashBalance)
        }
        AddSpace()
    }
}

@Composable
fun TotalCashBalanceRow(
    availableBalance: Double
) {
    val isPositive = availableBalance > 0
    Card(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically),
        elevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(text = "Available cash balance")
                Text(
                    text = "$${moneyFormat.format(availableBalance)}",
                    color = if (isPositive) MoneyGreen else Color.Red,
                    textAlign = TextAlign.End
                )
            }

        }
    }

}

val moneyFormat = DecimalFormat("#,###.00")