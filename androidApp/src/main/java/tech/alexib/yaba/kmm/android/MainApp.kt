package tech.alexib.yaba.kmm.android

import android.app.Application
import android.content.Context
import android.util.Log
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.alexib.yaba.kmm.AppInfo
import tech.alexib.yaba.kmm.android.ui.auth.login.LoginScreenViewModel
import tech.alexib.yaba.kmm.android.ui.auth.RegisterUserViewModel

import tech.alexib.yaba.kmm.android.ui.home.SplashScreenViewModel
import tech.alexib.yaba.kmm.android.ui.settings.SettingsScreenViewModel
import tech.alexib.yaba.kmm.di.initKoin

class MainApp : Application() {

    private val appModule = module {
        single<Context> { this@MainApp }
        single<AppInfo> { AndroidAppInfo }

        single {
            { Log.i("Startup", "Hello from Android/Kotlin!") }
        }

        viewModel { parameters -> SplashScreenViewModel(parameters.get(),get()) }
        viewModel { LoginScreenViewModel(get()) }
        viewModel { RegisterUserViewModel() }
        viewModel { SettingsScreenViewModel() }

    }

    override fun onCreate() {
        super.onCreate()

        initKoin(appModule)
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}