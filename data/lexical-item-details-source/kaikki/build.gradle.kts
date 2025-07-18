plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.kaikki"
}

dependencies {
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:kaikki"))
    implementation(project(":core:utils"))
    implementation(project(":domain:settings"))
}
