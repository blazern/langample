plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.data.panlex"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)

    api(project(":domain:model"))
    implementation(project(":core:ktor"))
}
