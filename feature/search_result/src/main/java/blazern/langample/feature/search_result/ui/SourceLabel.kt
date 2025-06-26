package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.strRsc

@Composable
internal fun BoxScope.SourceLabel(
    source: DataSource,
    mainColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        stringResource(source.strRsc),
        style = MaterialTheme.typography.labelSmall,
        color = mainColor.copy(alpha = 0.2f),
        modifier = modifier
            .align(Alignment.TopEnd)
            .padding(end = 8.dp, top = 4.dp),
    )
}
