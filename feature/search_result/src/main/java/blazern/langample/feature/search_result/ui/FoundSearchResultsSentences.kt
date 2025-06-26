package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.TranslationsSet

@Composable
internal fun FoundSearchResultsSentences(
    translations: List<TranslationsSet>,
    clipboard: Clipboard,
    onTextCopy: (String, Clipboard) -> Unit,
) {
    LazyColumn {
        items(translations.size) { index ->
            val example = translations[index]
            SentencesCard(
                sentences = listOf(
                    SentenceData(
                        sentence = example.original,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    *example.translations.map {
                        SentenceData(
                            sentence = it,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            textColor = MaterialTheme.colorScheme.onSecondary,
                        )
                    }.toTypedArray(),
                ),
                onSentenceClick = { onTextCopy(it.sentence.text, clipboard) },
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }
    }
}
