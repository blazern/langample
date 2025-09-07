package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.strRsc
import blazern.langample.domain.model.toType
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Loading
import blazern.langample.feature.search_result.ui.SourceLabel

@Composable
internal inline fun <reified D : LexicalItemDetail> FrameMain(
    detailState: LexicalItemDetailState<D>,
    modifier: Modifier = Modifier,
    content: @Composable (textColor: Color)->Unit,
) {
    val target = LexicalItemDetail.toType(D::class)
    val (color, textColor) = if (detailState !is LexicalItemDetailState.Error) {
        MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }
    Box(
        modifier.background(color)
    ) {
        Text(
            stringResource(target.strRsc),
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.5f),
            modifier = modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 4.dp),
        )
        content(textColor)
        SourceLabel(detailState.source, textColor)
    }
}

@Preview
@Composable
private fun Preview() {
    val target = LexicalItemDetail.Type.FORMS
    val source = DataSource.PANLEX
    FrameMain(
        Loading<Forms>(target, source),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Some content",
            modifier = Modifier.padding(16.dp)
        )
    }
}