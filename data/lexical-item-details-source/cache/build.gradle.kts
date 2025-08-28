plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.cache"
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":data:lexical-item-details-source:api"))
}
