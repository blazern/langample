plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.core.ui.components"
}

dependencies {
    implementation(project(":core:ui:theme"))
}