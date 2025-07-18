package blazern.langample.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.core.strings.R
import blazern.langample.core.ui.searchbar.SearchBar
import blazern.langample.theme.LangampleTheme

@Composable
fun HomeScreen(onSearch: (query: String)->Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        var query by rememberSaveable { mutableStateOf("") }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize()
        ) {
            SearchBar(
                query,
                onQueryChange = { query = it },
                onSearch = { onSearch(processQuery(query)) },
                placeholder = { Text(stringResource(R.string.home_input_hint)) },
                trailingIcon = {
                    IconButton(onClick = {
                        query = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onSearch(processQuery(query)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.home_btn_search))
            }
        }
    }
}

private fun processQuery(query: String) = query.trim()

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
fun Preview() {
    LangampleTheme {
        HomeScreen(onSearch = {})
    }
}
