package blazern.lexisoup.feature.home.model

import blazern.lexisoup.domain.model.Lang

internal data class HomeScreenState(
    val langFrom: Lang?,
    val langTo: Lang?,
    val query: String,
    val canSearch: Boolean,
    val isLocalhost: Boolean?,
)
