plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.panlex"
}

dependencies {
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:lexical-item-details-source:utils:cache"))
    implementation(project(":core:utils"))
    implementation(project(":data:langample-graphql"))
    testImplementation(libs.apollo.testing)
}
