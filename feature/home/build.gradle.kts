plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.feature.home"
}

dependencies {
    implementation(project(":core:ui:theme"))
    implementation(project(":core:ui:searchbar"))
}