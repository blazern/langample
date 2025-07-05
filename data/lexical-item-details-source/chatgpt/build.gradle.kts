plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.model.lexical_item_details_source.chatgpt"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:chatgpt"))
    implementation(project(":core:utils"))
}
