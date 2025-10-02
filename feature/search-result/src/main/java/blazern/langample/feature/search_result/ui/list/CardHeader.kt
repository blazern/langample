package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.strRsc

@Suppress("MagicNumber")
@Composable
internal fun CardHeader(
    headerText: String?,
    source: DataSource,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Row(modifier) {
        if (headerText != null) {
            Text(
                headerText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.weight(0.8f).clickable {
                    callbacks.onTextCopy(headerText)
                }
            )
        } else {
            Spacer(Modifier.weight(0.8f))
        }
        Text(
            stringResource(source.strRsc),
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.2f),
        )
    }
}

internal data class SelectedHeader(
    val text: String,
    val sourceDetail: LexicalItemDetail,
    val detailConsumed: Boolean,
)

internal fun selectHeader(details: List<LexicalItemDetail>): SelectedHeader? {
    val forms = details.filterIsInstance<Forms>().firstOrNull()
    if (forms != null) {
        val value = forms.value
        when (value) {
            is Forms.Value.Text -> return SelectedHeader(
                text = value.text,
                sourceDetail = forms,
                detailConsumed = true,
            )
            is Forms.Value.Detailed -> {
                if (value.forms.isNotEmpty()) {
                    return SelectedHeader(
                        text = value.forms
                            .sortedBy { it.importance }
                            .asReversed()
                            .first().text,
                        sourceDetail = forms,
                        detailConsumed = false,
                    )
                }
            }
        }
    }
    return null
}
