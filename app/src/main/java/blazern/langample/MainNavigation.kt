package blazern.langample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.compose.material3.Text
import androidx.navigation3.ui.NavDisplay
import blazern.langample.feature.home.HomeScreen
import blazern.langample.feature.search_result.ui.SearchResultsScreen

@Composable
fun MainNavigation() {
    val backStack = remember { mutableStateListOf<Any>(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    HomeScreen(
                        onSearch = {
                            backStack.add(SearchResults(it))
                        }
                    )
                }
                is SearchResults -> NavEntry(key) {
                    SearchResultsScreen(key.query)
                }
                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        }
    )
}