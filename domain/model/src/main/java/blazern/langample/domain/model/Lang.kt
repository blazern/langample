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
    FR("fr", "fra"),
    ;

    companion object {
        private val iso2Map = entries.associateBy { it.iso2 }
        private val iso3Map = entries.associateBy { it.iso3 }
        fun fromIso2(iso2: String): Lang? = iso2Map[iso2]
        fun fromIso3(iso3: String): Lang? = iso3Map[iso3]
    }
}

val Lang.strRsc: Int
    @StringRes
    get() {
        return when (this) {
            Lang.RU -> R.string.general_lang_ru
            Lang.EN -> R.string.general_lang_en
            Lang.DE -> R.string.general_lang_de
            Lang.FR -> R.string.general_lang_fr
        }
    }
