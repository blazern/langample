package blazern.lexisoup.core.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun stringResource(
    resource: StringResource,
    preview: String = "Lorem ipsum",
): String {
    if (!LocalInspectionMode.current) {
        return stringResource(resource)
    }

    var text by remember(resource) { mutableStateOf<String?>(null) }
    LaunchedEffect(resource) {
        text = try { getString(resource) } catch (_: Throwable) { preview }
    }
    return text ?: preview
}
