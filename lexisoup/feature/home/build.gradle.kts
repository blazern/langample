plugins {
    id("blazern.lexisoup.plugin.feature")
}

kotlin {
    androidLibrary {
        namespace = "blazern.lexisoup.feature.home"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:model"))
            implementation(project(":domain:settings"))
            implementation(project(":core:ui:theme"))
            implementation(project(":core:ui:strings"))
            implementation(project(":core:ui:components"))
        }
    }
}
