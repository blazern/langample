plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.feature.home"
}

dependencies {
    implementation(project(":core:theme"))
}