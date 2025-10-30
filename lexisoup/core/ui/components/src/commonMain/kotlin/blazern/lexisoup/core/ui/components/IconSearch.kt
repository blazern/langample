package blazern.lexisoup.core.ui.components

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.addPathNodes

internal val IconSearch = materialIcon(name = "Filled.Search") {
    materialPath {
        addPathNodes(
            "M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 " +
                    "6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79" +
                    "l5 4.99L20.49 19l-4.99-5z m-5 0C8.01 14 6 11.99 6 9.5" +
                    "S8.01 5 10.5 5 15 7.01 15 9.5 12.99 14 10.5 14z"
        )
    }
}
