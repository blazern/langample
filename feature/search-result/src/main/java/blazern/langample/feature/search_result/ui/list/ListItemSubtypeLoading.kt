package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.toType
import blazern.langample.feature.search_result.model.LexicalItemDetailState

@Composable
internal inline fun <reified D : LexicalItemDetail> ListItemSubtypeLoading(
    loading: LexicalItemDetailState.Loading<*>,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    callbacks.onLoadingDetailVisible(loading)
    when (LexicalItemDetail.toType(D::class)) {
        LexicalItemDetail.Type.FORMS -> FrameForms(loading) {
            LoadingContent(modifier)
        }
        LexicalItemDetail.Type.EXPLANATION -> FrameForms(loading) {
            LoadingContent(modifier)
        }
        LexicalItemDetail.Type.EXAMPLE -> FrameExample(loading) {
            LoadingContent(modifier)
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        LinearProgressIndicator()
    }
}
