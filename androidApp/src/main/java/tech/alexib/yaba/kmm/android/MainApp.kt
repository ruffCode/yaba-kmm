package tech.alexib.yaba.kmm.android

import android.app.Application
import android.content.Context
import android.util.Log
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.alexib.yaba.kmm.AppInfo
import tech.alexib.yaba.kmm.android.ui.auth.login.LoginScreenViewModel
import tech.alexib.yaba.kmm.android.ui.auth.register.RegisterScreenViewModel
import tech.alexib.yaba.kmm.android.ui.auth.splash.SplashScreenViewModel
import tech.alexib.yaba.kmm.android.ui.home.HomeViewModel
import tech.alexib.yaba.kmm.android.ui.plaid.PlaidLinkResultScreenViewModel
import tech.alexib.yaba.kmm.android.ui.plaid.PlaidLinkViewModel
import tech.alexib.yaba.kmm.android.ui.settings.SettingsScreenViewModel
import tech.alexib.yaba.kmm.data.api.ApolloUrl
import tech.alexib.yaba.kmm.di.initKoin

class MainApp : Application() {

    private val apolloUrl: ApolloUrl =
        if (BuildConfig.DEBUG) ApolloUrl("https://ruffrevival.ngrok.io/graphql")
        else ApolloUrl("https://yabasandbox.alexib.dev/graphql")
    private val appModule = module {
        single<Context> { this@MainApp }
        single<AppInfo> { AndroidAppInfo }
        single { apolloUrl }
        single {
            { Log.i("Startup", "Hello from Android/Kotlin!") }
        }

        viewModel { parameters -> SplashScreenViewModel(parameters.get(), get()) }
        viewModel { LoginScreenViewModel(get()) }
        viewModel { RegisterScreenViewModel() }
        viewModel { SettingsScreenViewModel() }
        viewModel { PlaidLinkViewModel(get()) }
        viewModel { PlaidLinkResultScreenViewModel() }
        viewModel { HomeViewModel(get()) }

    }

    override fun onCreate() {
        super.onCreate()

        initKoin(appModule)
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}