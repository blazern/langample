plugins {
    id("blazern.langample.plugin.feature")
}

android {
    namespace = "blazern.langample.feature.search_result"
}

dependencies {
    implementation(project(":core:ui:theme"))
    implementation(project(":core:ui:components"))
    implementation(project(":core:utils"))
    implementation(project(":data:lexical-item-details-source:aggregation"))
    implementation(kotlin("reflect"))
    testImplementation(project(":core:test-utils"))
    testImplementation(project(":data:lexical-item-details-source:utils:examples-tools"))
}
