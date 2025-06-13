plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.data.tatoeba"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.ktor.client.core)

    implementation(project(":core:ktor"))
}
