package blazern.langample.domain.model

enum class Lang(
    val iso2: String,
    val iso3: String,
) {
    RU("ru", "rus"),
    EN("en", "eng"),
    DE("de", "deu"),
}
