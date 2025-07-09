package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Error
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Loaded
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Loading
import blazern.langample.feature.search_result.ui.SourceLabel

/**
 * NOTE: if [detailState] is [LexicalItemDetailState.Loaded], the [Color.Transparent] color will
 * be set for both background and text, because the child is expected to set their
 * own colors.
 */
@Composable
internal fun FrameExample(
    detailState: LexicalItemDetailState<*>,
    modifier: Modifier = Modifier,
    content: @Composable (textColor: Color)->Unit,
) {
    val (frameColor, textColor) = when (detailState) {
        is Loading<*> -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        is Loaded<*> -> Color.Transparent to Color.Transparent
        is Error<*> -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }
    Box {
        Card(
            colors = CardDefaults.cardColors(containerColor = frameColor),
            modifier = modifier,
        ) {
            content(textColor)
        }
        if (detailState !is Loaded) {
            SourceLabel(detailState.source, textColor)
        }
    }
}
