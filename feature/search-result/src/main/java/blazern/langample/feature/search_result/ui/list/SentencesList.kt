package blazern.langample.feature.search_result.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.langample.core.strings.R
import blazern.langample.core.ui.components.Expandable
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence

@Composable
internal fun SentencesList(
    sentences: List<Sentence>,
    contentColor: Color,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Box {
        Expandable(
            collapsedMaxHeight = 145.dp,
            control = { expanded, canExpand, onToggle ->
                if (canExpand) {
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                            contentDescription = if (expanded) {
                                stringResource(R.string.general_expand)
                            } else {
                                stringResource(R.string.general_collapse)
                            },
                            tint = contentColor,
                        )
                    }
                }
            },
        ) {
            FlowRow(modifier = modifier) {
                sentences.forEachIndexed { inx, sentence ->
                    Box(
                        modifier = Modifier
                            .clickable { callbacks.onTextCopy(sentence.text) }
                    ) {
                        Text(
                            sentence.text,
                            color = contentColor
                        )
                    }
                    if (inx != sentences.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                .clickable { callbacks.onTextCopy(sentence.text) }
                        ) {
                            Text(
                                "·",
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val sentences = listOf(
        Sentence("dog", Lang.DE, DataSource.KAIKKI),
        Sentence("hound", Lang.DE, DataSource.KAIKKI),
        Sentence("mutt", Lang.DE, DataSource.KAIKKI),
        Sentence("human's best friend", Lang.DE, DataSource.KAIKKI),
    )
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SentencesList(
                sentences,
                contentColor = MaterialTheme.colorScheme.onBackground,
                callbacks = LexicalItemDetailCallbacks.Stub,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
