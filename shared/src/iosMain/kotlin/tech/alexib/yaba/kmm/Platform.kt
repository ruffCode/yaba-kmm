package tech.alexib.yaba.kmm

import kotlinx.coroutines.runBlocking
import platform.UIKit.UIDevice

@Suppress("MemberNameEqualsClassName", "EmptyDefaultConstructor")
actual class Platform actual constructor() {
    actual val platform: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun <T> getSync(block: suspend () -> T): T {
    return runBlocking { block() }
}
