package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.WordForm
import blazern.langample.domain.model.WordForm.Tag.Defined.Genitive
import blazern.langample.domain.model.WordForm.Tag.Defined.Nominative
import blazern.langample.domain.model.WordForm.Tag.Defined.Plural
import blazern.langample.domain.model.WordForm.Tag.Defined.Singular
import blazern.langample.feature.search_result.model.LexicalItemDetailsGroupState


internal sealed interface ExampleState {
    data class Loaded(val example: LexicalItemDetail.Example) : ExampleState
    data class Loading(val loading: LexicalItemDetailsGroupState.Loading) : ExampleState
    data class Error(val error: LexicalItemDetailsGroupState.Error) : ExampleState
}

@Composable
internal fun ExampleCard(
    state: ExampleState,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    val cardColors = if (state !is ExampleState.Error) {
        CardDefaults.cardColors()
    } else {
        CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        )
    }
    Card(
        colors = cardColors,
        modifier = modifier,
    ) {
        when (state) {
            is ExampleState.Loaded -> {
                val bg1 = MaterialTheme.colorScheme.surfaceContainerLowest
                val bg2 = MaterialTheme.colorScheme.surfaceContainerLow
                ExampleContent(
                    example = state.example,
                    contentColor = cardColors.contentColor,
                    backgroundColor1 = bg1,
                    backgroundColor2 = bg2,
                    callbacks = callbacks,
                )
            }
            is ExampleState.Error -> ErrorContent(
                state.error,
                cardColors.contentColor,
                callbacks,
            )
            is ExampleState.Loading -> {
                callbacks.onLoadingDetailVisible(state.loading)
                LoadingContent(state.loading.source, callbacks)
            }
        }
    }
}

@Composable
internal fun ExampleContent(
    example: LexicalItemDetail.Example,
    callbacks: LexicalItemDetailCallbacks,
    contentColor: Color,
    backgroundColor1: Color,
    backgroundColor2: Color,
    modifier: Modifier = Modifier,
) {
    val sentences = mutableListOf<Sentence>()
    sentences += example.translationsSet.original
    example.translationsSet.translations.forEach {
        sentences += it
    }
    Column(modifier) {
        sentences.forEachIndexed { index, sentence ->
            val (color, textColor) = if (index == 0) {
                backgroundColor1 to contentColor
            } else {
                backgroundColor2 to contentColor
            }
            SentenceLine(
                sentence,
                color,
                textColor,
                callbacks,
            )
        }
    }
}

@Composable
private fun SentenceLine(
    sentence: Sentence,
    color: Color,
    textColor: Color,
    callbacks: LexicalItemDetailCallbacks,
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .heightIn(min = 40.dp)
            .fillMaxWidth()
            .background(color)
            .clickable { callbacks.onTextCopy(sentence.text) },
    ) {
        Text(
            sentence.text,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 14.dp,
            )
        )
        SourceLabel(sentence.source, textColor)
    }
}


@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun PreviewAll() {
    val examples1 = listOf(
        LexicalItemDetail.Example(
            TranslationsSet(
                Sentence("Hund", Lang.EN, DataSource.KAIKKI),
                listOf(Sentence("dog", Lang.DE, DataSource.KAIKKI)),
                listOf(TranslationsSet.QUALITY_MAX),
            ),
            DataSource.KAIKKI,
        ),
        LexicalItemDetail.Example(
            TranslationsSet(
                Sentence("Der Hund sitzt", Lang.EN, DataSource.KAIKKI),
                listOf(Sentence("the dog sits", Lang.DE, DataSource.KAIKKI)),
                listOf(TranslationsSet.QUALITY_MAX),
            ),
            DataSource.KAIKKI,
        ),
    )

    val examples2 = listOf(
        LexicalItemDetail.Example(
            TranslationsSet(
                Sentence("Mein Lieblingshund", Lang.EN, DataSource.CHATGPT),
                listOf(Sentence("my favorite dog", Lang.DE, DataSource.CHATGPT)),
                listOf(TranslationsSet.QUALITY_MAX),
            ),
            DataSource.CHATGPT,
        ),
    )

    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    examples1.forEach {
                        ExampleCard(ExampleState.Loaded(it), LexicalItemDetailCallbacks.Stub)
                    }
                    ExampleCard(
                        ExampleState.Loading(
                            LexicalItemDetailsGroupState.Loading(
                                id = "1",
                                setOf(LexicalItemDetail.Type.EXAMPLE),
                                DataSource.TATOEBA,
                            ),
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                    examples2.forEach {
                        ExampleCard(ExampleState.Loaded(it), LexicalItemDetailCallbacks.Stub)
                    }
                    ExampleCard(
                        ExampleState.Error(
                            LexicalItemDetailsGroupState.Error(
                                id = "2",
                                Err.Other(null),
                                setOf(LexicalItemDetail.Type.EXAMPLE),
                                DataSource.TATOEBA,
                            ),
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                }
            }
        }
    }
}