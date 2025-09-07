package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.feature.search_result.ui.SourceLabel

@Composable
internal fun ListItemSubtypeLoadedExample(
    example: LexicalItemDetail.Example,
    callbacks: LexicalItemDetailCallbacks,
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
                MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.surfaceDim to MaterialTheme.colorScheme.onSurface
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