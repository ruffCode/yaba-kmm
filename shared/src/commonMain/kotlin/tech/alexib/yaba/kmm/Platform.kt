package tech.alexib.yaba.kmm

import kotlinx.coroutines.CoroutineScope

expect class Platform() {
    val platform: String
}


expect fun<T> getSync(block: suspend () -> T):T
