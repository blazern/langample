package blazern.langample

import kotlinx.serialization.Serializable

interface NavigationRoute

@Serializable
data object Home : NavigationRoute

@Serializable
data class SearchResults(val query: String) : NavigationRoute
