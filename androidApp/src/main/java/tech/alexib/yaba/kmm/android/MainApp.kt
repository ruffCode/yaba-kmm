package tech.alexib.yaba.kmm.android

import android.app.Application
import android.content.Context
import android.util.Log
import org.koin.dsl.module
import tech.alexib.yaba.kmm.AppInfo
import tech.alexib.yaba.kmm.di.initKoin

class MainApp:Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(module {
            single<Context> { this@MainApp }
            single<AppInfo> { AndroidAppInfo }
            single {
                { Log.i("Startup", "Hello from Android/Kotlin!") }
            }
        })
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}