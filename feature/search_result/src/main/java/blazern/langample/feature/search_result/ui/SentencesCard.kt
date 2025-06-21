package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.langample.theme.LangampleTheme

internal data class SentenceData(
    val text: String,
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
                        .heightIn(min = 48.dp)
                        .fillMaxWidth()
                        .background(sentenceData.backgroundColor)
                        .clickable { onSentenceClick(sentenceData) },
                ) {
                    Text(
                        sentenceData.text,
                        color = sentenceData.textColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
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
                    "Ein Hund sitzt auf dem Sofa und guckt mich an während ich esse",
                    MaterialTheme.colorScheme.primaryContainer,
                    textColor = Color.DarkGray,
                ),
                SentenceData(
                    "A dog sits on a sofa and looks at me while I eat",
                    MaterialTheme.colorScheme.secondaryContainer,
                    textColor = Color.Black,
                ),
            ),
            modifier = Modifier.width(256.dp),
        )
    }
}

@Preview
@Composable
fun Sentences3CardPreview() {
    LangampleTheme {
        SentencesCard(
            listOf(
                SentenceData("One", Color.Red),
                SentenceData("Two", Color.Green),
                SentenceData("Three", Color.Blue),
            ),
            modifier = Modifier,
        )
    }
}
