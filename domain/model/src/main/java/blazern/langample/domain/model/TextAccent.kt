package blazern.langample.domain.model

/**
 * @param start inclusive
 * @param end not inclusive
 */
data class TextAccent(
    val start: Int,
    val end: Int,
)
