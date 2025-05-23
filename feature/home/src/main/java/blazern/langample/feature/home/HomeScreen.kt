package blazern.langample.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import blazern.langample.theme.LangampleTheme

@Composable
fun HomeScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            var text by rememberSaveable { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label") },
                singleLine = true,
            )
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
fun Preview() {
    LangampleTheme {
        HomeScreen()
    }
}