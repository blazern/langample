package blazern.lexisoup.core.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blazern.lexisoup.core.ui.theme.LangampleTheme
import lexisoup.core.ui.components.generated.resources.Res
import lexisoup.core.ui.components.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { Text("") },
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            painterResource(Res.drawable.search),
            contentDescription = "",
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    onSearch(query)
                },
                expanded = false,
                onExpandedChange = {},
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon
            )
        },
        expanded = false,
        onExpandedChange = {},
        content = {},
        windowInsets = WindowInsets(0.dp),
        modifier = modifier,
    )
}

@Preview(name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun Preview() {
    LangampleTheme {
        SearchBar(
            "Search query",
            onQueryChange = {},
            onSearch = {},
        )
    }
}
