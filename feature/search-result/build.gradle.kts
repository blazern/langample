plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.feature.search_result"
}

dependencies {
    implementation(project(":core:ui:theme"))
    implementation(project(":domain:lexical-item-details-source"))
}
