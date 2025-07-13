package blazern.langample.feature.search_result.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.toType
import blazern.langample.feature.search_result.model.LexicalItemDetailState

@Composable
internal inline fun <reified D : LexicalItemDetail> ListItemSubtypeLoaded(
    loaded: LexicalItemDetailState.Loaded<D>,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    when (LexicalItemDetail.toType(D::class)) {
        LexicalItemDetail.Type.FORMS -> {
            FrameMain(loaded) { textColor ->
                ListItemSubtypeLoadedForms(
                    loaded.detail as LexicalItemDetail.Forms,
                    textColor,
                    callbacks,
                    modifier,
                )
            }
        }
        LexicalItemDetail.Type.WORD_TRANSLATIONS -> {
            FrameMain(loaded) { textColor ->
                ListItemSubtypeLoadedWordTranslations(
                    loaded.detail as LexicalItemDetail.WordTranslations,
                    textColor,
                    callbacks,
                    modifier,
                )
            }
        }
        LexicalItemDetail.Type.SYNONYMS -> {
            FrameMain(loaded) { textColor ->
                ListItemSubtypeLoadedSynonyms(
                    loaded.detail as LexicalItemDetail.Synonyms,
                    textColor,
                    callbacks,
                    modifier,
                )
            }
        }
        LexicalItemDetail.Type.EXPLANATION -> {
            FrameMain(loaded) { textColor ->
                ListItemSubtypeLoadedExplanation(
                    loaded.detail as LexicalItemDetail.Explanation,
                    textColor,
                    callbacks,
                    modifier,
                )
            }
        }
        LexicalItemDetail.Type.EXAMPLE -> {
            FrameExample(loaded) {
                ListItemSubtypeLoadedExample(
                    loaded.detail as LexicalItemDetail.Example,
                    callbacks,
                    modifier,
                )
            }
        }
    }
}
