package blazern.langample

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import blazern.langample.domain.model.Lang
import blazern.langample.feature.home.HomeRoute
import blazern.langample.feature.search_result.SearchResultsRoute
import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLParameter

@Composable
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
            val args = backStackEntry.arguments
            val query = args?.getString(ARG_QUERY)?.decodeURLPart().orEmpty()
            val langFrom = Lang.fromIso3(args?.getString(ARG_LANG_FROM).orEmpty()) ?: Lang.EN
            val langTo = Lang.fromIso3(args?.getString(ARG_LANG_TO).orEmpty()) ?: Lang.EN

            SearchResultsRoute(query, langFrom, langTo)
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_SEARCH_RESULTS = "search_results"

private const val ARG_QUERY = "query"
private const val ARG_LANG_FROM = "lang_from"
private const val ARG_LANG_TO = "lang_to"
