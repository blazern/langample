package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.LexicalItemDetail.Type.EXAMPLE
import blazern.langample.domain.model.LexicalItemDetail.Type.EXPLANATION
import blazern.langample.domain.model.LexicalItemDetail.Type.FORMS
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Error
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Loaded
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Loading
import blazern.langample.theme.LangampleTheme
import java.io.IOException

@Composable
internal inline fun <reified D : LexicalItemDetail> ListItem(
    detailState: LexicalItemDetailState<D>,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    when (detailState) {
        is Loading<*> -> ListItemSubtypeLoading<D>(detailState as Loading<D>, callbacks, modifier)
        is Loaded<*> -> ListItemSubtypeLoaded<D>(detailState as Loaded<D>, callbacks, modifier)
        is Error<*> -> ListItemSubtypeError<D>(detailState as Error<D>, callbacks, modifier)
    }
}

@Preview
@Composable
private fun FormsPreview() {
    val c = LexicalItemDetailCallbacks.Stub
    val m = Modifier.fillMaxWidth()
    val text = "der Hund, -e"
    LangampleTheme {
        Column {
            ListItem(Loading<Forms>(FORMS, DataSource.CHATGPT), c, m)
            ListItem(Loaded(Forms(text, DataSource.CHATGPT)), c, m)
            ListItem(Error<Forms>(IOException("No internet"), DataSource.CHATGPT), c, m)
        }
    }
}

@Preview
@Composable
private fun ExplanationPreview() {
    val c = LexicalItemDetailCallbacks.Stub
    val m = Modifier.fillMaxWidth()
    val text = "Hund is a dog, but here are a few other words to make the text longer"
    LangampleTheme {
        Column {
            ListItem(Loading<Explanation>(EXPLANATION, DataSource.CHATGPT), c, m)
            ListItem(Loaded(Explanation(text, DataSource.CHATGPT)), c, m)
            ListItem(Error<Explanation>(IOException("No internet"), DataSource.CHATGPT), c, m)
        }
    }
}

@Preview
@Composable
private fun ExamplesPreview() {
    val c = LexicalItemDetailCallbacks.Stub
    val m = Modifier.fillMaxWidth()
    val translations = TranslationsSet(
        Sentence("My dog", Lang.EN, DataSource.CHATGPT),
        listOf(
            Sentence("Mein Hund", Lang.DE, DataSource.CHATGPT),
            Sentence("Meine Hündin", Lang.DE, DataSource.CHATGPT),
            Sentence("Mein guter Hund", Lang.DE, DataSource.CHATGPT),
        ),
        listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
    )
    LangampleTheme {
        Column {
            ListItem(Loading<Example>(EXAMPLE, DataSource.TATOEBA), c, m)
            ListItem(Loaded(Example(translations, DataSource.TATOEBA)), c, m)
            ListItem(Error<Example>(IOException("No internet"), DataSource.TATOEBA), c, m)
        }
    }
}