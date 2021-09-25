/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import tech.alexib.yaba.android.ui.transactions.TransactionListScreenViewModel
import tech.alexib.yaba.data.fcm.PushTokenManager

val viewModelModule = module {
    viewModel { parameters -> SplashScreenViewModel(parameters.get(), get()) }
    viewModel { LoginScreenViewModel(get(), get()) }
    viewModel { RegisterScreenViewModel() }
    viewModel { SettingsScreenViewModel() }
    single { PlaidLinkViewModel(get()) }
    single {
        PlaidLinkResultScreenViewModel()
    }
    viewModel { BiometricSetupScreenViewModel() }
    viewModel { HomeViewModel() }
    viewModel { PlaidItemsScreenViewModel() }
    viewModel { PlaidItemDetailScreenViewModel(get()) }
    viewModel { TransactionListScreenViewModel() }
    viewModel { TransactionDetailScreenViewModel(get()) }
    single<PushTokenManager> { PushTokenManagerImpl() }
    viewModel { AccountsScreenViewModel() }
    viewModel { AccountDetailScreenViewModel(get(), get()) }
}
