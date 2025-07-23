package blazern.langample.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.core.strings.R as RL
import blazern.langample.core.ui.searchbar.SearchBar
import blazern.langample.domain.model.Lang
import blazern.langample.feature.home.R
import blazern.langample.feature.home.SearchFn
import blazern.langample.feature.home.model.HomeScreenState
import blazern.langample.theme.LangampleTheme

@Composable
internal fun HomeScreen(
    state: HomeScreenState,
    onQueryChange: (query: String)->Unit,
    onLangsChange: (langFrom: Lang, langTo: Lang)->Unit,
    onSearch: SearchFn,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize()
        ) {
            if (state.langFrom != null && state.langTo != null) {
                LangsSelector(state.langFrom, state.langTo, onLangsChange)
            }
            val onSearchWrapper = { query: String ->
                if (state.langFrom != null && state.langTo != null) {
                    onSearch(
                        query.trim(),
                        state.langFrom,
                        state.langTo,
                    )
                }
            }
            SearchBar(
                state.query,
                onQueryChange = { onQueryChange(it) },
                onSearch = { onSearchWrapper(state.query) },
                placeholder = { Text(stringResource(RL.string.home_input_hint)) },
                trailingIcon = {
                    IconButton(onClick = {
                        onQueryChange("")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(RL.string.home_cd_clear_search_query),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onSearchWrapper(state.query) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(RL.string.home_btn_search))
            }
        }
    }
}

@Composable
private fun LangsSelector(
    langFrom: Lang,
    langTo: Lang,
    onLangsChange: (Lang, Lang) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val langWidth = 100.dp
        LangDropdown(
            langFrom,
            textAlign = TextAlign.End,
            onSelected = { onLangsChange(it, langTo) },
            modifier = Modifier.width(langWidth)
        )
        IconButton(onClick = {
            onLangsChange(langTo, langFrom)
        }) {
            Icon(
                painterResource(R.drawable.langs_switch),
                contentDescription = stringResource(RL.string.home_cd_switch_langs),
            )
        }
        LangDropdown(
            langTo,
            textAlign = TextAlign.Start,
            onSelected = { onLangsChange(langFrom, it) },
            modifier = Modifier.width(langWidth)
        )
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
fun Preview() {
    val state = HomeScreenState(
        langFrom = Lang.DE,
        langTo = Lang.EN,
        query = "Hund",
    )
    LangampleTheme {
        HomeScreen(
            state,
            onQueryChange = {},
            onLangsChange = { _, _ -> },
            onSearch = { _, _, _ -> },
        )
    }
}
