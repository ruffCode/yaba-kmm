package tech.alexib.yaba.kmm

import kotlinx.coroutines.runBlocking

@Suppress("MemberNameEqualsClassName", "EmptyDefaultConstructor")
actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun <T> getSync(block: suspend () -> T): T {
    return runBlocking { block() }
}
