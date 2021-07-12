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
package tech.alexib.yaba.android

import android.app.Application
import android.content.Context
import android.util.Log
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tech.alexib.yaba.AppInfo
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
import tech.alexib.yaba.di.initKoin

class MainApp : Application() {

    private val apolloUrl: String = BuildConfig.APOLLO_URL

    private val appModule = module {
        single<Context> { this@MainApp }
        single<AppInfo> { AndroidAppInfo }
        single(named("serverUrl")) { apolloUrl }
        single {
            { Log.i("Startup", "Hello from Android/Kotlin!") }
        }

        viewModel { parameters -> SplashScreenViewModel(parameters.get(), get()) }
        viewModel { LoginScreenViewModel(get()) }
        viewModel { RegisterScreenViewModel() }
        viewModel { SettingsScreenViewModel() }
        viewModel { PlaidLinkViewModel(get()) }
        viewModel { PlaidLinkResultScreenViewModel() }
        viewModel { BiometricSetupScreenViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { PlaidItemsScreenViewModel() }
        viewModel { PlaidItemDetailScreenViewModel() }
        viewModel { TransactionListScreenViewModel() }
        viewModel { TransactionDetailScreenViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        initKoin(appModule)
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
