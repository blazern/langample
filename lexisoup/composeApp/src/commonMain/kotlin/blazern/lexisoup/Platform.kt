package blazern.lexisoup

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform