package blazern.langample.domain.model

data class WordForm(
    val text: String,
    val tags: List<Tag>,
    val lang: Lang,
) {
    val hasArticle: Boolean
        get() = this != this.withoutArticle()
    val hasPronoun: Boolean
        get() = this != this.withoutPronoun()
    val wordsCount: Int
        get() = text.split(delimiter).size
    val auxiliary: Boolean
        get() = tags.any { it is Tag.Defined.Auxiliary }
    fun withoutPronoun(): WordForm = this.without(pronounsOf(lang))
    fun withoutArticle(): WordForm = this.without(articlesOf(lang))

    sealed class Tag(open val value: String) {
        data class Undefined(
            override val value: String,
        ) : Tag(value)

        sealed class Defined(
            override val value: String,
        ) : Tag(value) {
            data class Infinitive(override val value: String) : Defined(value)
            data class Participle(override val value: String) : Defined(value)
            data class Present(override val value: String) : Defined(value)
            data class Past(override val value: String) : Defined(value)
            data class Indicative(override val value: String) : Defined(value)
            data class SubjunctiveI(override val value: String) : Defined(value)
            data class SubjunctiveII(override val value: String) : Defined(value)
            data class Imperative(override val value: String) : Defined(value)
            data class Preterite(override val value: String) : Defined(value)
            data class Perfect(override val value: String) : Defined(value)
            data class Pluperfect(override val value: String) : Defined(value)
            data class FutureI(override val value: String) : Defined(value)
            data class FutureII(override val value: String) : Defined(value)
            data class FirstPerson(override val value: String) : Defined(value)
            data class SecondPerson(override val value: String) : Defined(value)
            data class ThirdPerson(override val value: String) : Defined(value)
            data class Singular(override val value: String) : Defined(value)
            data class Plural(override val value: String) : Defined(value)
            data class Informal(override val value: String) : Defined(value)
            data class Formal(override val value: String) : Defined(value)
            data class Rare(override val value: String) : Defined(value)
            data class MultiwordConstruction(override val value: String) : Defined(value)
            data class Auxiliary(override val value: String) : Defined(value)
        }
    }
}

private val delimiter = Regex("""[\\/ ]""")

private fun pronounsOf(lang: Lang) = when (lang) {
    Lang.RU -> setOf("я", "ты", "он", "она", "оно", "мы", "вы", "Вы", "они")
    Lang.EN -> setOf("I", "you", "he", "she", "it", "we", "they")
    Lang.DE -> setOf("ich", "du", "ihr", "er", "sie", "Sie", "es", "wir")
    Lang.FR -> setOf("je", "j'", "j’", "tu", "il", "elle", "on", "nous", "vous", "ils", "elles")
}

private fun articlesOf(lang: Lang) = when (lang) {
    Lang.RU -> emptySet()
    Lang.EN -> setOf("the", "a", "an")
    Lang.DE -> setOf("der", "die", "das", "den", "dem", "des", "ein", "eine", "einen", "einem", "eines")
    Lang.FR -> setOf("le", "la", "les", "l'", "l’", "un", "une", "des", "du", "au", "aux")
}


private fun WordForm.without(parts: Set<String>): WordForm =
    this.copy(text = text
        .split(delimiter)
        .filter { !parts.contains(it) }
        .joinToString(" "))
