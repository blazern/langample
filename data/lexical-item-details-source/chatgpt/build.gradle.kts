plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.model.lexical_item_details_source.chatgpt"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    api(project(":domain:lexical-item-details-source"))
    implementation(project(":data:chatgpt"))
}
