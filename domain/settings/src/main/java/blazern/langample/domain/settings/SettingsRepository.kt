package blazern.langample.domain.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import blazern.langample.domain.model.Lang
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val context: Context,
) {
    // NOTE: Android only
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun getTatoebaAcceptableTagsSets(): List<Set<String>> {
        return listOf(
            setOf("nominative", "singular"),
            setOf("nominative", "plural"),
        )
    }

    suspend fun setLangFrom(lang: Lang) {
        context.dataStore.edit {
            it[KEY_LANG_FROM] = lang.iso3
        }
    }

    fun getLangFrom(): Flow<Lang> = context.dataStore.data.map {
        Lang.fromIso3(it[KEY_LANG_FROM] ?: "") ?: Lang.EN
    }

    suspend fun setLangTo(lang: Lang) {
        context.dataStore.edit {
            it[KEY_LANG_TO] = lang.iso3
        }
    }

    fun getLangTo(): Flow<Lang> = context.dataStore.data.map {
        Lang.fromIso3(it[KEY_LANG_TO] ?: "") ?: Lang.DE
    }

    private companion object {
        val KEY_LANG_FROM = stringPreferencesKey("lang_from")
        val KEY_LANG_TO = stringPreferencesKey("lang_to")
    }
}
