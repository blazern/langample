package blazern.langample.domain.model

import androidx.annotation.StringRes
import blazern.langample.core.strings.R

enum class DataSource {
    TATOEBA,
    CHATGPT,
    KAIKKI,
    PANLEX,
    WORTSCHATZ_LEIPZIG,
}

val DataSource.strRsc: Int
    @StringRes
    get() {
        return when (this) {
            DataSource.TATOEBA -> R.string.general_data_source_tatoeba
            DataSource.CHATGPT -> R.string.general_data_source_chatgpt
            DataSource.KAIKKI -> R.string.general_data_source_kaikki
            DataSource.PANLEX -> R.string.general_data_source_panlex
            DataSource.WORTSCHATZ_LEIPZIG -> R.string.general_data_source_wortschatz_leipzig
        }
    }
