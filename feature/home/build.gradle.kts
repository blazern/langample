plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.feature.home"
}

dependencies {
    implementation(project(":domain:model"))
    implementation(project(":core:ui:theme"))
    implementation(project(":core:ui:components"))
    implementation(project(":domain:settings"))
}