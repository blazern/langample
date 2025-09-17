plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.model.lexical_item_details_source.chatgpt"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    testImplementation(libs.apollo.testing)

    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:lexical-item-details-source:utils:cache"))
    implementation(project(":data:langample-graphql"))
    implementation(project(":core:utils"))
}
