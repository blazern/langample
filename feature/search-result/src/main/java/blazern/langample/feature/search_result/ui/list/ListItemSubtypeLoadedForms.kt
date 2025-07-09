package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.LexicalItemDetail

@Composable
internal fun ListItemSubtypeLoadedForms(
    forms: LexicalItemDetail.Forms,
    textColor: Color,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.clickable { callbacks.onTextCopy(forms.text) }
    ) {
        Text(
            forms.text,
            color = textColor,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
        )
    }
}
