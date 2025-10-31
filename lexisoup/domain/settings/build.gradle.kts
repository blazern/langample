plugins {
    id("blazern.lexisoup.plugin.library")
}

kotlin {
    androidLibrary {
        namespace = "blazern.lexisoup.domain.settings"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:model"))
        }
    }
}
