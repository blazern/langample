package blazern.langample.domain.model

import androidx.annotation.StringRes
import blazern.langample.core.strings.R

enum class Lang(
    val iso2: String,
    val iso3: String,
) {
    RU("ru", "rus"),
    EN("en", "eng"),
    DE("de", "deu"),
}

val Lang.strRsc: Int
    @StringRes
    get() {
        return when (this) {
            Lang.RU -> R.string.general_lang_ru
            Lang.EN -> R.string.general_lang_en
            Lang.DE -> R.string.general_lang_de
        }
    }