package blazern.lexisoup.feature.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import blazern.lexisoup.core.ui.components.SearchBar
import blazern.lexisoup.core.ui.strings.stringResource
import blazern.lexisoup.core.ui.theme.LangampleTheme
import blazern.lexisoup.core.ui.theme.LinkColor
import blazern.lexisoup.domain.model.Lang
import blazern.lexisoup.feature.home.SearchFn
import blazern.lexisoup.feature.home.model.HomeScreenState
import lexisoup.core.ui.strings.generated.resources.home_btn_search
import lexisoup.core.ui.strings.generated.resources.home_cd_clear_search_query
import lexisoup.core.ui.strings.generated.resources.home_cd_switch_langs
import lexisoup.core.ui.strings.generated.resources.home_input_hint
import org.jetbrains.compose.ui.tooling.preview.Preview
import lexisoup.core.ui.strings.generated.resources.Res as ResStr

@Suppress("LongParameterList")
@Composable
internal fun HomeScreen(
    state: HomeScreenState,
    onQueryChange: (query: String)->Unit,
    onLangsChange: (langFrom: Lang, langTo: Lang)->Unit,
    onSearch: SearchFn,
    onLocalhostToggled: (Boolean)->Unit,
    onPrivacyPolicyClick: ()->Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
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
                    placeholder = { Text(stringResource(ResStr.string.home_input_hint)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            onQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(ResStr.string.home_cd_clear_search_query),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onSearchWrapper(state.query) },
                    enabled = state.canSearch,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(ResStr.string.home_btn_search))
                }
            }

            if (state.isLocalhost != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Localhost")
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = state.isLocalhost,
                        onCheckedChange = { isChecked -> onLocalhostToggled(isChecked) }
                    )
                }
            }

            Text(
                text = "Privacy policy",
                color = LinkColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable { onPrivacyPolicyClick() }
            )
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
                IconSwitch,
                contentDescription = stringResource(ResStr.string.home_cd_switch_langs),
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

@Preview(name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun Preview() {
    val state = HomeScreenState(
        langFrom = Lang.DE,
        langTo = Lang.EN,
        query = "Hund",
        canSearch = true,
        isLocalhost = false,
    )
    LangampleTheme {
        HomeScreen(
            state = state,
            onQueryChange = {},
            onLangsChange = { _, _ -> },
            onSearch = { _, _, _ -> },
            onLocalhostToggled = {},
            onPrivacyPolicyClick = {},
        )
    }
}
