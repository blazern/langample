package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.feature.search_result.model.LexicalItemDetailState

@Composable
internal fun FoundSearchResultsSentences(
    translations: List<LexicalItemDetailState<Example>>,
    onTextCopy: (String) -> Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
    onFixErrorRequest: (LexicalItemDetailState.Error<*>) -> Unit,
) {
    LazyColumn {
        items(translations.size) { index ->
            when (val example = translations[index]) {
                is LexicalItemDetailState.Error<Example> -> Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().clickable { onFixErrorRequest(example) },
                ) {
                    Text(example.exception.toString(), color = MaterialTheme.colorScheme.onError)
                }
                is LexicalItemDetailState.Loading<Example> -> {
                    onLoadingDetailVisible(example)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                is LexicalItemDetailState.Loaded<Example> -> {
                    SentencesCard(
                        sentences = listOf(
                            SentenceData(
                                sentence = example.detail.translationsSet.original,
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                textColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            *example.detail.translationsSet.translations.map {
                                SentenceData(
                                    sentence = it,
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                    textColor = MaterialTheme.colorScheme.onSecondary,
                                )
                            }.toTypedArray(),
                        ),
                        onSentenceClick = { onTextCopy(it.sentence.text) },
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                }
            }

        }
    }
}
