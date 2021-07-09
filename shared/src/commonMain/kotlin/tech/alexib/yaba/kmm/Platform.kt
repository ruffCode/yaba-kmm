package tech.alexib.yaba.kmm

@Suppress("MemberNameEqualsClassName", "EmptyDefaultConstructor")
expect class Platform() {
    val platform: String
}

expect fun <T> getSync(block: suspend () -> T): T
