package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.strRsc
import blazern.langample.theme.LangampleTheme

internal data class SentenceData(
    val sentence: Sentence,
    val backgroundColor: Color,
    val textColor: Color = Color.Unspecified,
)

@Composable
internal fun SentencesCard(
    sentences: List<SentenceData>,
    onSentenceClick: (SentenceData)->Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        // let the inner layers supply the colors
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier,
    ) {
        Column {
            for (sentenceData in sentences) {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .fillMaxWidth()
                        .background(sentenceData.backgroundColor)
                        .clickable { onSentenceClick(sentenceData) },
                ) {
                    Text(
                        sentenceData.sentence.text,
                        color = sentenceData.textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 14.dp,
                        )
                    )
                    SourceLabel(sentenceData.sentence.source, sentenceData.textColor)
                }
            }
        }
    }
}

@Preview
@Composable
fun Sentences2CardPreview() {
    LangampleTheme {
        SentencesCard(
            listOf(
                SentenceData(
                    Sentence(
                        "Ein Hund sitzt auf dem Sofa und guckt mich an während ich esse",
                        Lang.DE,
                        DataSource.CHATGPT,
                    ),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                ),
                SentenceData(
                    Sentence(
                        "A dog sits on a sofa and looks at me while I eat",
                        Lang.EN,
                        DataSource.TATOEBA,
                    ),
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onSecondary,
                ),
                SentenceData(
                    Sentence(
                        "Пёс сидит на диване и глядит как я ем",
                        Lang.RU,
                        DataSource.CHATGPT,
                    ),
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onSecondary,
                ),
            ),
            modifier = Modifier,
        )
    }
}

@Preview
@Composable
fun Sentences3CardPreview() {
    LangampleTheme {
        SentencesCard(
            listOf(
                SentenceData(
                    Sentence(
                        "One",
                        Lang.EN,
                        DataSource.TATOEBA,
                    ),
                    Color.Red
                ),
                SentenceData(
                    Sentence(
                        "Two",
                        Lang.EN,
                        DataSource.CHATGPT,
                    ),
                    Color.Green
                ),
                SentenceData(
                    Sentence(
                        "Three",
                        Lang.EN,
                        DataSource.TATOEBA,
                    ),
                    Color.Blue
                ),
            ),
            modifier = Modifier.width(256.dp),
        )
    }
}
