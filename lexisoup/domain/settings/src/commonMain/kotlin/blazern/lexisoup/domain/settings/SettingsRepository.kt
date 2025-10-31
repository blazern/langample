package blazern.lexisoup.domain.settings

import blazern.lexisoup.domain.model.Lang
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepository {
    suspend fun getTatoebaAcceptableTagsSets(): List<Set<String>> {
        return listOf(
            setOf("nominative", "singular"),
            setOf("nominative", "plural"),
        )
    }

    suspend fun setLangFrom(lang: Lang) {
    }

    fun getLangFrom(): Flow<Lang> = flowOf(Lang.EN)

    suspend fun setLangTo(lang: Lang) = Unit

    fun getLangTo(): Flow<Lang> = flowOf(Lang.EN)
}
