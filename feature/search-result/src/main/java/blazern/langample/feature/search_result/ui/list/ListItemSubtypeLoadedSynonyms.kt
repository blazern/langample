package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence

@Composable
internal fun ListItemSubtypeLoadedSynonyms(
    synonyms: LexicalItemDetail.Synonyms,
    textColor: Color,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    ListItemSubtypeLoadedSentences(
        synonyms.translationsSet.translations,
        textColor,
        callbacks,
        modifier,
    )
}

@Composable
internal fun ListItemSubtypeLoadedSentences(
    sentences: List<Sentence>,
    textColor: Color,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Box {
        FlowRow(modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            for (sentence in sentences) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { callbacks.onTextCopy(sentence.text) }
                ) {
                    Text(
                        sentence.text,
                        color = textColor
                    )
                }
            }
        }
    }
}
