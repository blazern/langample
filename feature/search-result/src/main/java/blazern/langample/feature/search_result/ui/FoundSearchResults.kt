package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.feature.search_result.ui.list.LexicalItemDetailCallbacks
import blazern.langample.feature.search_result.ui.list.ListItem
import blazern.langample.theme.LangampleTheme

@Composable
internal fun FoundSearchResults(
    state: SearchResultsState,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val listState = rememberLazyListState()
        val primaryItemsCount = remember(state) {
            state.forms.size +
                    state.wordTranslations.size +
                    state.synonyms.size +
                    state.explanations.size
        }
        val primary = MaterialTheme.colorScheme.surface
        val background = MaterialTheme.colorScheme.surfaceContainer

        LazyColumn(
            state = listState,
            // Since all items except for examples fill entire width, and all
            // items appear and disappear with animation, we want to draw
            // the primary color behind the top items, so that when they are animated
            // there's no white background behind them.
            // At the same time, we want to draw [MaterialTheme.colorScheme.background] behind
            // examples, because it is their normal background.
            modifier = modifier.drawBehind {
                val width = size.width
                listState.layoutInfo.visibleItemsInfo.forEach { info ->
                    val background = if (info.index < primaryItemsCount) primary else background
                    drawRect(
                        color = background,
                        topLeft = Offset(0f, info.offset.toFloat()),
                        size = Size(width, info.size.toFloat())
                    )
                }
            }
        ) {
            lexicalDetailsItems(state.forms, callbacks)
            lexicalDetailsItems(state.wordTranslations, callbacks)
            lexicalDetailsItems(state.synonyms, callbacks)
            lexicalDetailsItems(state.explanations, callbacks)
            lexicalDetailsItems(
                state.examples,
                callbacks,
                last = true,
                Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp),
            )
        }
    }
}

private inline fun <reified D : LexicalItemDetail> LazyListScope.lexicalDetailsItems(
    items: List<LexicalItemDetailState<D>>,
    callbacks: LexicalItemDetailCallbacks,
    last: Boolean = false,
    modifier: Modifier = Modifier,
) {
    items(items.size, key = { items[it].id }) { index ->
        Box(modifier = modifier.animateItem().then(
            if (last && index == items.size - 1) {
                Modifier.navigationBarsPadding()
            } else {
                Modifier
            }
        )) {
            ListItem(
                items[index],
                callbacks,
                Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun Preview() {
    val state = SearchResultsState(
        forms = listOf(LexicalItemDetailState.Loaded(Forms("der Hund, -e", DataSource.CHATGPT))),
        explanations = listOf(LexicalItemDetailState.Loaded(Explanation("Hund is Dog", DataSource.CHATGPT))),
        examples = listOf(
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("The dog barks", Lang.EN, DataSource.TATOEBA),
                    listOf(Sentence("Der Hund bellt", Lang.DE, DataSource.TATOEBA)),
                    listOf(TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )),
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("The dog sits", Lang.EN, DataSource.CHATGPT),
                    listOf(
                        Sentence("Der Hund sitzt", Lang.DE, DataSource.CHATGPT),
                        Sentence("Собака сидит", Lang.RU, DataSource.CHATGPT),
                    ),
                    listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )),
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("A dog sits on a sofa and looks at me", Lang.EN, DataSource.CHATGPT),
                    listOf(Sentence(
                        "Ein Hund sitzt auf dem Sofa und guckt mich an",
                        Lang.DE,
                        DataSource.CHATGPT,
                    )),
                    listOf(TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            ))
        ),
    )
    LangampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FoundSearchResults(
                state = state,
                callbacks = LexicalItemDetailCallbacks.Stub,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
