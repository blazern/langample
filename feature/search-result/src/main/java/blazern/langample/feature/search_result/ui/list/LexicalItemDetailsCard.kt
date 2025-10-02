package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.LexicalItemDetail.Synonyms
import blazern.langample.domain.model.LexicalItemDetail.WordTranslations
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.WordForm
import blazern.langample.domain.model.WordForm.Tag.Defined.Genitive
import blazern.langample.domain.model.WordForm.Tag.Defined.Nominative
import blazern.langample.domain.model.WordForm.Tag.Defined.Plural
import blazern.langample.domain.model.WordForm.Tag.Defined.Singular
import blazern.langample.feature.search_result.model.LexicalItemDetailsGroupState

@Composable
internal fun LexicalItemDetailsCard(
    state: LexicalItemDetailsGroupState,
    callbacks: LexicalItemDetailCallbacks,
) {
    val defaultColors = CardDefaults.cardColors()
    val isError = state is LexicalItemDetailsGroupState.Error
    val containerColor = if (isError) MaterialTheme.colorScheme.error else defaultColors.containerColor
    val contentColor = if (isError) MaterialTheme.colorScheme.onError else defaultColors.contentColor
    val containerColorAnimated by animateColorAsState(containerColor)
    val contentColorAnimated by animateColorAsState(contentColor)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColorAnimated,
            contentColor = contentColorAnimated,
        ),
        modifier = Modifier.animateContentSize(animationSpec = spring()),
    ) {
        if (state is LexicalItemDetailsGroupState.Loading) {
            LaunchedEffect(state.id) {
                callbacks.onLoadingDetailVisible(state)
            }
        }

        Crossfade(targetState = state, label = "lexicalItemState") { target ->
            when (target) {
                is LexicalItemDetailsGroupState.Loaded -> {
                    LexicalItemDetailsCardContent(
                        target.details,
                        target.source,
                        contentColorAnimated,
                        callbacks,
                    )
                }
                is LexicalItemDetailsGroupState.Loading -> {
                    LoadingContent(target.source, callbacks)
                }
                is LexicalItemDetailsGroupState.Error -> {
                    ErrorContent(
                        target,
                        contentColorAnimated,
                        callbacks,
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun PreviewCards() {
    val translations = TranslationsSet(
        Sentence("Hund", Lang.EN, DataSource.KAIKKI),
        listOf(
            Sentence("dog", Lang.DE, DataSource.KAIKKI),
            Sentence("hound", Lang.DE, DataSource.KAIKKI),
            Sentence("mutt", Lang.DE, DataSource.KAIKKI),
            Sentence("human's best friend", Lang.DE, DataSource.KAIKKI),
        ),
        listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
    )
    val synonyms = TranslationsSet(
        Sentence("Hund", Lang.EN, DataSource.KAIKKI),
        listOf(
            Sentence("Hündin", Lang.DE, DataSource.KAIKKI),
            Sentence("Wauwau", Lang.DE, DataSource.KAIKKI),
        ),
        listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
    )
    val fancyForms = Forms(
        value = Forms.Value.Detailed(listOf(
            WordForm("der Hund", listOf(Singular(""), Nominative("")), Lang.DE),
            WordForm("die Hunde", listOf(Plural(""), Nominative("")), Lang.DE),
            WordForm("des Hundes", listOf(Singular(""), Genitive("")), Lang.DE),
            WordForm("des Hunds", listOf(Singular(""), Genitive("")), Lang.DE),
        )),
        source = DataSource.KAIKKI,
    )

    val explanation = """
        Hund means dog, it's a common domestic animal loved by many as a pet.
        Dogs are known for being loyal and are often kept as companionship or work"""
        .trimIndent()
        .replace(Regex("\n"), " ")
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Loaded(
                            id = "1",
                            listOf(
                                Explanation(explanation, DataSource.KAIKKI),
                                Forms(Forms.Value.Text("der Hund, -e"), DataSource.KAIKKI),
                                WordTranslations(translations, DataSource.KAIKKI),
                                Synonyms(synonyms, DataSource.KAIKKI),
                            ),
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Loaded(
                            id = "2",
                            listOf(
                                WordTranslations(translations, DataSource.KAIKKI),
                                Synonyms(synonyms, DataSource.KAIKKI),
                            ),
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Loaded(
                            id = "3",
                            listOf(
                                fancyForms,
                                Explanation(explanation, DataSource.KAIKKI),
                                WordTranslations(translations, DataSource.KAIKKI),
                                Synonyms(synonyms, DataSource.KAIKKI),
                            ),
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun PreviewAll() {
    val translations = TranslationsSet(
        Sentence("Hund", Lang.EN, DataSource.KAIKKI),
        listOf(
            Sentence("dog", Lang.DE, DataSource.KAIKKI),
            Sentence("hound", Lang.DE, DataSource.KAIKKI),
            Sentence("mutt", Lang.DE, DataSource.KAIKKI),
            Sentence("human's best friend", Lang.DE, DataSource.KAIKKI),
        ),
        listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
    )
    val synonyms = TranslationsSet(
        Sentence("Hund", Lang.EN, DataSource.KAIKKI),
        listOf(
            Sentence("Hündin", Lang.DE, DataSource.KAIKKI),
            Sentence("Wauwau", Lang.DE, DataSource.KAIKKI),
        ),
        listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
    )
    val fancyForms = Forms(
        value = Forms.Value.Detailed(listOf(
            WordForm("der Hund", listOf(Singular(""), Nominative("")), Lang.DE),
            WordForm("die Hunde", listOf(Plural(""), Nominative("")), Lang.DE),
            WordForm("des Hundes", listOf(Singular(""), Genitive("")), Lang.DE),
            WordForm("des Hunds", listOf(Singular(""), Genitive("")), Lang.DE),
        )),
        source = DataSource.KAIKKI,
    )

    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Loaded(
                            id = "1",
                            listOf(
                                Forms(Forms.Value.Text("der Hund, -e"), DataSource.KAIKKI),
                                WordTranslations(translations, DataSource.KAIKKI),
                                Synonyms(synonyms, DataSource.KAIKKI),
                            ),
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Loading(
                            id = "2",
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                    LexicalItemDetailsCard(
                        LexicalItemDetailsGroupState.Error(
                            id = "3",
                            Err.Other(null),
                            LexicalItemDetail.Type.entries.toSet(),
                            DataSource.KAIKKI,
                        ),
                        LexicalItemDetailCallbacks.Stub,
                    )
                }
            }
        }
    }
}