package blazern.lexisoup

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import blazern.lexisoup.domain.model.Lang
import blazern.lexisoup.feature.home.HomeRoute
import io.ktor.http.encodeURLParameter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeRoute(
                onSearch = { query, langFrom, langTo ->
                    navController.navigate(
                        "$ROUTE_SEARCH_RESULTS?" +
                                "$ARG_QUERY=${query.encodeURLParameter()}" +
                                "&$ARG_LANG_FROM=${langFrom.iso3}" +
                                "&$ARG_LANG_TO=${langTo.iso3}"
                    )
                }
            )
        }

        composable(
            route = "$ROUTE_SEARCH_RESULTS?" +
                    "$ARG_QUERY={$ARG_QUERY}" +
                    "&$ARG_LANG_FROM={$ARG_LANG_FROM}" +
                    "&$ARG_LANG_TO={$ARG_LANG_TO}",
            arguments = listOf(
                navArgument(ARG_QUERY) { type = NavType.StringType },
                navArgument(ARG_LANG_FROM) { type = NavType.StringType },
                navArgument(ARG_LANG_TO) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.read { getStringOrNull(ARG_QUERY) }.orEmpty()
            val langFrom = backStackEntry.arguments?.read { getStringOrNull(ARG_LANG_FROM) }.orEmpty()
            val langTo = backStackEntry.arguments?.read { getStringOrNull(ARG_LANG_TO) }.orEmpty()

            SearchResultsRoute(
                query,
                Lang.fromIso3(langFrom) ?: Lang.EN,
                Lang.fromIso3(langTo) ?: Lang.EN,
            )
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_SEARCH_RESULTS = "search_results"

private const val ARG_QUERY = "query"
private const val ARG_LANG_FROM = "lang_from"
private const val ARG_LANG_TO = "lang_to"

@Composable
fun SearchResultsRoute(
    query: String,
    langFrom: Lang,
    langTo: Lang,
) {
    Column {
        Text("SearchResultsRoute $query $langFrom $langTo")
        Text("SearchResultsRoute $query $langFrom $langTo")
        Text("SearchResultsRoute $query $langFrom $langTo")
        Text("SearchResultsRoute $query $langFrom $langTo")
        Text("SearchResultsRoute $query $langFrom $langTo")
//        Text("SearchResultsRoute $query $langFrom $langTo ${stringResource(Res.string.home_btn_search)}")
    }
}
