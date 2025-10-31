package blazern.lexisoup.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blazern.lexisoup.domain.model.Lang
import blazern.lexisoup.domain.settings.SettingsRepository
import blazern.lexisoup.feature.home.model.HomeScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeScreenViewModel(
    query: String,
    private val settings: SettingsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState(
        langFrom = null,
        langTo = null,
        query = query,
        canSearch = canSearch(query),
    ))
    val state: StateFlow<HomeScreenState> = _state

    init {
        settings.getLangFrom().onEach { langFrom ->
            _state.update { it.copy(langFrom = langFrom) }
        }.launchIn(viewModelScope)

        settings.getLangTo().onEach { langTo ->
            _state.update { it.copy(langTo = langTo) }
        }.launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        _state.value = state.value.copy(
            query = value,
            canSearch = canSearch(value),
        )
    }

    fun onLangsChange(langFrom: Lang, langTo: Lang) {
        viewModelScope.launch {
            val oldLangTo = requireNotNull(_state.value.langTo)
            val oldLangFrom = requireNotNull(_state.value.langFrom)
            if (langFrom == langTo) {
                settings.setLangTo(oldLangFrom)
                settings.setLangFrom(oldLangTo)
            } else {
                settings.setLangTo(langTo)
                settings.setLangFrom(langFrom)
            }
        }
    }

    private fun canSearch(query: String): Boolean {
        return 2 <= query.trim().length
    }
}
