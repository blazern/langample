plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.wortschatz_leipzig"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.ktor.client.core)
    api(project(":domain:model"))
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:lexical-item-details-source:cache"))
    implementation(project(":data:lexical-item-details-source:kaikki"))
    implementation(project(":core:ktor"))
    testImplementation(project(":core:utils"))
}
