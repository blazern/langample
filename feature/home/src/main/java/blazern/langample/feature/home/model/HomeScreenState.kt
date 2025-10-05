package blazern.langample.feature.home.model

import blazern.langample.domain.model.Lang

internal data class HomeScreenState(
    val langFrom: Lang?,
    val langTo: Lang?,
    val query: String,
    val canSearch: Boolean,
)
