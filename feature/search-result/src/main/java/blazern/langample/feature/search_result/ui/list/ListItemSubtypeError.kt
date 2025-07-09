package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.toType
import blazern.langample.feature.search_result.model.LexicalItemDetailState

@Composable
internal inline fun <reified D : LexicalItemDetail> ListItemSubtypeError(
    error: LexicalItemDetailState.Error<*>,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    when (LexicalItemDetail.toType(D::class)) {
        LexicalItemDetail.Type.FORMS -> FrameForms(error) {
            ErrorContent(error, it, callbacks, modifier)
        }
        LexicalItemDetail.Type.EXPLANATION -> FrameForms(error) {
            ErrorContent(error, it, callbacks, modifier)
        }
        LexicalItemDetail.Type.EXAMPLE -> FrameExample(error) {
            ErrorContent(error, it, callbacks, modifier)
        }
    }
}

@Composable
private fun ErrorContent(
    error: LexicalItemDetailState.Error<*>,
    textColor: Color,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    val errorText = error.exception.toString()
    Box(modifier = modifier
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .clickable { callbacks.onFixErrorRequest(error) }
    ) {
        Text(errorText, color = textColor)
    }
}
