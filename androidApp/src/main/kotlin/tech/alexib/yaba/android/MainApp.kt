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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.work.WorkManager
import androidx.work.await
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import kotlinx.coroutines.runBlocking
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tech.alexib.yaba.AppInfo
import tech.alexib.yaba.android.di.viewModelModule
import tech.alexib.yaba.data.di.initKoin

class MainApp : Application() {

    var appInfo: ApplicationInfo? = null

    private val serverUrl: String by lazy {
        appInfo?.metaData?.getString("serverUrl") ?: "https://yabasandbox.alexib.dev/graphql"
    }

    private val isSandbox: Boolean by lazy {
        appInfo?.metaData?.getBoolean("isSandbox") ?: true
    }

    override fun onCreate() {
        super.onCreate()

        try {
            appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        initKoin {
            androidContext(this@MainApp)
            workManagerFactory()
            modules(createAppModule(serverUrl, isSandbox), viewModelModule)
        }
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        SentryAndroid.init(this) { options ->
            options.setBeforeSend { event, _ ->
                if (SentryLevel.DEBUG == event.level) {
                    null
                } else event
            }
        }
        cancelPendingWorkManager(this)
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}

fun createAppModule(
    serverUrl: String = "https://yabasandbox.alexib.dev/graphql",
    isSandbox: Boolean = true
) =
    module {
        single(named("serverUrl")) { serverUrl }
        single(named("isSandbox")) { isSandbox }
        single<AppInfo> { AndroidAppInfo }
    }

private fun cancelPendingWorkManager(mainApplication: MainApp) {
    runBlocking {
        WorkManager.getInstance(mainApplication)
            .cancelAllWork()
            .result
            .await()
    }
}
