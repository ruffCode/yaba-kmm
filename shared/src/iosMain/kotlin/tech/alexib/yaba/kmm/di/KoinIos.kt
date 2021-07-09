package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import tech.alexib.yaba.kmm.data.db.DriverFactory

fun initKoinIos(
//    userDefaults: NSUserDefaults,
//    appInfo: AppInfo,
    doOnStartup: () -> Unit
): KoinApplication = initKoin(
    module {
//        single<Settings> { AppleSettings(userDefaults) }
//        single { appInfo }
        single { doOnStartup }
    }
)

actual val platformModule = module {
    val baseKermit = Kermit(NSLogLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
    single<SqlDriver> { DriverFactory().createDriver() }
}

fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?, parameter: Any): Any {
    val kClazz = getOriginalKotlinClass(objCClass)!!
    return get(kClazz, qualifier) { parametersOf(parameter) }
}

fun Koin.get(objCClass: ObjCClass, parameter: Any): Any {
    val kClazz = getOriginalKotlinClass(objCClass)!!
    return get(kClazz, null) { parametersOf(parameter) }
}

fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?): Any {
    val kClazz = getOriginalKotlinClass(objCClass)!!
    return get(kClazz, qualifier, null)
}
