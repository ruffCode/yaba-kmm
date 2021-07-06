package tech.alexib.yaba.kmm

expect class Platform() {
    val platform: String
}


expect fun<T> getSync(block: suspend () -> T):T
