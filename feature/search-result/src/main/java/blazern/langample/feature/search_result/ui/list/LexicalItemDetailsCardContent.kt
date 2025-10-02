package blazern.langample.feature.search_result.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import blazern.langample.core.strings.R
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.LexicalItemDetail.Synonyms
import blazern.langample.domain.model.LexicalItemDetail.WordTranslations
import blazern.langample.domain.model.Sentence

@Composable
internal fun LexicalItemDetailsCardContent(
    details: List<LexicalItemDetail>,
    source: DataSource,
    contentColor: Color,
    callbacks: LexicalItemDetailCallbacks,
) {
    val itemPaddings = PaddingValues(vertical = 18.dp)
    val header = selectHeader(details)
    val detailsFiltered = if (header != null && header.detailConsumed) {
        details.filter { it != header.sourceDetail }
    } else {
        details
    }
    Column(Modifier.fillMaxWidth()) {
        CardHeader(
            header?.text,
            source,
            callbacks,
            Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        Column(
            Modifier.padding(horizontal = 28.dp),
        ) {
            detailsFiltered.compose<Explanation> {
                Box {
                    Text(
                        it.text,
                        color = contentColor,
                        modifier = Modifier.padding(itemPaddings).clickable {
                            callbacks.onTextCopy(it.text)
                        }
                    )
                }
            }
            detailsFiltered.compose<Forms> {
                Box(Modifier.fillMaxWidth()) {
                    LexicalItemDetailForms(
                        it,
                        contentColor,
                        callbacks,
                        Modifier.padding(itemPaddings),
                    )
                    Label(
                        stringResource(R.string.general_lexical_item_detail_type_forms),
                        contentColor,
                    )
                }
            }
            detailsFiltered.compose<WordTranslations> {
                Box(Modifier.fillMaxWidth()) {
                    SentencesPart(
                        stringResource(R.string.general_lexical_item_detail_type_word_translations),
                        it.translationsSet.translations,
                        callbacks,
                        contentColor,
                        Modifier.padding(itemPaddings),
                    )
                }
            }
            detailsFiltered.compose<Synonyms> {
                Box(Modifier.fillMaxWidth()) {
                    SentencesPart(
                        stringResource(R.string.general_lexical_item_detail_type_synonyms),
                        it.translationsSet.translations,
                        callbacks,
                        contentColor,
                        Modifier.padding(itemPaddings),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SentencesPart(
    label: String,
    sentences: List<Sentence>,
    callbacks: LexicalItemDetailCallbacks,
    textColor: Color,
    modifier: Modifier,
) {
    SentencesList(
        sentences,
        textColor,
        callbacks,
        modifier,
    )
    Label(label, textColor)
}

@SuppressLint("ComposableNaming")
@Composable
private inline fun <reified T : LexicalItemDetail> List<LexicalItemDetail>.compose(
    fn: @Composable (T) -> Unit,
) {
    filterIsInstance<T>().forEach { fn(it) }
}

@Composable
private fun BoxScope.Label(
    text: String,
    mainColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = mainColor.copy(alpha = 0.2f),
        modifier = modifier
            .align(Alignment.TopStart)
            .padding(top = 4.dp),
    )
}
