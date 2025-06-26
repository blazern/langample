plugins {
    id("blazern.langample.plugin.feature")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.feature.search_result"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":core:ui:theme"))
    implementation(project(":data:tatoeba"))
    implementation(project(":data:chatgpt"))
}
