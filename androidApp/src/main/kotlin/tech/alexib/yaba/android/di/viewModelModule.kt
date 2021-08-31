package tech.alexib.yaba.android.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.alexib.yaba.android.fcm.PushTokenManagerImpl
import tech.alexib.yaba.android.ui.accounts.AccountsScreenViewModel
import tech.alexib.yaba.android.ui.accounts.detail.AccountDetailScreenViewModel
import tech.alexib.yaba.android.ui.auth.biometric.BiometricSetupScreenViewModel
import tech.alexib.yaba.android.ui.auth.login.LoginScreenViewModel
import tech.alexib.yaba.android.ui.auth.register.RegisterScreenViewModel
import tech.alexib.yaba.android.ui.auth.splash.SplashScreenViewModel
import tech.alexib.yaba.android.ui.home.HomeViewModel
import tech.alexib.yaba.android.ui.plaid.PlaidLinkResultScreenViewModel
import tech.alexib.yaba.android.ui.plaid.PlaidLinkViewModel
import tech.alexib.yaba.android.ui.settings.SettingsScreenViewModel
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemDetailScreenViewModel
import tech.alexib.yaba.android.ui.settings.plaid_items.PlaidItemsScreenViewModel
import tech.alexib.yaba.android.ui.transactions.TransactionDetailScreenViewModel
import tech.alexib.yaba.android.ui.transactions.TransactionListPager
import tech.alexib.yaba.android.ui.transactions.TransactionListScreenViewModel
import tech.alexib.yaba.data.fcm.PushTokenManager
import tech.alexib.yaba.util.getWith

val viewModelModule = module {
    viewModel { parameters -> SplashScreenViewModel(parameters.get(), get()) }
    viewModel { LoginScreenViewModel(get(), get()) }
    viewModel { RegisterScreenViewModel() }
    viewModel { SettingsScreenViewModel() }
    viewModel { PlaidLinkViewModel(get()) }
    viewModel { PlaidLinkResultScreenViewModel() }
    viewModel { BiometricSetupScreenViewModel() }
    viewModel { HomeViewModel() }
    viewModel { PlaidItemsScreenViewModel() }
    viewModel { PlaidItemDetailScreenViewModel() }
    viewModel { TransactionListScreenViewModel() }
    viewModel { TransactionDetailScreenViewModel() }
    single<PushTokenManager> { PushTokenManagerImpl() }
    viewModel { AccountsScreenViewModel() }
    viewModel { AccountDetailScreenViewModel() }
    single { TransactionListPager(get(),getWith("TransactionListPager")) }
}
