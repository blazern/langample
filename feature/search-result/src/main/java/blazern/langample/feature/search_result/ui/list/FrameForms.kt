package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.ui.SourceLabel

@Composable
internal inline fun FrameForms(
    detailState: LexicalItemDetailState<*>,
    modifier: Modifier = Modifier,
    content: @Composable (textColor: Color)->Unit,
) {
    val (color, textColor) = if (detailState !is LexicalItemDetailState.Error) {
        MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }
    Box(
        modifier.background(color)
    ) {
        content(textColor)
        SourceLabel(detailState.source, textColor)
    }
}
