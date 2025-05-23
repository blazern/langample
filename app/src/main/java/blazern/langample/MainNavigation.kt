package blazern.langample

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.compose.material3.Text
import androidx.navigation3.ui.NavDisplay
import blazern.langample.feature.home.HomeScreen

@Composable
fun MainNavigation() {
    val backStack = remember { mutableStateListOf<Any>(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    Column {
                        HomeScreen()
//                        Button(onClick = {
//                            backStack.add(SearchResults("AAA"))
//                        }) {
//                            Text("Click to navigate")
//                        }
                    }
                }

                is SearchResults -> NavEntry(key) {
                    Text("Product: ${key.query}")
                }

                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        }
    )
}