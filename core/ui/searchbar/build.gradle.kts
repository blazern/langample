plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.core.ui.searchbar"
}

dependencies {
    implementation(project(":core:ui:theme"))
}